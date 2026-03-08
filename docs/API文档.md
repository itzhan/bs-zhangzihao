# 农家乐线上预约管理系统 API 文档

## 基础信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Bearer Token
- **请求头**: `Authorization: Bearer {token}`
- **Content-Type**: `application/json`

## 统一响应结构

### 标准响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

## 认证模块 `/api/auth`

### 1. 用户登录

**POST** `/api/auth/login`

**描述**: 用户登录，返回 JWT token 和用户信息

**认证**: 无需认证

**请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "user001",
      "nickname": "张三",
      "phone": "13800138000",
      "email": "user@example.com",
      "avatar": "http://localhost:8080/files/avatar.jpg",
      "gender": 1,
      "role": "USER",
      "status": 1
    }
  }
}
```

---

### 2. 用户注册

**POST** `/api/auth/register`

**描述**: 新用户注册

**认证**: 无需认证

**请求体**:
```json
{
  "username": "string",
  "password": "string",
  "nickname": "string",
  "phone": "string",
  "email": "string",
  "gender": 0
}
```

**字段说明**:
- `username`: 必填，用户名
- `password`: 必填，密码
- `nickname`: 可选，昵称
- `phone`: 可选，手机号
- `email`: 可选，邮箱
- `gender`: 可选，性别（0-未知，1-男，2-女）

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "user001"
  }
}
```

---

### 3. 获取当前用户信息

**GET** `/api/auth/info`

**描述**: 获取当前登录用户信息

**认证**: 需要认证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "user001",
    "nickname": "张三",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "http://localhost:8080/files/avatar.jpg",
    "gender": 1,
    "role": "USER",
    "status": 1
  }
}
```

---

## 用户模块 `/api/users`

### 1. 获取个人信息

**GET** `/api/users/me`

**描述**: 获取当前用户的个人信息

**认证**: 需要认证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "user001",
    "nickname": "张三",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "http://localhost:8080/files/avatar.jpg",
    "gender": 1,
    "role": "USER",
    "status": 1
  }
}
```

---

### 2. 更新个人信息

**PUT** `/api/users/me`

**描述**: 更新当前用户的个人信息

**认证**: 需要认证

**请求体**:
```json
{
  "nickname": "李四",
  "phone": "13900139000",
  "email": "newemail@example.com",
  "gender": 1,
  "avatar": "http://localhost:8080/files/new-avatar.jpg"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "nickname": "李四",
    "phone": "13900139000",
    "email": "newemail@example.com"
  }
}
```

---

### 3. 修改密码

**PUT** `/api/users/me/password`

**描述**: 修改当前用户的登录密码

**认证**: 需要认证

**请求体**:
```json
{
  "oldPassword": "old123456",
  "newPassword": "new123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

---

## 农家乐模块 `/api/farmhouses` (公开)

### 1. 获取农家乐列表

**GET** `/api/farmhouses`

**描述**: 获取农家乐列表，支持关键词搜索

**认证**: 无需认证

**查询参数**:
- `keyword`: 可选，搜索关键词（名称、地址等）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "田园风光农家乐",
      "description": "环境优美，设施齐全",
      "shortDesc": "环境优美",
      "address": "北京市昌平区xxx",
      "phone": "010-12345678",
      "coverImage": "http://localhost:8080/files/cover.jpg",
      "images": ["http://localhost:8080/files/img1.jpg"],
      "ownerName": "王老板",
      "rating": 4.5,
      "reviewCount": 120,
      "tags": "休闲,度假",
      "features": "WiFi,停车,餐饮",
      "businessHours": "09:00-18:00",
      "sortOrder": 1,
      "status": 1
    }
  ]
}
```

---

### 2. 获取农家乐详情

**GET** `/api/farmhouses/{id}`

**描述**: 获取指定农家乐的详细信息

**认证**: 无需认证

**路径参数**:
- `id`: 农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "田园风光农家乐",
    "description": "详细描述...",
    "shortDesc": "环境优美",
    "address": "北京市昌平区xxx",
    "phone": "010-12345678",
    "coverImage": "http://localhost:8080/files/cover.jpg",
    "images": ["http://localhost:8080/files/img1.jpg"],
    "ownerName": "王老板",
    "rating": 4.5,
    "reviewCount": 120,
    "tags": "休闲,度假",
    "features": "WiFi,停车,餐饮",
    "businessHours": "09:00-18:00",
    "sortOrder": 1,
    "status": 1
  }
}
```

---

### 3. 获取农家乐套餐列表

**GET** `/api/farmhouses/{id}/packages`

**描述**: 获取指定农家乐的所有套餐

**认证**: 无需认证

**路径参数**:
- `id`: 农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "farmhouseId": 1,
      "name": "一日游套餐",
      "description": "包含午餐和活动",
      "price": 198.00,
      "originalPrice": 238.00,
      "coverImage": "http://localhost:8080/files/package.jpg",
      "type": 1,
      "capacity": 10,
      "duration": 1,
      "includes": "午餐,活动",
      "sortOrder": 1,
      "status": 1
    }
  ]
}
```

