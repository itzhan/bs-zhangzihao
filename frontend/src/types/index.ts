export interface User {
  id: number
  username: string
  nickname: string
  phone: string
  email: string
  avatar: string
  gender: number
  role: string
  status: number
}

export interface Farmhouse {
  id: number
  name: string
  description: string
  shortDesc: string
  address: string
  phone: string
  coverImage: string
  images: string
  ownerName: string
  rating: number
  reviewCount: number
  tags: string
  features: string
  businessHours: string
  status: number
}

export interface FarmhousePackage {
  id: number
  farmhouseId: number
  name: string
  description: string
  price: number
  originalPrice: number
  coverImage: string
  type: number
  capacity: number
  duration: string
  includes: string
  status: number
}

export interface Schedule {
  id: number
  packageId: number
  farmhouseId: number
  scheduleDate: string
  totalQuota: number
  remainingQuota: number
  priceOverride: number
  status: number
}

export interface Reservation {
  id: number
  reservationNo: string
  userId: number
  farmhouseId: number
  packageId: number
  scheduleId: number
  reserveDate: string
  personCount: number
  contactName: string
  contactPhone: string
  remark: string
  status: number
  cancelReason: string
  createTime: string
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  farmhouseId: number
  packageId: number
  farmhouseName: string
  packageName: string
  personCount: number
  unitPrice: number
  totalAmount: number
  status: number
  paymentMethod: string
  payTime: string
  createTime: string
}

export interface Review {
  id: number
  userId: number
  farmhouseId: number
  orderId: number
  rating: number
  content: string
  images: string
  status: number
  adminReply: string
  replyTime: string
  createTime: string
}

export interface Announcement {
  id: number
  title: string
  content: string
  type: number
  coverImage: string
  startTime: string
  endTime: string
  status: number
}

export interface Notification {
  id: number
  userId: number
  title: string
  content: string
  type: number
  isRead: number
  createTime: string
}

// API response types
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageResult<T = unknown> {
  records: T[]
  total: number
}
