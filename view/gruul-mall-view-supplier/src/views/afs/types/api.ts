import { ApiOrder, OrderDiscount, OrderPayment, ShopOrder, OrderReceiver, ORDERSTATUS, ORDERTYPE } from '@/views/order/types/order'
import { DeliverType } from '@/views/order/orderShipment/types'
import { A_REfUND_WHY, ARefundType, AFSSTATUS, PACKAGESTATUS } from '@/views/afs/types'
export { ApiAfsOrder, ApiHistory, ApiBuyersData, ApiShopInfo, ProcessingHistory }
/**
 * @description: 后端返回订单详情数据
 * @returns {*}
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
}
interface AfsKeyNodeTimeout {
    commentTimeout: string
    confirmTimeout: string
    payTimeout: string
}
/**
 * @description: 后端返回协商历史信息
 * @returns {*}
 */
interface ApiHistory {
    afsOrderItem: AfsOrderItem
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
 * @description:  后端返回买家信息
 * @returns {*}
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
 * @description: 加工后的协商历史数据
 * @returns {*}
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
    historyBuyerReturnedInfo?: Record<string, any>
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