---

### 4. 获取农家乐评价

**GET** `/api/farmhouses/{id}/reviews`

**描述**: 获取指定农家乐的评价列表（分页）

**认证**: 无需认证

**路径参数**:
- `id`: 农家乐ID

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "farmhouseId": 1,
        "rating": 5,
        "content": "非常满意",
        "images": [],
        "status": 1,
        "adminReply": null,
        "replyTime": null
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

## 套餐模块 `/api/packages` (公开)

### 1. 获取套餐详情

**GET** `/api/packages/{id}`

**描述**: 获取指定套餐的详细信息

**认证**: 无需认证

**路径参数**:
- `id`: 套餐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "farmhouseId": 1,
    "name": "一日游套餐",
    "description": "详细描述...",
    "price": 198.00,
    "originalPrice": 238.00,
    "coverImage": "http://localhost:8080/files/package.jpg",
    "type": 1,
    "capacity": 10,
    "duration": 1,
    "includes": "午餐,活动",
    "sortOrder": 1,
    "status": 1
  }
}
```

---

## 档期模块 `/api/schedules` (公开)

### 1. 获取套餐可用档期

**GET** `/api/schedules/package/{packageId}`

**描述**: 获取指定套餐的可用档期列表

**认证**: 无需认证

**路径参数**:
- `packageId`: 套餐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "packageId": 1,
      "farmhouseId": 1,
      "scheduleDate": "2026-02-15",
      "totalQuota": 20,
      "remainingQuota": 15,
      "priceOverride": null,
      "status": 1
    }
  ]
}
```

---

## 预约模块 `/api/reservations` (需认证)

### 1. 创建预约

**POST** `/api/reservations`

**描述**: 创建新的预约

**认证**: 需要认证

**请求体**:
```json
{
  "farmhouseId": 1,
  "packageId": 1,
  "scheduleId": 1,
  "reserveDate": "2026-02-15",
  "personCount": 4,
  "contactName": "张三",
  "contactPhone": "13800138000",
  "remark": "需要停车位"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "预约创建成功",
  "data": {
    "id": 1,
    "reservationNo": "RES20260208001",
    "userId": 1,
    "farmhouseId": 1,
    "packageId": 1,
    "scheduleId": 1,
    "reserveDate": "2026-02-15",
    "personCount": 4,
    "contactName": "张三",
    "contactPhone": "13800138000",
    "remark": "需要停车位",
    "status": 0,
    "cancelReason": null
  }
}
```

---

### 2. 查看我的预约

**GET** `/api/reservations`

**描述**: 获取当前用户的预约列表（分页）

**认证**: 需要认证

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `status`: 可选，预约状态（0-待确认，1-已确认，2-已完成，3-已取消）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "reservationNo": "RES20260208001",
        "farmhouseId": 1,
        "packageId": 1,
        "reserveDate": "2026-02-15",
        "personCount": 4,
        "status": 1
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

---

### 3. 查看预约详情

**GET** `/api/reservations/{id}`

**描述**: 获取指定预约的详细信息

**认证**: 需要认证

