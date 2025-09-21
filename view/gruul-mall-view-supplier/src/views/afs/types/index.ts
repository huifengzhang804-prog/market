export { A_REfUND_WHY, ARefundType, ApiOrderAfsItem }

/**
 * @param NONE 无售后
 * @param REFUND_REQUEST 申请退款
 * @param SYSTEM_REFUND_AGREE 系统自动同意退款申请
 * @param REFUND_AGREE 已同意退款申请
 * @param REFUND_REJECT 拒绝了退款申请
 * @param REFUNDED 退款已到账
 * @param RETURN_REFUND_REQUEST 申请退货退款
 * @param SYSTEM_RETURN_REFUND_AGREE 系统自动同意退货退款申请
 * @param RETURN_REFUND_AGREE 已同意退货退款申请
 * @param RETURN_REFUND_REJECT 拒绝了退货退款申请
 * @param SYSTEM_RETURNED_REFUND_CONFIRM 退货退款 系统自动确认收货
 * @param RETURNED_REFUND   退货已发出
 * @param SYSTEM_CLOSED 系统自动关闭
 * @param RETURNED_REFUND_CONFIRM 确认退货已收到
 * @param RETURNED_REFUND_REJECT 已拒绝收货
 * @param  RETURNED_REFUNDED 退货退款已完成
 * @param  BUYER_CLOSED  主动撤销了售后申请
 * @returns {*}
 */
export enum AFSSTATUS {
    NONE,
    REFUND_REQUEST,
    SYSTEM_REFUND_AGREE,
    REFUND_AGREE,
    REFUND_REJECT,
    SYSTEM_RETURN_REFUND_AGREE,
    REFUNDED,
    SYSTEM_RETURNED_REFUND_CONFIRM,
    RETURN_REFUND_REQUEST,
    RETURN_REFUND_AGREE,
    RETURN_REFUND_REJECT,
    RETURNED_REFUND,
    SYSTEM_CLOSED,
    RETURNED_REFUND_CONFIRM,
    RETURNED_REFUND_REJECT,
    RETURNED_REFUNDED,
    BUYER_CLOSED,
}
/**
 * @description: 退款原因
 * @param DONT_NEED_IT  '多拍/拍错/不想要'
 * @param WRONG_FORM  '地址/联系电话 填写错误'
 * @param EXPRESS_STAGNATED  '快递停滞/未送达'
 * @param EXPRESS_REJECTION  '货物破损已拒签'
 * @param SEVEN_DAYS_NO_REASON  '七天无理由退货（商品无损）'
 * @param WRONG_PRODUCT_DESC  '尺寸/容量/参数与商品描述不符'
 * @param DONT_LIKE_IT  '不喜欢或效果不好'
 * @param SOME_MISSING  '缺件/漏发'
 * @param QUALITY_PROBLEM  '质量问题'
 * @param WRONG_PRODUCT  '商家发错货'
 *
 */
enum A_REfUND_WHY {
    DONT_NEED_IT,
    WRONG_FORM,
    EXPRESS_STAGNATED,
    EXPRESS_REJECTION,
    SEVEN_DAYS_NO_REASON,
    WRONG_PRODUCT_DESC,
    DONT_LIKE_IT,
    SOME_MISSING,
    QUALITY_PROBLEM,
    WRONG_PRODUCT,
}
/**
 * @description: 退款类型
 * @param REFUND   仅退款
 * @param RETURN_REFUND  退货退款
 */
enum ARefundType {
    REFUND,
    RETURN_REFUND,
}
/**
 * @description: 包裹状态
 * @param  WAITING_FOR_DELIVER 待发货
 * @param WAITING_FOR_RECEIVE  待收货
 * @param BUYER_WAITING_FOR_COMMENT 买家确认收货 待评价
 * @param SYSTEM_WAITING_FOR_COMMENT 系统确认收货 待评价
 * @param BUYER_COMMENTED_COMPLETED 买家已评论 已完成
 * @param SYSTEM_COMMENTED_COMPLETED 系统自动好评 已完成
 */
export enum PACKAGESTATUS {
    WAITING_FOR_DELIVER,
    WAITING_FOR_RECEIVE,
    BUYER_WAITING_FOR_COMMENT,
    SYSTEM_WAITING_FOR_COMMENT,
    BUYER_COMMENTED_COMPLETED,
    SYSTEM_COMMENTED_COMPLETED,
}
/**
 * @description: 售后列表接口返回
 * @returns {*}
 */
interface ApiOrderAfsItem {
    afsOrderReceiver?: AfsOrderReceiver
    afsOrderItem: AfsOrderItem
    buyerId: string
    buyerNickname: string
    createTime: string
    explain: string
    id: string
    no: string
    orderNo: string
    keyNodeTimeout: KeyNodeTimeout
    packageStatus: keyof typeof PACKAGESTATUS
    reason: keyof typeof A_REfUND_WHY
    refundAmount: string
    shopId: string
    shopOrderItemId: string
    status: keyof typeof AFSSTATUS
    type: keyof typeof ARefundType
    updateTime: string
    packageId?: string
    shopName?: string
    remark: string
    shopLogo?: string
    statusContent?: string
}
interface KeyNodeTimeout {
    confirmReturnedTimeout: string
    requestAgreeTimeout: string
    returnedTimeout: string
}
interface AfsOrderReceiver {
    address: string
    area: [string, string, string?]
    mobile: string
    name: string
}
interface AfsOrderItem {
    createTime: string
    dealPrice: string
    image: string
    num: number
    productId: string
    productName: string
    salePrice: string
    skuId: string
    specs: string[]
    updateTime: string
    version: 0
}
