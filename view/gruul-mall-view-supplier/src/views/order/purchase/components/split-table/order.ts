export interface ApiPurchaseOrder {
    [key: string]: any
    createTime: string
    no: string
    payAmount: string
    shopId: string
    shopUserId: string
    status: keyof typeof PurchaseOrderStatus
    supplierId: string
    type: 'PURCHASE'
    remark?: string
    extra: {
        receiver: {
            address: string
            mobile: string
            name: string
            area: [string, string, string?]
        }
        pay: {
            orderNo: string
            payType: 'OFFLINE' | 'OFFLINE'
            proof?: string
        }
        remark?: string
    }
    extraInfo: {
        shopPhone: string
        shopName: string
    }
    orderItems: OrderItems[]
    timeNodes: {
        payTime: string
        deliveryTime?: string
        receiveTime?: string
    }
}

export interface OrderItems {
    dealPrice: string
    freightPrice: string
    image: string
    num: number
    orderNo: string
    packageStatus: 'WAITING_FOR_DELIVER' | 'WAITING_FOR_RECEIVE' | 'COMPLETED'
    productName: string
    salePrice: string
    supplierId: string
    specs?: string[]
    id: string
}

export enum PurchaseOrderStatus {
    UNPAID, // 未支付
    PAYMENT_AUDIT, // 支付审核中
    AUDIT_FAIL_CLOSED, // 审核未通过 已关闭
    PAID, // 已支付
    SYSTEM_CLOSED, // 超时未支付 系统关闭
    BUYER_CLOSED, // 买家关闭订单
    SELLER_CLOSED, // 卖家关闭订单
}