**路径参数**:
- `id`: 预约ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "reservationNo": "RES20260208001",
    "userId": 1,
    "farmhouseId": 1,
    "packageId": 1,
    "scheduleId": 1,
    "reserveDate": "2026-02-15",
    "personCount": 4,
    "contactName": "张三",
    "contactPhone": "13800138000",
    "remark": "需要停车位",
    "status": 1,
    "cancelReason": null
  }
}
```

---

### 4. 取消预约

**PUT** `/api/reservations/{id}/cancel`

**描述**: 取消指定的预约

**认证**: 需要认证

**路径参数**:
- `id`: 预约ID

**请求体**:
```json
{
  "reason": "临时有事，无法前往"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "预约已取消",
  "data": null
}
```

---

## 订单模块 `/api/orders` (需认证)

### 1. 创建订单

**POST** `/api/orders`

**描述**: 基于预约创建订单

**认证**: 需要认证

**请求体**:
```json
{
  "reservationId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "id": 1,
    "orderNo": "ORD20260208001",
    "userId": 1,
    "reservationId": 1,
    "farmhouseId": 1,
    "packageId": 1,
    "farmhouseName": "田园风光农家乐",
    "packageName": "一日游套餐",
    "personCount": 4,
    "unitPrice": 198.00,
    "totalAmount": 792.00,
    "status": 0,
    "paymentMethod": null,
    "payTime": null,
    "cancelTime": null,
    "cancelReason": null,
    "completeTime": null,
    "remark": null
  }
}
```

---

### 2. 查看我的订单

**GET** `/api/orders`

**描述**: 获取当前用户的订单列表（分页）

**认证**: 需要认证

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `status`: 可选，订单状态（0-待支付，1-已支付，2-已完成，3-已取消，4-退款中，5-已退款）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD20260208001",
        "farmhouseName": "田园风光农家乐",
        "packageName": "一日游套餐",
        "totalAmount": 792.00,
        "status": 1,
        "payTime": "2026-02-08T10:30:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

### 3. 查看订单详情

**GET** `/api/orders/{id}`

**描述**: 获取指定订单的详细信息

**认证**: 需要认证

**路径参数**:
- `id`: 订单ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD20260208001",
    "userId": 1,
    "reservationId": 1,
    "farmhouseId": 1,
    "packageId": 1,
    "farmhouseName": "田园风光农家乐",
    "packageName": "一日游套餐",
    "personCount": 4,
    "unitPrice": 198.00,
    "totalAmount": 792.00,
    "status": 1,
    "paymentMethod": "微信支付",
    "payTime": "2026-02-08T10:30:00",
    "cancelTime": null,
    "cancelReason": null,
    "completeTime": null,
    "remark": null
  }
}
```

---

### 4. 支付订单

**PUT** `/api/orders/{id}/pay`

**描述**: 支付指定订单

**认证**: 需要认证

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "paymentMethod": "微信支付"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "支付成功",
  "data": null
}
```

---

### 5. 取消订单

**PUT** `/api/orders/{id}/cancel`

**描述**: 取消指定订单

**认证**: 需要认证

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "reason": "临时有事，无法前往"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "订单已取消",
  "data": null
}
```

---

### 6. 申请退款

**PUT** `/api/orders/{id}/refund`

**描述**: 申请订单退款

**认证**: 需要认证

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "reason": "服务不满意"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "退款申请已提交",
  "data": null
}
```

---

## 评价模块 `/api/reviews` (需认证创建, 公开查看)

### 1. 发布评价

**POST** `/api/reviews`

**描述**: 发布对农家乐的评价

**认证**: 需要认证

**请求体**:
```json
{
  "farmhouseId": 1,
  "orderId": 1,
  "rating": 5,
  "content": "非常满意，环境优美，服务周到",
  "images": ["http://localhost:8080/files/review1.jpg"]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "评价发布成功",
  "data": {
    "id": 1,
    "userId": 1,
    "farmhouseId": 1,
    "orderId": 1,
    "rating": 5,
    "content": "非常满意，环境优美，服务周到",
    "images": ["http://localhost:8080/files/review1.jpg"],
    "status": 0,
    "adminReply": null,
    "replyTime": null
  }
}
```

---

### 2. 获取农家乐评价(公开)

**GET** `/api/reviews/public/farmhouse/{farmhouseId}`

**描述**: 获取指定农家乐的公开评价列表（分页）

**认证**: 无需认证

**路径参数**:
- `farmhouseId`: 农家乐ID

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "rating": 5,
        "content": "非常满意",
        "images": [],
        "status": 1,
        "adminReply": "感谢您的评价",
        "replyTime": "2026-02-08T11:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

## 收藏模块 `/api/favorites` (需认证)

### 1. 收藏农家乐

**POST** `/api/favorites/{farmhouseId}`

**描述**: 收藏指定的农家乐

**认证**: 需要认证

**路径参数**:
- `farmhouseId`: 农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "id": 1,
    "userId": 1,
    "farmhouseId": 1
  }
}
```

---

### 2. 取消收藏

**DELETE** `/api/favorites/{farmhouseId}`

**描述**: 取消收藏指定的农家乐

**认证**: 需要认证

**路径参数**:
- `farmhouseId`: 农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "取消收藏成功",
  "data": null
}
```

---

### 3. 我的收藏列表

**GET** `/api/favorites`

**描述**: 获取当前用户的收藏列表

**认证**: 需要认证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "farmhouseId": 1,
      "farmhouse": {
        "id": 1,
        "name": "田园风光农家乐",
        "coverImage": "http://localhost:8080/files/cover.jpg",
        "rating": 4.5
      }
    }
  ]
}
```

