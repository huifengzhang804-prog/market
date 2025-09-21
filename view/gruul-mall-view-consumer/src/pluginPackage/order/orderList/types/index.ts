import type { AddressItemType } from '@/apis/address/model'
import { AFSSTATUS } from '@pluginPackage/order/applyAfterSales/types'
import { ORDERPAYMENT } from '@/apis/paymentDetail/model'
import type { ApiLogistics01 } from '@pluginPackage/order/orderDetail/types'
import { type DistributionKeyType, SellType } from '@/apis/good/model'
import type { FormType } from '@pluginPackage/order/confirmOrder/types'
import { IcStatus } from '@/hooks'

export { IcStatus }

/**
 * 订单状态
 * @param  UNPAID 未支付
 * @param  PAID 已支付
 * @param  BUYER_CLOSED 买家关闭订单
 * @param  SYSTEM_CLOSED 系统关闭订单
 * @param  SELLER_CLOSED 卖家关闭订单
 * @param  TEAMING 拼团中
 * @param  TEAM_FAIL 拼团失败
 */
export enum ORDERSTATUS {
  UNPAID,
  PAID,
  BUYER_CLOSED,
  SYSTEM_CLOSED,
  SELLER_CLOSED,
  TEAMING,
  TEAM_FAIL,
}

/**
 * 订单tab状态
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

enum SHOPITEMSTATUS {
  OK,
  CLOSED,
}

/**
 * 订单类型
 * @param COMMON 普通商品
 * @param SPIKE 秒杀
 * @param TEAM  拼团
 */
enum ORDERTYPE {
  COMMON,
  SPIKE,
  TEAM,
}

/**
 * 订单支付状态
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
 * 优惠类型
 * @param PLATFORM_COUPON 平台优惠券
 * @param SHOP_COUPON 店铺优惠券
 * @param FULL_REDUCTION 店铺满减
 */
export enum DISCOUNTSOURCETYPE {
  PLATFORM_COUPON,
  SHOP_COUPON,
  MEMBER_DEDUCTION,
  FULL_REDUCTION,
  CONSUMPTION_REBATE,
}

/**
 * 优惠状态
 */
enum DISCOUNTSOURCESTATUS {
  NORMAL,
  CLOSED,
}

/**
 * 商铺订单状态
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
 * 包裹状态
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
 * 包裹配置方式
 * @param EXPRESS 快递
 */
enum PACKAGETYPE {
  EXPRESS,
}

/**
 * 订单类型
 * @param {string} buyerId 买家用户id
 * @param {string} no 订单号
 * @param {ORDERSTATUS} status 订单状态
 * @param {ORDERTYPE} type 订单类型
 * @param {OrderPayment} orderPayment 订单支付相关信息
 * @param {OrderDiscount} orderDiscounts 订单优惠相关
 * @param {ShopOrder} shopOrders 店铺订单相关
 */
export interface ApiOrder {
  id: string
  buyerId: string
  no: string
  createTime: string
  updateTime: string
  status: keyof typeof ORDERSTATUS
  type: keyof typeof ORDERTYPE
  remark: string
  timeout: KeyNodeTimeout
  orderPayment: OrderPayment
  orderDiscounts: OrderDiscount[]
  shopOrders: ShopOrder[]
  payTimeOut?: string
  orderReceiver: AddressItemType
  extra: {
    teamNo?: string
    distributionMode: DistributionKeyType
    packUpTime?: string
    shopStoreId?: string
    address?: string
    contractNumber?: string
  }
  shopOrderPackages?: ApiLogistics01[]
  icStatus?: keyof typeof IcStatus // 同城配送状态
}

interface KeyNodeTimeout {
  commentTimeout: string
  confirmTimeout: string
  payTimeout: string
  confirmTimeoutMills: string
  payTimeoutMills: string
  commentTimeoutMills: string
}

export type OrderInfo = ApiOrder

/**
 * 支付相关信息
 * @param payerId 支付用户id
 * @param type 支付类型
 * @param status 支付状态
 * @param totalAmount 订单总金额
 * @param freightAmount 总运费
 * @param discountAmount 优惠总金额
 * @param payTime 支付时间
 * @param payAmount 支付总金额金额 = 订单总金额 - 优惠总金额
 */
interface OrderPayment {
  id: string
  sn: string
  createTime: string
  shopId: Long
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
 * 订单优惠
 * @param sourceType 优惠类型
 * @param sourceStatus 优惠状态
 * @param sourceId 优惠源Id
 * @param sourceAmount 优惠金额
 * @param sourceDesc 优惠信息描述
 * @param discountItems 优惠项对应商品
 */
export interface OrderDiscount {
  shopId: Long
  orderNo: string
  sourceType: keyof typeof DISCOUNTSOURCETYPE
  sourceStatus: keyof typeof DISCOUNTSOURCESTATUS
  sourceId: string
  sourceAmount: number
  sourceDesc: string
  discountItems: OrderDiscountItem[]
}

/**
 * 优惠对应的商品
 * @param packageId 店铺包裹id
 * @param packageItemId 店铺包裹商品id
 * @param discountId 优惠项id
 */
interface OrderDiscountItem {
  shopId: Long
  packageId: string
  packageItemId: string
  discountId: string
  discountAmount: string
}

/**
 * 订单备注表单
 */
interface Remark {
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
 *
 */
export interface RemarkItem {
  key: string
  value: string
  type: FormType
}

/**
 * 店铺订单相关
 * @param no 店铺订单号
 * @param remark 店铺订单备注
 */
export interface ShopOrder {
  no: string
  status: keyof typeof SHOPORDERSTATUS
  shopId: Long
  orderNo: string
  shopName: string
  shopLogo: string
  remark: Remark
  shopOrderItems: ShopOrderItem[]
  createTime: string
  orderReceiver?: AddressItemType
  updateTime?: string
  id: string
  more: boolean
  totalAmount?: Long
  extra: {
    // 发货时间
    deliverTime: string
    // 收货时间
    receiveTime: string
  }
}

export interface ShopOrderItem {
  afsNo?: string
  afsStatus: keyof typeof AFSSTATUS
  dealPrice: string
  fixPrice: string
  status: keyof typeof SHOPITEMSTATUS
  freightPrice: string
  freightTemplateId: string
  id: string
  image: string
  num: number
  productId: Long
  productName: string
  salePrice: Long
  shopId: Long
  skuId: Long
  specs: string[]
  weight: number
  packageId?: string
  packageStatus: keyof typeof PACKAGESTATUS
  deliverType?: DistributionKeyType
  updateTime?: string
  services: []
  //业务处理字段
  //表示改 item 是否是 多条 item 合并的数据
  merged?: boolean
  /**
   * 商品销售类型
   */
  sellType: keyof typeof SellType
}
