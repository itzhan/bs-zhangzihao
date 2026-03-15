#!/bin/bash
# ============================================================
# 农家乐线上预约管理系统 - API联调测试脚本
# 覆盖全部前后端接口 (50+ endpoints)
# ============================================================

BASE_URL="http://localhost:8090"
PASS=0
FAIL=0
TOTAL=0
ERRORS=""

# 临时文件
TMP_DIR="/tmp/farmstay_test_$$"
mkdir -p "$TMP_DIR"
trap "rm -rf $TMP_DIR" EXIT

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# ============================================================
# 辅助函数 - 使用临时文件存储响应，避免stdout污染
# ============================================================
test_api() {
    local method="$1"
    local url="$2"
    local desc="$3"
    local data="$4"
    local token="$5"
    local expected_code="${6:-200}"
    local resp_file="$TMP_DIR/resp_${TOTAL}.json"

    TOTAL=$((TOTAL + 1))

    local curl_args=(-s -w "%{http_code}" -o "$resp_file")
    curl_args+=(-H "Content-Type: application/json")

    if [ -n "$token" ]; then
        curl_args+=(-H "Authorization: Bearer $token")
    fi

    curl_args+=(-X "$method")

    if [ -n "$data" ] && [ "$method" != "GET" ] && [ "$method" != "DELETE" ]; then
        curl_args+=(-d "$data")
    fi

    local http_code
    http_code=$(curl "${curl_args[@]}" "$url" 2>/dev/null | tr -d '\n\r ')

    local body=""
    if [ -f "$resp_file" ]; then
        body=$(cat "$resp_file")
    fi

    # 检测
    if [ "$http_code" = "$expected_code" ]; then
        local biz_code
        biz_code=$(echo "$body" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('code',200))" 2>/dev/null)
        if [ "$biz_code" = "200" ] || [ "$biz_code" = "0" ] || [ -z "$biz_code" ]; then
            PASS=$((PASS + 1))
            printf "  ${GREEN}✅ [%s] %s${NC}\n" "$method" "$desc"
        else
            local biz_msg
            biz_msg=$(echo "$body" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('message',''))" 2>/dev/null)
            FAIL=$((FAIL + 1))
            printf "  ${RED}❌ [%s] %s (业务码: %s, %s)${NC}\n" "$method" "$desc" "$biz_code" "$biz_msg"
            ERRORS="$ERRORS\n  ❌ [$method] $desc -> code=$biz_code $biz_msg"
        fi
    else
        FAIL=$((FAIL + 1))
        printf "  ${RED}❌ [%s] %s (HTTP %s, 期望 %s)${NC}\n" "$method" "$desc" "$http_code" "$expected_code"
        ERRORS="$ERRORS\n  ❌ [$method] $desc -> HTTP $http_code (期望 $expected_code)"
    fi
}

# 登录并返回token (使用文件通信)
do_login() {
    local username="$1"
    local password="$2"
    local token_file="$TMP_DIR/token_${username}.txt"
    local resp_file="$TMP_DIR/login_${username}.json"

    local http_code
    http_code=$(curl -s -w "%{http_code}" -o "$resp_file" \
        -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\"}" 2>/dev/null | tr -d '\n\r ')

    if [ "$http_code" = "200" ]; then
        local body
        body=$(cat "$resp_file")
        local token
        token=$(echo "$body" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('data',{}).get('token','') if isinstance(d.get('data'), dict) else d.get('data',''))" 2>/dev/null)
        echo "$token" > "$token_file"
        echo "$token"
    else
        echo ""
    fi
}

# ============================================================
# 检查后端是否运行
# ============================================================
echo ""
printf "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}\n"
printf "${BLUE}║   农家乐线上预约管理系统 - API联调测试                       ║${NC}\n"
printf "${BLUE}║   后端地址: %s                                  ║${NC}\n" "$BASE_URL"
printf "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}\n"
echo ""