---

### 4. 检查是否已收藏

**GET** `/api/favorites/check/{farmhouseId}`

**描述**: 检查当前用户是否已收藏指定农家乐

**认证**: 需要认证

**路径参数**:
- `farmhouseId`: 农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "isFavorited": true
  }
}
```

---

## 公告模块 `/api/announcements` (公开)

### 1. 获取公告列表

**GET** `/api/announcements`

**描述**: 获取系统公告列表

**认证**: 无需认证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "系统维护通知",
      "content": "系统将于...",
      "type": 1,
      "coverImage": "http://localhost:8080/files/announce.jpg",
      "startTime": "2026-02-01T00:00:00",
      "endTime": "2026-02-28T23:59:59",
      "status": 1,
      "sortOrder": 1
    }
  ]
}
```

---

## 通知模块 `/api/notifications` (需认证)

### 1. 我的通知列表

**GET** `/api/notifications`

**描述**: 获取当前用户的通知列表（分页）

**认证**: 需要认证

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "title": "预约确认通知",
        "content": "您的预约已确认",
        "type": 1,
        "isRead": 0
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 标记已读

**PUT** `/api/notifications/{id}/read`

**描述**: 标记指定通知为已读

**认证**: 需要认证

**路径参数**:
- `id`: 通知ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 3. 全部已读

**PUT** `/api/notifications/read-all`

**描述**: 标记所有通知为已读

**认证**: 需要认证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 4. 未读数量

**GET** `/api/notifications/unread-count`

**描述**: 获取当前用户的未读通知数量

**认证**: 需要认证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "count": 5
  }
}
```

---

## 文件上传 `/api/files` (需认证)

### 1. 上传文件

**POST** `/api/files/upload`

**描述**: 上传文件（图片、文档等）

**认证**: 需要认证

**Content-Type**: `multipart/form-data`

**请求参数**:
- `file`: 文件（form-data字段）

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:8080/files/20260208/abc123.jpg",
    "filename": "abc123.jpg",
    "size": 102400
  }
}
```

---

## 管理员-用户管理 `/api/admin/users`

### 1. 用户列表

**GET** `/api/admin/users`

**描述**: 获取用户列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `keyword`: 可选，搜索关键词（用户名、昵称、手机号）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "user001",
        "nickname": "张三",
        "phone": "13800138000",
        "email": "user@example.com",
        "role": "USER",
        "status": 1
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 用户详情

**GET** `/api/admin/users/{id}`

**描述**: 获取指定用户的详细信息

**认证**: 需要管理员权限

**路径参数**:
- `id`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "user001",
    "nickname": "张三",
    "phone": "13800138000",
    "email": "user@example.com",
    "avatar": "http://localhost:8080/files/avatar.jpg",
    "gender": 1,
    "role": "USER",
    "status": 1
  }
}
```

---

### 3. 启用/禁用用户

**PUT** `/api/admin/users/{id}/status`

**描述**: 启用或禁用指定用户

**认证**: 需要管理员权限

**路径参数**:
- `id`: 用户ID

**请求体**:
```json
{
  "status": 0
}
```

**字段说明**:
- `status`: 0-禁用，1-启用

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 管理员-农家乐管理 `/api/admin/farmhouses`

### 1. 农家乐列表

**GET** `/api/admin/farmhouses`

**描述**: 获取农家乐列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "田园风光农家乐",
        "address": "北京市昌平区xxx",
        "phone": "010-12345678",
        "rating": 4.5,
        "reviewCount": 120,
        "status": 1
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 新增农家乐

**POST** `/api/admin/farmhouses`

**描述**: 新增农家乐

**认证**: 需要管理员权限

**请求体**:
```json
{
  "name": "新农家乐",
  "description": "详细描述",
  "shortDesc": "简短描述",
  "address": "北京市xxx",
  "phone": "010-12345678",
  "coverImage": "http://localhost:8080/files/cover.jpg",
  "images": ["http://localhost:8080/files/img1.jpg"],
  "ownerName": "老板姓名",
  "tags": "休闲,度假",
  "features": "WiFi,停车,餐饮",
  "businessHours": "09:00-18:00",
  "sortOrder": 1,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "name": "新农家乐"
  }
}
```

---

### 3. 编辑农家乐

**PUT** `/api/admin/farmhouses/{id}`

**描述**: 编辑指定农家乐信息

**认证**: 需要管理员权限

**路径参数**:
- `id`: 农家乐ID

**请求体**:
```json
{
  "name": "更新后的名称",
  "description": "更新后的描述",
  "address": "更新后的地址",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 4. 删除农家乐

**DELETE** `/api/admin/farmhouses/{id}`

**描述**: 删除指定农家乐

**认证**: 需要管理员权限

**路径参数**:
- `id`: 农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 5. 上架/下架

**PUT** `/api/admin/farmhouses/{id}/status`

**描述**: 上架或下架指定农家乐

**认证**: 需要管理员权限

**路径参数**:
- `id`: 农家乐ID

**请求体**:
```json
{
  "status": 0
}
```

**字段说明**:
- `status`: 0-下架，1-上架

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 管理员-套餐管理 `/api/admin/packages`

### 1. 套餐列表

**GET** `/api/admin/packages`

**描述**: 获取套餐列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `farmhouseId`: 可选，农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "farmhouseId": 1,
        "name": "一日游套餐",
        "price": 198.00,
        "type": 1,
        "capacity": 10,
        "status": 1
      }
    ],
    "total": 30,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 新增套餐

