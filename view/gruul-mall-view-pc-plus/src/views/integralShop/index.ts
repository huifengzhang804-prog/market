export interface IntegralGoodsType {
  createTime: string
  id: string
  integralPrice: string
  name: string
  pic: string
  salesVolume?: string
  status: 'SELL_ON' | 'SELL_OFF'
  stock: number
  price: number | string
  salePrice: string
}

export interface IntegralGoodsInfoType extends IntegralGoodsType {
  albumPics?: string[] | string
  detail?: string
  freightPrice: number
  integralProductAttributes: IntegralProductAttributes[]
  virtualSalesVolume: number
  price: string
  salePrice: string
}
export interface IntegralProductAttributes {
  attributeName: string
  attributeValue: string
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

/**
 * @description:
 * @param useRule: string 积分使用规则
 * @param indate: number 积分有效期
 * @param ruleInfo: string 积分值信息
 * @param integralGainRule: array 积分值信息
 */
export interface IntegralRulesParameter {
  useRule: string
  indate: number
  ruleInfo: string
  integralGainRule: IntegralGainRule[]
}
/**
 * @description:
 * @param rulesParameter: object 获取积分规则参数
 * @param gainRuleType: enum 	获取积分规则类型
 * @param open: boolean 是否开启
 */
export interface IntegralGainRule {
  rulesParameter: RulesParameter
  gainRuleType: keyof typeof GAINRULETYPE
  open: boolean
}
/**
 * @description:
 * @param basicsValue: number 基础值
 * @param extendValue:  HashMap<连续登入天数,对应积分值>
 */
export interface RulesParameter {
  basicsValue: number
  extendValue: {
    [x: number]: number
  }
}
export enum GAINRULETYPE {
  SHARE = '分享',
  LOGIN = '登入',
  SING_IN = '签到',
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
export interface IOrderList {
  buyerId: string
  buyerNickname: string
  createTime: string
  freightPrice: string
  id: string
  image: string
  integralOrderReceiver?: IIntegralOrderReceiver
  integralOrderReceiverVO?: IIntegralOrderReceiver
  no: string
  num: number
  price: string
  productName: string
  status: keyof typeof orderStatus
  salePrice: string
  timeout: { confirmTimeout: string; payTimeout: string }
  expressName?: string
  buyerRemark?: string
  productId?: string
  source?: string
  expressCompanyName?: string
  expressNo?: string
}

export interface IIntegralOrderReceiver {
  address: string
  mobile: string
  name: string
  areaCode: string[]
  id?: string
}
export interface IIntegralReceiverData {
  name: string
  mobile: string
  areaCode?: string[]
  address: string
}
enum ChangeType {
  INCREASE = '增加',
  REDUCE = '减少',
}
