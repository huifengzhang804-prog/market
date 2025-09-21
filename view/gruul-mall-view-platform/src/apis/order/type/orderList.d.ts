export interface OrderItem {
    buyerId: string
    buyerNickname: string
    createTime: string
    extra: OrderItemExtra
    id: string
    isPriority: boolean
    no: string
    orderPayment: OrderPayment
    orderReceiver: OrderReceiver
    platform: string
    shopOrders: ShopOrder[]
    status: string
    timeout: Timeout
    type: string
    updateTime: string
}

export interface OrderItemExtra {
    distributionMode: string
    packUpTime: string
    shopStoreId: string
}

export interface OrderPayment {
    createTime: string
    discountAmount: number
    extra: OrderPaymentExtra
    freightAmount: number
    id: string
    payAmount: number
    payerId?: string
    payTime?: string
    totalAmount: number
    type?: string
    updateTime: string
}

export interface OrderPaymentExtra {
    rebatePay: boolean
}

export interface OrderReceiver {
    address: string
    area: string[]
    id: string
    mobile: string
    name: string
}

export interface ShopOrder {
    createTime: string
    id: string
    no: string
    orderNo: string
    remark: string
    shopId: string
    shopLogo: string
    shopMode: string
    shopName: string
    shopOrderItems: ShopOrderItem[]
    shopType: string
    status: string
}

export interface ShopOrderItem {
    afsStatus: string
    createTime: string
    dealPrice: number
    fixPrice: number
    freightPrice: number
    freightTemplateId: number
    id: string
    image: string
    num: number
    packageStatus: string
    productId: string
    productName: string
    salePrice: number
    sellType: string
    shopId: string
    skuId: string
    specs: string[]
    status: string
    weight: number
}

export interface Timeout {
    afsAuditTimeout: number
    commentTimeout: number
    confirmTimeout: number
    payTimeout: number
}