printf "${YELLOW}🔍 检查后端服务...${NC}\n"
HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/farmhouses" 2>/dev/null)
if [ "$HEALTH_CHECK" = "000" ]; then
    printf "${RED}❌ 后端服务未运行! 请先启动后端 (端口 8090)${NC}\n"
    exit 1
fi
printf "${GREEN}✅ 后端服务正常运行${NC}\n\n"

# ============================================================
# 1. 认证模块 (Auth)
# ============================================================
printf "${YELLOW}━━━ 1. 认证模块 (Auth) ━━━${NC}\n"

# 1.1 用户注册 (使用时间戳确保唯一)
REG_USER="testuser_$(date +%s)"
test_api "POST" "$BASE_URL/api/auth/register" "用户注册 ($REG_USER)" \
    "{\"username\":\"$REG_USER\",\"password\":\"test123\",\"nickname\":\"API测试用户\",\"phone\":\"13900000099\",\"email\":\"apitest_$(date +%s)@test.com\",\"gender\":1}"

# 1.2 用户登录 - 使用单独函数获取token
TOTAL=$((TOTAL + 1))
USER_TOKEN=$(do_login "user1" "user123")
if [ -n "$USER_TOKEN" ] && [ "$USER_TOKEN" != "None" ] && [ ${#USER_TOKEN} -gt 20 ]; then
    PASS=$((PASS + 1))
    printf "  ${GREEN}✅ [POST] 用户登录 (user1/user123)${NC}\n"
else
    FAIL=$((FAIL + 1))
    printf "  ${RED}❌ [POST] 用户登录 (user1/user123) - token获取失败${NC}\n"
    ERRORS="$ERRORS\n  ❌ [POST] 用户登录 -> token获取失败"
fi

# 1.3 管理员登录
TOTAL=$((TOTAL + 1))
ADMIN_TOKEN=$(do_login "admin" "admin123")
if [ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "None" ] && [ ${#ADMIN_TOKEN} -gt 20 ]; then
    PASS=$((PASS + 1))
    printf "  ${GREEN}✅ [POST] 管理员登录 (admin/admin123)${NC}\n"
else
    FAIL=$((FAIL + 1))
    printf "  ${RED}❌ [POST] 管理员登录 (admin/admin123) - token获取失败${NC}\n"
    ERRORS="$ERRORS\n  ❌ [POST] 管理员登录 -> token获取失败"
fi

printf "  ${BLUE}📌 用户Token: ${USER_TOKEN:0:30}...${NC}\n"
printf "  ${BLUE}📌 管理员Token: ${ADMIN_TOKEN:0:30}...${NC}\n"

# 1.4 获取当前用户信息
test_api "GET" "$BASE_URL/api/auth/info" "获取当前用户信息" "" "$USER_TOKEN"

echo ""

# ============================================================
# 2. 农家乐模块 (公开接口)
# ============================================================
printf "${YELLOW}━━━ 2. 农家乐模块 (公开接口) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/farmhouses" "农家乐列表 (无筛选)"
test_api "GET" "${BASE_URL}/api/farmhouses?keyword=%E6%A1%83%E6%BA%90" "农家乐列表 (搜索:桃源)"
test_api "GET" "$BASE_URL/api/farmhouses/1" "农家乐详情 (id=1)"
test_api "GET" "$BASE_URL/api/farmhouses/1/packages" "农家乐套餐列表 (id=1)"
test_api "GET" "$BASE_URL/api/farmhouses/1/reviews?page=1&size=5" "农家乐评价列表 (id=1)"

echo ""

# ============================================================
# 3. 套餐模块 (公开接口)
# ============================================================
printf "${YELLOW}━━━ 3. 套餐模块 (公开接口) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/packages/1" "套餐详情 (id=1)"

echo ""

# ============================================================
# 4. 档期模块 (公开接口)
# ============================================================
printf "${YELLOW}━━━ 4. 档期模块 (公开接口) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/schedules/package/1" "按套餐查档期 (packageId=1)"

echo ""

# ============================================================
# 5. 公告模块 (公开接口)
# ============================================================
printf "${YELLOW}━━━ 5. 公告模块 (公开接口) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/announcements?page=1&size=10" "公告列表"

echo ""

# ============================================================
# 6. 评价模块 (公开接口)
# ============================================================
printf "${YELLOW}━━━ 6. 评价公开接口 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/reviews/public/farmhouse/1?page=1&size=5" "公开评价列表 (farmhouseId=1)"

echo ""

# ============================================================
# 7. 用户个人模块 (需认证)
# ============================================================
printf "${YELLOW}━━━ 7. 用户个人模块 (需认证) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/users/me" "获取个人资料" "" "$USER_TOKEN"
test_api "PUT" "$BASE_URL/api/users/me" "更新个人资料" \
    '{"nickname":"张小明(已更新)","phone":"13811111111","email":"zhangxm_new@qq.com","gender":1}' "$USER_TOKEN"
test_api "PUT" "$BASE_URL/api/users/me/password" "修改密码" \
    '{"oldPassword":"user123","newPassword":"user123"}' "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/users/me/notifications?page=1&size=10" "获取通知列表 (via user)" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/users/me/notifications/unread-count" "获取未读通知数 (via user)" "" "$USER_TOKEN"

echo ""

# ============================================================
# 8. 收藏模块 (需认证)
# ============================================================
printf "${YELLOW}━━━ 8. 收藏模块 (需认证) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/favorites?page=1&size=10" "收藏列表" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/favorites/check/3" "检查是否收藏 (farmhouseId=3)" "" "$USER_TOKEN"
test_api "POST" "$BASE_URL/api/favorites/3" "添加收藏 (farmhouseId=3)" "" "$USER_TOKEN"
test_api "DELETE" "$BASE_URL/api/favorites/3" "取消收藏 (farmhouseId=3)" "" "$USER_TOKEN"

echo ""

# ============================================================
# 9. 通知模块 (需认证)
# ============================================================
printf "${YELLOW}━━━ 9. 通知模块 (需认证) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/notifications?page=1&size=10" "通知列表" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/notifications/unread-count" "未读通知数" "" "$USER_TOKEN"
test_api "PUT" "$BASE_URL/api/notifications/7/read" "标记通知已读 (id=7)" "" "$USER_TOKEN"
test_api "PUT" "$BASE_URL/api/notifications/read-all" "标记全部已读" "" "$USER_TOKEN"

echo ""

# ============================================================
# 10. 预约模块 (需认证)
# ============================================================
printf "${YELLOW}━━━ 10. 预约模块 (需认证) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/reservations?page=1&size=10" "我的预约列表" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/reservations/1" "预约详情 (id=1)" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/reservations?page=1&size=10&status=1" "预约列表 (status=已确认)" "" "$USER_TOKEN"

echo ""

# ============================================================
# 11. 订单模块 (需认证)
# ============================================================
printf "${YELLOW}━━━ 11. 订单模块 (需认证) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/orders?page=1&size=10" "我的订单列表" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/orders/1" "订单详情 (id=1)" "" "$USER_TOKEN"
test_api "GET" "$BASE_URL/api/orders?page=1&size=10&status=1" "订单列表 (status=已支付)" "" "$USER_TOKEN"

echo ""

# ============================================================
# 12. 管理端 - Dashboard
# ============================================================
printf "${YELLOW}━━━ 12. 管理端 - Dashboard (需管理员) ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/dashboard/statistics" "Dashboard统计概览" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/dashboard/reservation-trend?days=30" "预约趋势 (30天)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/dashboard/revenue-trend?days=30" "收入趋势 (30天)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/dashboard/top-farmhouses?limit=5" "热门农家乐TOP5" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/dashboard/rating-distribution" "评分分布" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 13. 管理端 - 农家乐管理
# ============================================================
printf "${YELLOW}━━━ 13. 管理端 - 农家乐管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/farmhouses?page=1&size=10" "农家乐分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "${BASE_URL}/api/admin/farmhouses?page=1&size=10&keyword=%E7%AB%B9%E6%9E%97" "农家乐关键词搜索" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/farmhouses?page=1&size=10&status=1" "农家乐按状态筛选" "" "$ADMIN_TOKEN"
test_api "POST" "$BASE_URL/api/admin/farmhouses" "创建农家乐 (测试)" \
    '{"name":"API测试农家乐","description":"自动化测试创建","shortDesc":"测试用","address":"测试地址","phone":"13900000000","coverImage":"https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=400","ownerName":"测试","tags":"测试","features":"[\"WiFi\"]","businessHours":"08:00-20:00","sortOrder":1,"status":1}' "$ADMIN_TOKEN"

echo ""

# ============================================================
# 14. 管理端 - 套餐管理
# ============================================================
printf "${YELLOW}━━━ 14. 管理端 - 套餐管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/packages?page=1&size=10" "套餐分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/packages?page=1&size=10&farmhouseId=1" "套餐按农家乐筛选" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/packages?page=1&size=10&type=1" "套餐按类型筛选 (住宿)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 15. 管理端 - 档期管理
# ============================================================
printf "${YELLOW}━━━ 15. 管理端 - 档期管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/schedules?page=1&size=10" "档期分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/schedules?page=1&size=10&packageId=1" "档期按套餐筛选" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/schedules?page=1&size=10&farmhouseId=1" "档期按农家乐筛选" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 16. 管理端 - 预约管理
# ============================================================
printf "${YELLOW}━━━ 16. 管理端 - 预约管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/reservations?page=1&size=10" "预约分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/reservations?page=1&size=10&status=0" "预约列表 (待确认)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/reservations?page=1&size=10&farmhouseId=1" "预约列表 (按农家乐)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 17. 管理端 - 订单管理
# ============================================================
printf "${YELLOW}━━━ 17. 管理端 - 订单管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/orders?page=1&size=10" "订单分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/orders?page=1&size=10&status=1" "订单列表 (已支付)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/orders?page=1&size=10&farmhouseId=1" "订单列表 (按农家乐)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 18. 管理端 - 评价管理
# ============================================================
printf "${YELLOW}━━━ 18. 管理端 - 评价管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/reviews?page=1&size=10" "评价分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/reviews?page=1&size=10&status=0" "评价列表 (待审核)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/reviews?page=1&size=10&farmhouseId=1" "评价列表 (按农家乐)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 19. 管理端 - 用户管理
# ============================================================
printf "${YELLOW}━━━ 19. 管理端 - 用户管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/users?page=1&size=10" "用户分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "${BASE_URL}/api/admin/users?page=1&size=10&keyword=%E5%BC%A0" "用户搜索 (关键词:张)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/users/2" "用户详情 (id=2)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 20. 管理端 - 公告管理
# ============================================================
printf "${YELLOW}━━━ 20. 管理端 - 公告管理 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/announcements?page=1&size=10" "公告分页列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/announcements?page=1&size=10&type=1" "公告列表 (公告类型)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/announcements?page=1&size=10&status=1" "公告列表 (已发布)" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/announcements/1" "公告详情 (id=1)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 21. 管理端 - 审计日志
# ============================================================
printf "${YELLOW}━━━ 21. 管理端 - 审计日志 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/audit-logs?page=1&size=10" "审计日志列表" "" "$ADMIN_TOKEN"
test_api "GET" "$BASE_URL/api/admin/audit-logs?page=1&size=10&action=LOGIN" "审计日志 (LOGIN操作)" "" "$ADMIN_TOKEN"
test_api "GET" "${BASE_URL}/api/admin/audit-logs?page=1&size=10&module=%E8%AE%A4%E8%AF%81" "审计日志 (认证模块)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 22. 管理端 - 系统设置
# ============================================================
printf "${YELLOW}━━━ 22. 管理端 - 系统设置 ━━━${NC}\n"

test_api "GET" "$BASE_URL/api/admin/settings" "获取系统设置" "" "$ADMIN_TOKEN"
test_api "PUT" "$BASE_URL/api/admin/settings" "更新系统设置" \
    '{"site_name":"农家乐线上预约管理系统","site_description":"发现河南最美农家乐，享受田园慢生活"}' "$ADMIN_TOKEN"

echo ""

# ============================================================
# 23. 权限验证测试
# ============================================================
printf "${YELLOW}━━━ 23. 权限验证测试 ━━━${NC}\n"

# 23.1 无Token访问需认证接口
TOTAL=$((TOTAL + 1))
auth_http=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/orders" 2>/dev/null)
if [ "$auth_http" = "401" ] || [ "$auth_http" = "403" ]; then
    PASS=$((PASS + 1))
    printf "  ${GREEN}✅ [GET] 无Token访问认证接口 -> 正确拒绝 (HTTP %s)${NC}\n" "$auth_http"
else
    FAIL=$((FAIL + 1))
    printf "  ${RED}❌ [GET] 无Token访问认证接口 -> 未拒绝 (HTTP %s)${NC}\n" "$auth_http"
fi

# 23.2 普通用户访问管理员接口
TOTAL=$((TOTAL + 1))
admin_http=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/admin/users" \
    -H "Authorization: Bearer $USER_TOKEN" 2>/dev/null)
if [ "$admin_http" = "401" ] || [ "$admin_http" = "403" ]; then
    PASS=$((PASS + 1))
    printf "  ${GREEN}✅ [GET] 普通用户访问管理接口 -> 正确拒绝 (HTTP %s)${NC}\n" "$admin_http"
else
    FAIL=$((FAIL + 1))
    printf "  ${RED}❌ [GET] 普通用户访问管理接口 -> 未拒绝 (HTTP %s)${NC}\n" "$admin_http"
fi

echo ""

# ============================================================
# 清理测试数据
# ============================================================
printf "${YELLOW}━━━ 清理测试数据 ━━━${NC}\n"

test_api "DELETE" "$BASE_URL/api/admin/farmhouses/11" "删除测试农家乐 (id=11)" "" "$ADMIN_TOKEN"

echo ""

# ============================================================
# 测试结果汇总
# ============================================================
echo ""
printf "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}\n"
printf "${BLUE}║                    测试结果汇总                             ║${NC}\n"
printf "${BLUE}╠══════════════════════════════════════════════════════════════╣${NC}\n"
printf "${BLUE}║${NC}  总测试数:  %-46s${BLUE}║${NC}\n" "$TOTAL"
printf "${BLUE}║${NC}  ${GREEN}通过:      %-46s${NC}${BLUE}║${NC}\n" "$PASS"
printf "${BLUE}║${NC}  ${RED}失败:      %-46s${NC}${BLUE}║${NC}\n" "$FAIL"

if [ $TOTAL -gt 0 ]; then
    RATE=$(python3 -c "print(f'{$PASS/$TOTAL*100:.1f}%')" 2>/dev/null || echo "N/A")
    printf "${BLUE}║${NC}  通过率:    %-46s${BLUE}║${NC}\n" "$RATE"
fi

printf "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}\n"

if [ -n "$ERRORS" ]; then
    echo ""
    printf "${RED}━━━ 失败详情 ━━━${NC}\n"
    printf "$ERRORS\n"
fi

echo ""

if [ $FAIL -eq 0 ]; then
    printf "${GREEN}🎉 所有测试通过！前后端联调无问题！${NC}\n"
    exit 0
else
    printf "${RED}⚠️ 有 $FAIL 个测试失败，请检查上方失败详情。${NC}\n"
    exit 1
fi