**POST** `/api/admin/packages`

**描述**: 新增套餐

**认证**: 需要管理员权限

**请求体**:
```json
{
  "farmhouseId": 1,
  "name": "新套餐",
  "description": "套餐描述",
  "price": 198.00,
  "originalPrice": 238.00,
  "coverImage": "http://localhost:8080/files/package.jpg",
  "type": 1,
  "capacity": 10,
  "duration": 1,
  "includes": "午餐,活动",
  "sortOrder": 1,
  "status": 1
}
```

**字段说明**:
- `type`: 1-一日游，2-两日游，3-三日游，4-其他

**响应示例**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "name": "新套餐"
  }
}
```

---

### 3. 编辑套餐

**PUT** `/api/admin/packages/{id}`

**描述**: 编辑指定套餐信息

**认证**: 需要管理员权限

**路径参数**:
- `id`: 套餐ID

**请求体**:
```json
{
  "name": "更新后的套餐名",
  "price": 188.00,
  "description": "更新后的描述",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 4. 删除套餐

**DELETE** `/api/admin/packages/{id}`

**描述**: 删除指定套餐

**认证**: 需要管理员权限

**路径参数**:
- `id`: 套餐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 管理员-档期管理 `/api/admin/schedules`

### 1. 档期列表

**GET** `/api/admin/schedules`

**描述**: 获取档期列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `packageId`: 可选，套餐ID
- `farmhouseId`: 可选，农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "packageId": 1,
        "farmhouseId": 1,
        "scheduleDate": "2026-02-15",
        "totalQuota": 20,
        "remainingQuota": 15,
        "status": 1
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 新增档期

**POST** `/api/admin/schedules`

**描述**: 新增档期

**认证**: 需要管理员权限

**请求体**:
```json
{
  "packageId": 1,
  "farmhouseId": 1,
  "scheduleDate": "2026-02-20",
  "totalQuota": 20,
  "priceOverride": null,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "scheduleDate": "2026-02-20"
  }
}
```

---

### 3. 编辑档期

**PUT** `/api/admin/schedules/{id}`

**描述**: 编辑指定档期信息

**认证**: 需要管理员权限

**路径参数**:
- `id`: 档期ID

**请求体**:
```json
{
  "scheduleDate": "2026-02-21",
  "totalQuota": 25,
  "priceOverride": 180.00,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 4. 删除档期

**DELETE** `/api/admin/schedules/{id}`

**描述**: 删除指定档期

**认证**: 需要管理员权限

**路径参数**:
- `id`: 档期ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 管理员-预约管理 `/api/admin/reservations`

### 1. 预约列表

**GET** `/api/admin/reservations`

**描述**: 获取预约列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `status`: 可选，预约状态
- `farmhouseId`: 可选，农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "reservationNo": "RES20260208001",
        "userId": 1,
        "farmhouseId": 1,
        "reserveDate": "2026-02-15",
        "personCount": 4,
        "contactName": "张三",
        "contactPhone": "13800138000",
        "status": 0
      }
    ],
    "total": 200,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 确认预约

**PUT** `/api/admin/reservations/{id}/confirm`

**描述**: 确认指定预约

**认证**: 需要管理员权限

**路径参数**:
- `id`: 预约ID

**响应示例**:
```json
{
  "code": 200,
  "message": "预约已确认",
  "data": null
}
```

---

### 3. 完成预约

**PUT** `/api/admin/reservations/{id}/complete`

**描述**: 标记指定预约为已完成

**认证**: 需要管理员权限

**路径参数**:
- `id`: 预约ID

**响应示例**:
```json
{
  "code": 200,
  "message": "预约已完成",
  "data": null
}
```

---

## 管理员-订单管理 `/api/admin/orders`

### 1. 订单列表

**GET** `/api/admin/orders`

**描述**: 获取订单列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `status`: 可选，订单状态
- `farmhouseId`: 可选，农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD20260208001",
        "userId": 1,
        "farmhouseName": "田园风光农家乐",
        "packageName": "一日游套餐",
        "totalAmount": 792.00,
        "status": 1,
        "payTime": "2026-02-08T10:30:00"
      }
    ],
    "total": 500,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 处理退款

**PUT** `/api/admin/orders/{id}/refund`

**描述**: 处理订单退款申请

**认证**: 需要管理员权限

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "action": "approve",
  "remark": "同意退款"
}
```

**字段说明**:
- `action`: approve-同意退款，reject-拒绝退款
- `remark`: 备注信息

**响应示例**:
```json
{
  "code": 200,
  "message": "退款处理成功",
  "data": null
}
```

---

### 3. 完成订单

**PUT** `/api/admin/orders/{id}/complete`

**描述**: 标记指定订单为已完成

**认证**: 需要管理员权限

**路径参数**:
- `id`: 订单ID

**响应示例**:
```json
{
  "code": 200,
  "message": "订单已完成",
  "data": null
}
```

---

## 管理员-评价管理 `/api/admin/reviews`

### 1. 评价列表

**GET** `/api/admin/reviews`

**描述**: 获取评价列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `status`: 可选，评价状态（0-待审核，1-已通过，2-已拒绝）
- `farmhouseId`: 可选，农家乐ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "farmhouseId": 1,
        "rating": 5,
        "content": "非常满意",
        "status": 0
      }
    ],
    "total": 300,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 审核通过

