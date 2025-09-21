import type { ORDERPAYMENT } from '@/apis/paymentDetail/model'

export interface IOrderList {
  affiliationPlatform: string
  buyerId: string
  buyerNickname: string
  createTime: string
  freightPrice: string
  id: Long
  image: string
  integralOrderReceiver?: IIntegralOrderReceiver
  integralOrderReceiverVO?: IIntegralOrderReceiver
  integralOrderPayment?: {
    freightAmount: number
    payAmount: number
    payIntegral: number
    totalIntegral: number
    payTime: string
    payType: keyof typeof ORDERPAYMENT
  }
  no: string
  num: number
  price: Long
  productName: string
  status: Exclude<keyof typeof orderStatus, ''>
  salePrice: Long
  timeout: { confirmTimeout: string; payTimeout: string }
  expressName?: string
  buyerRemark?: string
  productId?: string
  source?: string
  expressCompanyName?: string
  expressNo?: string
  productType?: string
}

interface IIntegralOrderReceiver {
  address: string
  mobile: string
  name: string
  area: [string, string, string?]
  id?: Long
}

export enum orderStatus {
  UNPAID = '未支付',
  PAID = '待发货',
  ON_DELIVERY = '待收货',
  ACCOMPLISH = '已完成',
  SYSTEM_CLOSED = '已关闭',
  '' = '全部',
}

export interface IIntegralOrderListParams {
  size: number
  current: number
  pages: number
  status: keyof typeof orderStatus
}
export interface IIntegralReceiverData {
  name: string
  mobile: string
  area: [string, string, string?]
  address: string
}
enum GainIntegralType {
  /**
   * 每日登入
   */
  DAY_LOGIN = '每日登入',
  /**
   * 积分商品兑换
   */
  INTEGRAL_PRODUCT_EXCHANGE = '积分商品兑换',

  /**
   * 每日分享
   */
  DAY_SHARE = '每日分享',

  /**
   * 积分清空
   */
  INTEGRAL_CLEAR = '积分清空',

  /**
   * 每日签到
   */
  DAY_SIGN_IN = '每日签到',

  /**
   * 系统充值
   */
  SYSTEM_RECHARGE = '系统充值',

  /**
   * 系统扣除
   */
  SYSTEM_DEDUCT = '系统扣除',

  /**
   * 订单消费
   */
  ORDER_CONSUMPTION = '订单消费',

  /**
   * 订单取消
   */
  ORDER_CANCEL = '订单取消',

  /**
   * 消费所得
   */
  INTEGRAL_CONSUME = '消费所得',
}
enum ChangeType {
  INCREASE = '增加',
  REDUCE = '减少',
}
export interface IntegralDetailInfo {
  changeType: keyof typeof ChangeType
  clear: boolean
  createTime: string
  deleted: false
  gainIntegralType: keyof typeof GainIntegralType
  id: string
  particulars: string
  updateTime: string
  userId: string
  variationIntegral: string
}
