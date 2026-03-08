#!/bin/bash
# ============================================================
# 农家乐线上预约管理系统 - Mac 一键启动脚本
# ============================================================

set -e

# ---------- 颜色定义 ----------
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# ---------- 项目路径 ----------
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
ADMIN_DIR="$SCRIPT_DIR/admin"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
SQL_DIR="$SCRIPT_DIR/sql"

# ---------- 配置 ----------
DB_NAME="farmstay_db"
DB_USER="root"
DB_PASS="ab123168"
BACKEND_PORT=8090
ADMIN_PORT=8848
FRONTEND_PORT=5173

# ---------- 日志文件 ----------
LOG_DIR="$SCRIPT_DIR/.logs"
mkdir -p "$LOG_DIR"
BACKEND_LOG="$LOG_DIR/backend.log"
ADMIN_LOG="$LOG_DIR/admin.log"
FRONTEND_LOG="$LOG_DIR/frontend.log"

# ---------- PID 数组 ----------
PIDS=()

# =============================================================
# 工具函数
# =============================================================
print_banner() {
    echo ""
    echo -e "${CYAN}${BOLD}╔══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}${BOLD}║        🌾 农家乐线上预约管理系统 - 一键启动脚本        ║${NC}"
    echo -e "${CYAN}${BOLD}╚══════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

info()    { echo -e "${BLUE}[INFO]${NC}  $1"; }
success() { echo -e "${GREEN}[✓]${NC}     $1"; }
warn()    { echo -e "${YELLOW}[⚠]${NC}     $1"; }
error()   { echo -e "${RED}[✗]${NC}     $1"; }
step()    { echo -e "\n${MAGENTA}${BOLD}▸ $1${NC}"; }

# =============================================================
# 0. 优雅退出：Ctrl+C 关闭所有子进程
# =============================================================
cleanup() {
    echo ""
    warn "正在关闭所有服务..."
    for pid in "${PIDS[@]}"; do
        if kill -0 "$pid" 2>/dev/null; then
            kill -TERM "$pid" 2>/dev/null
            wait "$pid" 2>/dev/null
        fi
    done
    # 清理可能残留的子进程组
    kill 0 2>/dev/null || true
    success "所有服务已关闭，再见！"
    exit 0
}
trap cleanup SIGINT SIGTERM

# =============================================================
# 1. 环境检测与自动安装
# =============================================================
check_and_install() {
    local cmd="$1"
    local name="$2"
    local install_cmd="$3"

    if command -v "$cmd" &>/dev/null; then
        local ver
        ver=$("$cmd" --version 2>&1 | head -n1)
        success "$name 已安装: $ver"
    else
        warn "$name 未检测到，正在安装..."
        eval "$install_cmd"
        if command -v "$cmd" &>/dev/null; then
            success "$name 安装成功"
        else
            error "$name 安装失败，请手动安装后重新运行脚本"
            exit 1
        fi
    fi
}

check_homebrew() {
    if ! command -v brew &>/dev/null; then
        warn "Homebrew 未检测到，正在安装..."
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        # Apple Silicon 路径
        if [ -f "/opt/homebrew/bin/brew" ]; then
            eval "$(/opt/homebrew/bin/brew shellenv)"
        fi
        success "Homebrew 安装成功"
    else
        success "Homebrew 已安装"
    fi
}

check_env() {
    step "检测运行环境"

    check_homebrew

    check_and_install "java" "Java" "brew install openjdk@17 && sudo ln -sfn \$(brew --prefix openjdk@17)/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk"
    check_and_install "mvn" "Maven" "brew install maven"
    check_and_install "node" "Node.js" "brew install node@20"
    check_and_install "pnpm" "pnpm" "npm install -g pnpm"
    check_and_install "mysql" "MySQL Client" "brew install mysql"

    # 检查 MySQL 服务是否运行
    if ! mysqladmin ping -u"$DB_USER" -p"$DB_PASS" --silent 2>/dev/null; then
        warn "MySQL 服务未运行，正在尝试启动..."
        brew services start mysql 2>/dev/null || mysql.server start 2>/dev/null || true
        sleep 3
        if ! mysqladmin ping -u"$DB_USER" -p"$DB_PASS" --silent 2>/dev/null; then
            error "MySQL 服务启动失败，请手动启动 MySQL 后重新运行脚本"
            exit 1
        fi
        success "MySQL 服务已启动"
    else
        success "MySQL 服务正在运行"
    fi
}

# =============================================================
# 2. 数据库检测与初始化
# =============================================================
check_database() {
    step "检测数据库"

    local db_exists
    db_exists=$(mysql -u"$DB_USER" -p"$DB_PASS" -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='$DB_NAME'" -sN 2>/dev/null)

    if [ -n "$db_exists" ]; then
        success "数据库 '$DB_NAME' 已存在，跳过初始化"
    else
        warn "数据库 '$DB_NAME' 不存在，正在初始化..."

        if [ ! -f "$SQL_DIR/init.sql" ]; then
            error "找不到 sql/init.sql 文件"
            exit 1
        fi
        if [ ! -f "$SQL_DIR/data.sql" ]; then
            error "找不到 sql/data.sql 文件"
            exit 1
        fi

        info "执行 init.sql (建库建表)..."
        mysql -u"$DB_USER" -p"$DB_PASS" < "$SQL_DIR/init.sql"
        success "数据库结构创建完成"

        info "执行 data.sql (导入测试数据)..."
        mysql -u"$DB_USER" -p"$DB_PASS" < "$SQL_DIR/data.sql"
        success "测试数据导入完成"
    fi
}

# =============================================================
# 3. 端口检测与释放
# =============================================================
check_port() {
    local port=$1
    local service=$2
    local pid
    pid=$(lsof -ti :"$port" 2>/dev/null || true)

    if [ -n "$pid" ]; then
        warn "端口 $port ($service) 被进程 PID=$pid 占用，正在释放..."
        kill -9 $pid 2>/dev/null || true
        sleep 1
        success "端口 $port 已释放"
    else
        success "端口 $port ($service) 空闲"
    fi
}

check_ports() {
    step "检测端口占用"
    check_port $BACKEND_PORT  "后端"
    check_port $ADMIN_PORT    "管理端"
    check_port $FRONTEND_PORT "用户端"
}

# =============================================================
# 4. 前端依赖安装
# =============================================================
install_deps() {
    step "检测前端依赖"

    if [ ! -d "$ADMIN_DIR/node_modules" ]; then
        warn "管理端 node_modules 不存在，正在安装依赖..."
        (cd "$ADMIN_DIR" && pnpm install)
        success "管理端依赖安装完成"
    else
        success "管理端依赖已就绪"
    fi

    if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
        warn "用户端 node_modules 不存在，正在安装依赖..."
        (cd "$FRONTEND_DIR" && pnpm install)
        success "用户端依赖安装完成"
    else
        success "用户端依赖已就绪"
    fi
}

# =============================================================
# 5. 启动服务
# =============================================================
start_services() {
    step "启动所有服务"

    # 清空旧日志
    > "$BACKEND_LOG"
    > "$ADMIN_LOG"
    > "$FRONTEND_LOG"

    # --- 后端 ---
    info "启动后端服务 (端口 $BACKEND_PORT)..."
    (cd "$BACKEND_DIR" && mvn spring-boot:run -q 2>&1 | tee -a "$BACKEND_LOG") &
    PIDS+=($!)

    # --- 管理端 ---
    info "启动管理端 (端口 $ADMIN_PORT)..."
    (cd "$ADMIN_DIR" && pnpm dev 2>&1 | tee -a "$ADMIN_LOG") &
    PIDS+=($!)

    # --- 用户端 ---
    info "启动用户端 (端口 $FRONTEND_PORT)..."
    (cd "$FRONTEND_DIR" && pnpm dev 2>&1 | tee -a "$FRONTEND_LOG") &
    PIDS+=($!)

    # 等待服务启动
    info "等待服务启动中..."
    sleep 8
}

# =============================================================
# 6. 信息面板
# =============================================================
print_info_panel() {
    echo ""
    echo -e "${CYAN}${BOLD}╔══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}${BOLD}║                 🚀 所有服务已启动                        ║${NC}"
    echo -e "${CYAN}${BOLD}╠══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${CYAN}║${NC}                                                          ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ${BOLD}📡 服务地址${NC}                                            ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ├─ 后端 API: ${GREEN}http://localhost:${BACKEND_PORT}${NC}                  ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ├─ 管理端:   ${GREEN}http://localhost:${ADMIN_PORT}${NC}                  ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  └─ 用户端:   ${GREEN}http://localhost:${FRONTEND_PORT}${NC}                  ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}                                                          ${CYAN}║${NC}"
    echo -e "${CYAN}╠══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${CYAN}║${NC}                                                          ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ${BOLD}🔑 登录账号${NC}                                            ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ┌────────────┬────────────┬────────────┐             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  │ ${BOLD}角色${NC}       │ ${BOLD}账号${NC}       │ ${BOLD}密码${NC}       │             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  ├────────────┼────────────┼────────────┤             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  │ ${YELLOW}管理员${NC}     │ admin      │ admin123   │             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  │ ${GREEN}普通用户${NC}   │ user1      │ user123    │             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  │ ${GREEN}普通用户${NC}   │ user2      │ user123    │             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  │ ${GREEN}...${NC}        │ user3~10   │ user123    │             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}  └────────────┴────────────┴────────────┘             ${CYAN}║${NC}"
    echo -e "${CYAN}║${NC}                                                          ${CYAN}║${NC}"
    echo -e "${CYAN}╠══════════════════════════════════════════════════════════╣${NC}"
    echo -e "${CYAN}║${NC}  ${YELLOW}按 Ctrl+C 停止所有服务${NC}                                 ${CYAN}║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${BOLD}─── 实时日志输出 (前后端报错会在此显示) ───${NC}"
    echo ""
}

# =============================================================
# 主流程
# =============================================================
main() {
    print_banner
    check_env
    check_database
    check_ports
    install_deps
    start_services
    print_info_panel

    # 持续 tail 所有日志，让用户看到实时输出（包括报错）
    tail -f "$BACKEND_LOG" "$ADMIN_LOG" "$FRONTEND_LOG" &
    PIDS+=($!)

    # 等待所有子进程（直到 Ctrl+C）
    wait
}

main
