export { ParamsAfs, UploadItem, A_REfUND_WHY, ARefundType, DescribeTheReason }
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
/** 申请售后携带参数
 * @param orderNo 订单号
 * @param shopId 店铺id
 * @param itemId 订单商品id
 * @param reason 售后原因
 * @param type 售后工单类型
 * @param remark 说明
 * @param refundAmount 申请退款金额
 * @param evidences 图片（url）数组
 * @returns {*}
 */
interface ParamsAfs {
  orderNo: string
  shopId: string
  itemId: string
  reason: string
  type: string
  remark: string
  refundAmount: number
  evidences: string[]
}
/** 上传图片列表的 item对象
 * @param url 预览图片的地址
 * @param error 上传失败，此值为true
 * @param progress 0-100之间的值
 * @returns {*}
 */
interface UploadItem {
  url: string
  error: boolean
  progress: number
  response: {
    code: number
    data: string
    msg: string
    success: boolean
  }
}
/**
 * @description: 退款类型描述
 * @returns {*}
 */
interface DescribeTheReason {
  name: string
  done: boolean
  key: keyof typeof ARefundType | string
}