**PUT** `/api/admin/reviews/{id}/approve`

**描述**: 审核通过指定评价

**认证**: 需要管理员权限

**路径参数**:
- `id`: 评价ID

**响应示例**:
```json
{
  "code": 200,
  "message": "审核通过",
  "data": null
}
```

---

### 3. 审核拒绝

**PUT** `/api/admin/reviews/{id}/reject`

**描述**: 审核拒绝指定评价

**认证**: 需要管理员权限

**路径参数**:
- `id`: 评价ID

**请求体**:
```json
{
  "reason": "评价内容不符合规范"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "审核拒绝",
  "data": null
}
```

---

### 4. 管理员回复

**PUT** `/api/admin/reviews/{id}/reply`

**描述**: 管理员回复指定评价

**认证**: 需要管理员权限

**路径参数**:
- `id`: 评价ID

**请求体**:
```json
{
  "reply": "感谢您的评价，我们会继续努力"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "回复成功",
  "data": null
}
```

---

## 管理员-公告管理 `/api/admin/announcements`

### 1. 公告列表

**GET** `/api/admin/announcements`

**描述**: 获取公告列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "系统维护通知",
        "type": 1,
        "startTime": "2026-02-01T00:00:00",
        "endTime": "2026-02-28T23:59:59",
        "status": 1,
        "sortOrder": 1
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

### 2. 新增公告

**POST** `/api/admin/announcements`

**描述**: 新增公告

**认证**: 需要管理员权限

**请求体**:
```json
{
  "title": "新公告标题",
  "content": "公告内容",
  "type": 1,
  "coverImage": "http://localhost:8080/files/announce.jpg",
  "startTime": "2026-02-10T00:00:00",
  "endTime": "2026-02-28T23:59:59",
  "status": 1,
  "sortOrder": 1
}
```

**字段说明**:
- `type`: 1-系统公告，2-活动公告

**响应示例**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "title": "新公告标题"
  }
}
```

---

### 3. 公告详情

**GET** `/api/admin/announcements/{id}`

**描述**: 获取指定公告的详细信息

**认证**: 需要管理员权限

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "系统维护通知",
    "content": "公告内容...",
    "type": 1,
    "coverImage": "http://localhost:8080/files/announce.jpg",
    "startTime": "2026-02-01T00:00:00",
    "endTime": "2026-02-28T23:59:59",
    "status": 1,
    "sortOrder": 1
  }
}
```

---

### 4. 编辑公告

**PUT** `/api/admin/announcements/{id}`

**描述**: 编辑指定公告信息

