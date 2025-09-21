import { SellTypeEnum } from '@/views/good/types'
import { Remark } from '@apis/order/types'

export enum IcStatus {
    '' = '',
    /**
     * 运费价格计算 待发单（未发货）
     * 这种状态的单子可以随时丢弃
     */
    PRICE_PADDING = '待发货',
    /**
     * 待接单
     */
    PENDING = '待接单',
    /**
     * 已接单待到店
     */
    TAKEN = '待到店',
    /**
     * 已到店 待取货
     */
    ARRIVED_SHOP = '待取货',
    /**
     * 已取货 配送中
     */
    PICKUP = '配送中',
    /**
     * 已送达
     */
    DELIVERED = '已送达',
    /**
     * 异常 配送异常
     */
    ERROR = '配送异常',
}

export enum IcStatusText {
    '' = '',
    PRICE_PADDING = '商品待发货',
    PENDING = '等待骑手接单',
    TAKEN = '骑手正在赶往商家',
    ARRIVED_SHOP = '骑手正在店内取货',
    PICKUP = '骑手正在送货',
    DELIVERED = '商品已送达',
    ERROR = '配送异常',
}

export interface OrderDetail {
    buyerId: string
    buyerNickname: string
    createTime: string
    extra: DataExtra
    id: string
    no: string
    orderDiscounts: any[]
    orderPayment: OrderPayment
    orderReceiver: OrderReceiver
    platform: string
    shopOrderPackages: any[]
    shopOrders: ShopOrder[]
    status: string
    timeout: Timeout
    type: string
    updateTime: string
    icStatus?: keyof typeof IcStatus // 同城配送状态
}

export interface DataExtra {
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
    payerId: string
    payTime: string
    totalAmount: number
    type: string
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
    createTime?: string
    discountAmount?: number
    extra?: { [key: string]: any }
    freightAmount?: number
    id?: string
    no: string
    remark?: Remark
    shopId?: string
    shopLogo?: string
    shopName?: string
    shopOrderItems: ShopOrderItem[]
    status?: string
    totalAmount?: number
    updateTime?: string
    orderReceiver?: string
}

export interface ShopOrderItem {
    afsStatus?: string
    dealPrice?: number
    fixPrice?: number
    freightPrice?: number
    freightTemplateId?: number
    id: string
    packageId?: string
    image?: string
    num?: number
    packageStatus?: string
    productId?: string
    productName?: string
    salePrice?: number
    sellType: keyof typeof SellTypeEnum
    services?: string[]
    shopId?: string
    skuId?: string
    specs?: string[]
    status?: string
    updateTime?: string
    weight?: number
}

export interface Timeout {
    afsAuditTimeout: number
    commentTimeout: number
    confirmTimeout: number
    payTimeout: number
}
