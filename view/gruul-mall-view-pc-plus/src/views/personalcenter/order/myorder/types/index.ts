//import { AFSSTATUS } from '@/basePackage/pages/applyAfterSales/types'
export interface ApiLogistics01 {
  createTime: string
  deleted: boolean
  expressCompanyCode: string
  expressCompanyName: string
  expressNo: string
  id: string
  orderNo: string
  receiverAddress: string
  receiverMobile: string
  receiverName: string
  remark: Remark
  shopId: string
  status: keyof typeof PACKAGESTATUS
  type: keyof typeof DeliverType
  updateTime: string
  version: 0
  success: boolean
}
export interface AddressItemType {
  id: string
  address: string
  areaCode: string[]
  isDefault: boolean
  mobile: string
  name: string
}
/**
 * 订单备注表单
 */
export interface Remark {
  //订单播报通知
  orderNotify: boolean
  //平台备注
  platform?: string
  //店铺备注
  shop?: string
  //供应商备注
  supplier?: string
  //表单备注项
  items?: RemarkItem[]
}

/**
 * 备注项
 */
export interface RemarkItem {
  key: string
  value: string
  type: FormType
}
/**
 * 备注表单类型
 */
export enum FormType {
  MOBILE = 'MOBILE',
  CITIZEN_ID = 'CITIZEN_ID',
  TEXT = 'TEXT',
  NUMBER = 'NUMBER',
  IMAGE = 'IMAGE',
  DATE = 'DATE',
  TIME = 'TIME',
  DATETIME = 'DATETIME',
  REMARK = 'REMARK',
}
/**
 * @param: UNFINISHED  未处理完成
 * @param: ACCOMPLISH 已处理完成
 * @returns {*}
 */
export enum NotifyStatus {
  UNFINISHED,
  ACCOMPLISH,
}
/**
 * @param: WITHOUT  无需物流发货
 * @param: EXPRESS 普通发货 自己填 物流公司与 单号
 * @param: PRINT_EXPRESS 打印发货
 * @returns {*}
 */
export enum DeliverType {
  WITHOUT,
  EXPRESS,
  PRINT_EXPRESS,
}
/**
 * @description: 订单状态
 * @param  UNPAID 未支付
 * @param  PAID 已支付
 * @param  BUYER_CLOSED 买家关闭订单
 * @param  SYSTEM_CLOSED 系统关闭订单
 * @param  SELLER_CLOSED 卖家关闭订单
 */
export enum ORDERSTATUS {
  UNPAID,
  PAID,
  BUYER_CLOSED,
  SYSTEM_CLOSED,
  SELLER_CLOSED,
  TEAMING,
  ACCOMPLISH,
  ON_DELIVERY,
}
/**
 * @description: 订单tab状态
 * @param UNPAID 待支付
 * @param UN_DELIVERY 待发货
 * @param UN_RECEIVE 待收货
 * @param COMPLETED 已完成
 * @param CLOSED 已关闭
 * @param UN_COMMENT 待评价
 */
export enum QUERYORDERSTATUS {
  UNPAID,
  UN_DELIVERY,
  UN_RECEIVE,
  COMPLETED,
  UN_COMMENT,
  CLOSED,
}
export enum SHOPITEMSTATUS {
  OK,
  CLOSED,
}
/**
 * @description: 订单类型
 * @param COMMON 商品订单
 * @param SPIKE 秒杀
 */
enum ORDERTYPE {
  COMMON,
  SPIKE,
  TEAM,
}
export enum ORDERPAYMENT {
  WECHAT,
  ALIPAY,
  BALANCE,
}
/**
 * @description: 订单支付状态
 * @param CLOSED 取消支付
 * @param UNPAID 未支付
 * @param PAID 已支付
 */
enum ORDERPAYMENTSTATUS {
  CLOSED,
  UNPAID,
  PAID,
}
/**
 * @description: 优惠类型
 * @param PLATFORM_COUPON 平台优惠券
 * @param SHOP_COUPON 店铺优惠券
 */
enum DISCOUNTSOURCETYPE {
  PLATFORM_COUPON,
  SHOP_COUPON,
}
/**
 * @description: 优惠状态
 */
enum DISCOUNTSOURCESTATUS {
  NORMAL,
  CLOSED,
}
/**
 * @description: 商铺订单状态
 * @param SYSTEM_CLOSED 系统关闭
 * @param BUYER_CLOSED  买家关闭订单
 * @param SELLER_CLOSED  卖家关闭订单
 */
