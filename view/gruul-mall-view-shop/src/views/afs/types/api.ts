import { ApiOrder, OrderDiscount, OrderPayment, ShopOrder, OrderReceiver, ORDERSTATUS, ORDERTYPE } from '@/views/order/types/order'
import { DeliverType } from '@/views/order/orderShipment/types'
import { A_REfUND_WHY, ARefundType, AFSSTATUS, PACKAGESTATUS } from '@/views/afs/types'
export type { ApiAfsOrder, ApiHistory, ApiBuyersData, ApiShopInfo, ProcessingHistory }
/**
 * 后端返回订单详情数据
 */
interface ApiAfsOrder {
    histories: []
    buyerId: string
    createTime: string
    id: string
    no: string
    orderDiscounts: OrderDiscount[]
    orderPayment: OrderPayment
    orderReceiver: OrderReceiver
    payTimeOut: string
    shopOrders: ShopOrder[]
    status: keyof typeof ORDERSTATUS
    type: keyof typeof ORDERTYPE
    updateTime: string
    shopOrderPackages: ApiLogistics01[]
    keyNodeTimeout: AfsKeyNodeTimeout
    icStatus?: keyof typeof IcStatus // 同城配送状态
}
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

interface AfsKeyNodeTimeout {
    commentTimeout: string
    confirmTimeout: string
    payTimeout: string
}
/**
 * 后端返回协商历史信息
 */
interface ApiHistory {
    afsOrderItem: AfsOrderItem
    overdueTime: string
    buyerId: string
    buyerAvatar: string
    buyerNickname: string
    buyerPhone: string
    createTime: string
    explain: string
    histories: History[]
    id: string
    no: string
    orderNo: string
    packageStatus: keyof typeof PACKAGESTATUS
    reason: keyof typeof A_REfUND_WHY
    refundAmount: string
    keyNodeTimeout: KeyNodeTimeout
    shopId: string
    shopLogo: string
    shopName: string
    shopOrderItemId: string
    status: keyof typeof AFSSTATUS
    type: keyof typeof ARefundType
    updateTime: string
    version: number
    supplierId?: string
}
interface KeyNodeTimeout {
    confirmReturnedTimeout: string
    requestAgreeTimeout: string
    returnedTimeout: string
}
interface AfsOrderItem {
    createTime: string
    dealPrice: string
    image: string
    num: number
    productId: string
    productName: string
    salePrice: string
    services: string[]
    skuId: string
    specs: string[]
    updateTime: string
    version: number
}
interface History {
    afsStatus: keyof typeof AFSSTATUS
    createTime: string
    evidences: string[]
    id: string
    packageStatus: keyof typeof PACKAGESTATUS
    remark: string
    updateTime: string
}
/**
 *  后端返回买家信息
 */
interface ApiBuyersData {
    avatar: string
    nickname: string
    userId: string
    phone: string
}
interface ApiShopInfo {
    id: string
    logo: string
    name: string
}
/**
 * 加工后的协商历史数据
 */
interface ProcessingHistory extends History {
    isConsumer?: boolean
    afsStatusCn?: string
    reason?: keyof typeof A_REfUND_WHY
    refundAmount?: string
    afsStatus: keyof typeof AFSSTATUS
    createTime: string
    evidences: string[]
    id: string
    logo?: string
    name?: string
    packageStatus: keyof typeof PACKAGESTATUS
    remark: string
    updateTime: string
    type?: string
    historyBuyerReturnedInfo?: Obj
}
export interface ApiLogistics01 {
    createTime: string
    deleted: false
    deliveryTime?: string
    expressCompanyCode: string
    expressCompanyName: string
    expressNo: string
    id: string
    orderNo: string
    receiverAddress: string
    receiverMobile: string
    receiverName: string
    remark: string
    shopId: string
    status: keyof typeof PACKAGESTATUS
    type: keyof typeof DeliverType
    updateTime: string
    version: 0
    // 确认收货时间
    confirmTime?: string
    //  评论时间
    commentTime?: string
    success: true
}
