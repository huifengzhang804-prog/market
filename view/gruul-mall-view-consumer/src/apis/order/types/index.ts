import type { Address, Geometry } from '@/apis/address/type'
import { IcStatus } from '@/pluginPackage/order/orderList/types'

/**
 * 选择的配送方式
 */
export const enum DistributionMode {
  MERCHANT = 'MERCHANT',
  EXPRESS = 'EXPRESS',
  INTRA_CITY_DISTRIBUTION = 'INTRA_CITY_DISTRIBUTION',
  SHOP_STORE = 'SHOP_STORE',
  VIRTUAL = 'VIRTUAL',
}

/**
 * 订单类型
 */
const enum OrderType {
  COMMON = 'COMMON',
  SPIKE = 'SPIKE',
  TEAM = 'TEAM',
  BARGAIN = 'BARGAIN',
  PACKAGE = 'PACKAGE',
}

/**
 * 订单来源
 */
const enum Source {
  PRODUCT = 'PRODUCT',
  CART = 'CART',
}

/**
 * OrderShopsDTO
 */
export interface OrderShopsDTORequest {
  /**
   * Return int64.
   */
  empty?: number
  /**
   * 活动参数
   */
  activity?: ActivityParam
  /**
   * 优惠信息
   * key-》优惠类型  优惠券/满减
   * value-》优惠信息
   */
  discounts?: Obj
  /**
   * 选择的配送方式
   */
  distributionMode: keyof typeof DistributionMode
  /**
   * 额外数据
   */
  extra?: JSONObject
  /**
   * 订单类型
   */
  orderType: keyof typeof OrderType
  /**
   * 收货地址详情
   */
  receiver?: Address
  /**
   * 选购的店铺id
   */
  shopPackages: ShopPackageDTO[]
  /**
   * 订单来源
   */
  source: keyof typeof Source
}

/**
 * 活动参数
 */
interface ActivityParam {
  /**
   * 活动id 入参
   */
  activityId?: number
  /**
   * 额外参数 入参
   */
  extra?: Obj
  /**
   * 订单号 业务处理数据
   */
  orderNo?: string
  /**
   * skuKeys map集合
   * key: skuKey
   * value: 数量
   */
  skuKeyMap?: Obj
  /**
   * 买家id 业务处理数据
   */
  userId?: number
}

/**
 * 额外数据
 */
interface JSONObject {
  /**
   * 配置项
   */
  config?: JSONConfig
  /**
   * No comments found.(map data)
   */
  raw?: Obj
}

/**
 * 配置项
 */
interface JSONConfig {
  /**
   * 是否检查重复key
   */
  checkDuplicate?: boolean
  /**
   * 日期格式，null表示默认的时间戳
   */
  dateFormat?: string
  /**
   * 是否忽略键的大小写
   */
  ignoreCase?: boolean
  /**
   * 是否忽略转换过程中的异常
   */
  ignoreError?: boolean
  /**
   * 是否忽略null值
   */
  ignoreNullValue?: boolean
  /**
   * 键排序规则，{@code null}表示不排序，不排序情况下，按照加入顺序排序
   */
  keyComparator?: Obj
  /**
   * 是否去除末尾多余0，例如如果为true,5.0返回5
   */
  stripTrailingZeros?: boolean
  /**
   * 是否支持transient关键字修饰和@Transient注解，如果支持，被修饰的字段或方法对应的字段将被忽略。
   */
  transientSupport?: boolean
}

/**
 * ShopPackageDTO
 */
interface ShopPackageDTO {
  /**
   * 店铺id
   */
  id: Long
  /**
   * 店铺logo
   */
  logo: string
  /**
   * 店铺名称
   */
  name: string
  /**
   * 商品列表
   */
  products: ProductDTO[]
  /**
   * 备注
   */
  remark: Obj
}

/**
 * ProductDTO
 */
interface ProductDTO {
  /**
   * 购买的商品id
   */
  id: Long
  /**
   * 选购数量
   */
  num: number
  /**
   * 商品属性
   */
  productFeaturesValue?: Obj
  /**
   * 选择购买的商品SKU 信息
   */
  skuId?: Long
}

export interface IcOrderDetail {
  // 收货人位置
  receiverLocation: Geometry

  // 配送历史 用户查询只有一条，管理端查询会有多条数据
  orders: IcOrder[]
}
interface IcOrder {
  // 配送单状态
  status: keyof typeof IcStatus
  // 配送方类型
  type: keyof typeof IcType
  // 各状态时间节点
  times: IcOrderTimes
  remark?: string
  courier?: IcOrderCourier // 配送员信息
  errorHandler?: IcOrderErrorHandler // 异常状态处理
  pickupCode: number // 取餐码
  deliverSeconds: number // 配送时长 s
  statusDesc: string // 状态描述
}
interface IcOrderCourier {
  name: string
  mobile: string
  avatar?: string
}
interface IcOrderErrorHandler {
  status: keyof typeof IcOrderErrorHandlerStatus
}
const enum IcOrderErrorHandlerStatus {
  RESHIP = 'RESHIP', // 重新发货
  DELIVERED = 'DELIVERED', // 已送达（待收货）
  CLOSE_REFUND = 'CLOSE_REFUND', // 关闭且退款
}
export interface IcOrderTimes {
  shippingTime: string // 发货时间
  takeOrderTime?: string // 接单时间
  arrivalShopTime?: string // 到店时间
  pickupTime?: string // 取货时间
  deliveredTime?: string //送达时间
  errorTime?: string // 发生错误的时间
}

const enum IcType {
  // 商家自配
  SELF = 'SELF',
  UUPT = 'UUPT',
}

export interface CourierLoctionInfo {
  courier: IcOrderCourier
  distance: number // 距离目标位置的距离
  location?: Geometry // 定位
  expectTime: string // 预计送达时间
  minutes: number // 预计还有送达时长 单位分钟 为负代表已超时
}