**认证**: 需要管理员权限

**路径参数**:
- `id`: 公告ID

**请求体**:
```json
{
  "title": "更新后的标题",
  "content": "更新后的内容",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 5. 删除公告

**DELETE** `/api/admin/announcements/{id}`

**描述**: 删除指定公告

**认证**: 需要管理员权限

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 管理员-审计日志 `/api/admin/audit-logs`

### 1. 日志列表

**GET** `/api/admin/audit-logs`

**描述**: 获取系统审计日志列表（分页）

**认证**: 需要管理员权限

**查询参数**:
- `page`: 页码，默认1
- `size`: 每页数量，默认10
- `action`: 可选，操作类型
- `userId`: 可选，用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "action": "CREATE_FARMHOUSE",
        "description": "创建农家乐",
        "ipAddress": "127.0.0.1",
        "createTime": "2026-02-08T10:00:00"
      }
    ],
    "total": 1000,
    "page": 1,
    "size": 10
  }
}
```

---

## 管理员-系统设置 `/api/admin/settings`

### 1. 获取所有配置

**GET** `/api/admin/settings`

**描述**: 获取系统所有配置项

**认证**: 需要管理员权限

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "siteName": "农家乐预约系统",
    "siteLogo": "http://localhost:8080/files/logo.png",
    "contactPhone": "400-123-4567",
    "contactEmail": "service@example.com",
    "minReservationDays": 1,
    "maxReservationDays": 30
  }
}
```

---

### 2. 批量更新配置

**PUT** `/api/admin/settings`

**描述**: 批量更新系统配置

**认证**: 需要管理员权限

**请求体**:
```json
{
  "siteName": "新系统名称",
  "contactPhone": "400-123-4568",
  "minReservationDays": 2
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

## 管理员-数据统计 `/api/admin/dashboard`

### 1. 统计概览

**GET** `/api/admin/dashboard/statistics`

**描述**: 获取系统统计概览数据

**认证**: 需要管理员权限

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalUsers": 1000,
    "totalFarmhouses": 50,
    "totalOrders": 5000,
    "totalRevenue": 500000.00,
    "todayOrders": 20,
    "todayRevenue": 5000.00,
    "pendingReservations": 10,
    "pendingReviews": 5
  }
}
```

---

### 2. 预约趋势

**GET** `/api/admin/dashboard/reservation-trend`

**描述**: 获取预约趋势数据

**认证**: 需要管理员权限

**查询参数**:
- `startDate`: 开始日期，格式：yyyy-MM-dd
- `endDate`: 结束日期，格式：yyyy-MM-dd
- `type`: 统计类型（day/week/month），默认day

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "labels": ["2026-02-01", "2026-02-02", "2026-02-03"],
    "values": [10, 15, 20]
  }
}
```

---

### 3. 营收趋势

**GET** `/api/admin/dashboard/revenue-trend`

**描述**: 获取营收趋势数据

**认证**: 需要管理员权限

**查询参数**:
- `startDate`: 开始日期，格式：yyyy-MM-dd
- `endDate`: 结束日期，格式：yyyy-MM-dd
- `type`: 统计类型（day/week/month），默认day

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "labels": ["2026-02-01", "2026-02-02", "2026-02-03"],
    "values": [5000.00, 8000.00, 10000.00]
  }
}
```

---

### 4. 热门农家乐

**GET** `/api/admin/dashboard/top-farmhouses`

**描述**: 获取热门农家乐排行

**认证**: 需要管理员权限

**查询参数**:
- `limit`: 返回数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "田园风光农家乐",
      "orderCount": 500,
      "revenue": 99000.00,
      "rating": 4.8
    }
  ]
}
```

---

### 5. 评分分布

**GET** `/api/admin/dashboard/rating-distribution`

**描述**: 获取评分分布数据

