import { ShopOrder, OrderReceiver, ORDERPAYMENT, ORDERPAYMENTSTATUS, ORDERTYPE } from '@/views/order/types/order'
import { A_REfUND_WHY, ARefundType, AFSSTATUS, PACKAGESTATUS } from '@/views/afs/types'
/**
 * @param: WITHOUT  无需物流发货
 * @param: EXPRESS 普通发货 自己填 物流公司与 单号
 * @param: PRINT_EXPRESS 打印发货
 */
export enum DeliverType {
    WITHOUT,
    EXPRESS,
    PRINT_EXPRESS,
}
/**
 * 后端返回售后列表查看详情订单数据
 */
export interface ApiAfsOrder {
    buyerId: string
    createTime: string
    id: string
    no: string
    orderDiscounts: []
    orderPayment: OrderPayment
    orderReceiver: OrderReceiver
    payTimeOut: string
    shopOrders: ShopOrder[]
    status: keyof typeof ORDERPAYMENTSTATUS
    type: keyof typeof ORDERTYPE
    shopOrderPackages: ApiLogistics01[]
    updateTime: string
    histories?: []
}
interface OrderPayment {
    createTime: string
    discountAmount: string
    freightAmount: string
    id: string
    payAmount: string
    payTime: string
    payerId: string
    sn: string
    totalAmount: string
    type: keyof typeof ORDERPAYMENT
}
/**
 * 后端返回的User接口信息
 */
export interface ApiBuyersData {
    avatar?: string
    nickname?: string
    userId?: string
    phone?: string
    gender?: string
    createTime?: string
    updateTime?: string
    version?: number
    deleted?: boolean
    id?: string
}
/**
 * 后端返回协商历史信息
 */
export interface ApiHistory {
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
interface ApiShopInfo {
    id: string
    logo: string
    name: string
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