export enum SHOPORDERSTATUS {
  OK,
  SYSTEM_CLOSED,
  BUYER_CLOSED,
  SELLER_CLOSED,
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
 * @description: 包裹配置方式
 * @param EXPRESS 快递
 */
enum PACKAGETYPE {
  EXPRESS,
}
/**
 * @description: 订单类型
 * @param {string} buyerId 买家用户id
 * @param {string} no 订单号
 * @param {ORDERSTATUS} status 订单状态
 * @param {ORDERTYPE} type 订单类型
 * @param {OrderPayment} orderPayment 订单支付相关信息
 * @param {OrderDiscount} orderDiscounts 订单优惠相关
 * @param {ShopOrder} shopOrders 店铺订单相关
 */
export interface ApiOrder {
  icStatus: undefined
  id: string
  shopId: string
  buyerId: string
  no: string
  createTime: string
  updateTime: string
  distributionMode: string
  status: keyof typeof ORDERSTATUS
  type: keyof typeof ORDERTYPE
  remark: Remark
  keyNodeTimeout: KeyNodeTimeout
  orderPayment: OrderPayment
  orderDiscounts: OrderDiscount[]
  shopOrders: ShopOrder[]
  payTimeOut?: string
  orderReceiver: AddressItemType
  shopOrderPackages?: any[]
  extra: any
  // shopOrderPackages?: ApiLogistics01[]
}
export interface KeyNodeTimeout {
  commentTimeout: string
  confirmTimeout: string
  payTimeout: string
  confirmTimeoutMills: string
  payTimeoutMills: string
  commentTimeoutMills: string
}
export type OrderInfo = ApiOrder & ShopOrder
/**
 * @description: 支付相关信息
 * @param payerId 支付用户id
 * @param type 支付类型
 * @param status 支付状态
 * @param totalAmount 订单总金额
 * @param freightAmount 总运费
 * @param discountAmount 优惠总金额
 * @param payTime 支付时间
 * @param payAmount 支付总金额金额 = 订单总金额 - 优惠总金额
 */
export interface OrderPayment {
  id: string
  sn: string
  createTime: string
  shopId: string
  orderNo: string
  payerId: string
  type: keyof typeof ORDERPAYMENT
  totalAmount: string
  freightAmount: string
  discountAmount: string
  payAmount: string
  payTime: string
}
/**
 * @description: 订单优惠
 * @param sourceType 优惠类型
 * @param sourceStatus 优惠状态
 * @param sourceId 优惠源Id
 * @param sourceAmount 优惠金额
 * @param sourceDesc 优惠信息描述
 * @param discountItems 优惠项对应商品
 */
export interface OrderDiscount {
  shopId: string
  orderNo: string
  sourceType: keyof typeof DISCOUNTSOURCETYPE
  sourceStatus: keyof typeof DISCOUNTSOURCESTATUS
  sourceId: string
  sourceAmount: number
  sourceDesc: string
  discountItems: OrderDiscountItem[]
}
/**
 * @description: 优惠对应的商品
 * @param packageId 店铺包裹id
 * @param packageItemId 店铺包裹商品id
 * @param discountId 优惠项id
 */
interface OrderDiscountItem {
  shopId: string
  packageId: string
  packageItemId: string
  discountId: string
  discountAmount: string
}
/**
 * @description: 店铺订单相关
 * @param no 店铺订单号
 * @param remark 店铺订单备注
 */
export interface ShopOrder {
  shopType: string
  updateTime: string
  no: string
  status: keyof typeof SHOPORDERSTATUS
  shopId: string
  orderNo: string
  shopName: string
  shopLogo: string
  remark: Remark
  shopOrderItems: ShopOrderItem[]
  createTime: string
  orderReceiver?: AddressItemType
  id: string
}
export interface ShopOrderItem {
  updateTime: any
  afsNo?: string
  afsStatus: keyof typeof AFSSTATUS
  dealPrice: string
  status: keyof typeof SHOPITEMSTATUS
  freightPrice: string
  freightTemplateId: string
  id: string
  image: string
  num: number
  orderNo: string
  productId: string
  productName: string
  salePrice: string
  shopId: string
  skuId: string
  specs: string[]
  weight: number
  packageId?: string
  packageStatus: keyof typeof PACKAGESTATUS
  services: []
  fixPrice: string
}

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