**认证**: 需要管理员权限

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "5": 200,
    "4": 150,
    "3": 50,
    "2": 20,
    "1": 10
  }
}
```

---

## 实体字段说明

### User (用户)
- `id`: 用户ID
- `username`: 用户名
- `nickname`: 昵称
- `phone`: 手机号
- `email`: 邮箱
- `avatar`: 头像URL
- `gender`: 性别（0-未知，1-男，2-女）
- `role`: 角色（USER-普通用户，ADMIN-管理员）
- `status`: 状态（0-禁用，1-启用）

### Farmhouse (农家乐)
- `id`: 农家乐ID
- `name`: 名称
- `description`: 详细描述
- `shortDesc`: 简短描述
- `address`: 地址
- `phone`: 联系电话
- `coverImage`: 封面图片URL
- `images`: 图片列表（数组）
- `ownerName`: 老板姓名
- `rating`: 评分（0-5）
- `reviewCount`: 评价数量
- `tags`: 标签（逗号分隔）
- `features`: 特色/设施（逗号分隔）
- `businessHours`: 营业时间
- `sortOrder`: 排序序号
- `status`: 状态（0-下架，1-上架）

### FarmhousePackage (套餐)
- `id`: 套餐ID
- `farmhouseId`: 农家乐ID
- `name`: 套餐名称
- `description`: 套餐描述
- `price`: 现价
- `originalPrice`: 原价
- `coverImage`: 封面图片URL
- `type`: 类型（1-一日游，2-两日游，3-三日游，4-其他）
- `capacity`: 容量（人数）
- `duration`: 时长（天数）
- `includes`: 包含内容（逗号分隔）
- `sortOrder`: 排序序号
- `status`: 状态（0-下架，1-上架）

### Schedule (档期)
- `id`: 档期ID
- `packageId`: 套餐ID
- `farmhouseId`: 农家乐ID
- `scheduleDate`: 档期日期（yyyy-MM-dd）
- `totalQuota`: 总配额
- `remainingQuota`: 剩余配额
- `priceOverride`: 价格覆盖（可选，覆盖套餐价格）
- `status`: 状态（0-禁用，1-启用）

### Reservation (预约)
- `id`: 预约ID
- `reservationNo`: 预约编号
- `userId`: 用户ID
- `farmhouseId`: 农家乐ID
- `packageId`: 套餐ID
- `scheduleId`: 档期ID
- `reserveDate`: 预约日期（yyyy-MM-dd）
- `personCount`: 人数
- `contactName`: 联系人姓名
- `contactPhone`: 联系人电话
- `remark`: 备注
- `status`: 状态（0-待确认，1-已确认，2-已完成，3-已取消）
- `cancelReason`: 取消原因

### Order (订单)
- `id`: 订单ID
- `orderNo`: 订单编号
- `userId`: 用户ID
- `reservationId`: 预约ID
- `farmhouseId`: 农家乐ID
- `packageId`: 套餐ID
- `farmhouseName`: 农家乐名称
- `packageName`: 套餐名称
- `personCount`: 人数
- `unitPrice`: 单价
- `totalAmount`: 总金额
- `status`: 状态（0-待支付，1-已支付，2-已完成，3-已取消，4-退款中，5-已退款）
- `paymentMethod`: 支付方式
- `payTime`: 支付时间
- `cancelTime`: 取消时间
- `cancelReason`: 取消原因
- `completeTime`: 完成时间
- `remark`: 备注

### Review (评价)
- `id`: 评价ID
- `userId`: 用户ID
- `farmhouseId`: 农家乐ID
- `orderId`: 订单ID
- `rating`: 评分（1-5）
- `content`: 评价内容
- `images`: 评价图片（数组）
- `status`: 状态（0-待审核，1-已通过，2-已拒绝）
- `adminReply`: 管理员回复
- `replyTime`: 回复时间

### Favorite (收藏)
- `id`: 收藏ID
- `userId`: 用户ID
- `farmhouseId`: 农家乐ID

### Announcement (公告)
- `id`: 公告ID
- `title`: 标题
- `content`: 内容
- `type`: 类型（1-系统公告，2-活动公告）
- `coverImage`: 封面图片URL
- `startTime`: 开始时间
- `endTime`: 结束时间
- `status`: 状态（0-禁用，1-启用）
- `sortOrder`: 排序序号

### Notification (通知)
- `id`: 通知ID
- `userId`: 用户ID
- `title`: 标题
- `content`: 内容
- `type`: 类型（1-预约通知，2-订单通知，3-评价通知，4-系统通知）
- `isRead`: 是否已读（0-未读，1-已读）

---

## 错误码说明

- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未认证或token过期
- `403`: 无权限访问
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 注意事项

1. 所有需要认证的接口，请在请求头中添加：`Authorization: Bearer {token}`
2. 请求和响应的Content-Type均为 `application/json`，文件上传接口除外
3. 日期格式统一使用 `yyyy-MM-dd` 或 `yyyy-MM-ddTHH:mm:ss`
4. 分页参数：`page` 从1开始，`size` 默认10
5. 状态码为0通常表示禁用/待处理，1表示启用/已处理
6. 管理员接口需要用户角色为 `ADMIN`

---

**文档版本**: v1.0  
**最后更新**: 2026-02-08
