import type { StorageProducts } from '@/pages/modules/car/types'
import calculateFn from '@/pluginPackage/order/confirmOrder/hooks/calculate'
import type { DistributionKeyType } from '@/apis/good/model'
import type { Address } from '@/apis/address/type'

/**
 * 订单类型
 * @param COMMON 普通商品
 * @param SPIKE 秒杀
 * @param TEAM 拼团
 * @param BARGAIN 砍价
 * @param PACKAGE 套餐
 */
export enum ORDER_TYPE {
  COMMON = '普通商品',
  SPIKE = '秒杀',
  TEAM = '拼团',
  BARGAIN = '砍价',
  PACKAGE = '套餐',
}

export type OrderType = keyof typeof ORDER_TYPE

export interface OrderProductType {
  shopId: Long
  shopLogo: string
  shopName: string
  products: StorageProducts[]
}

/**
 * 下单提交表单类型
 * @param receiver 用户地址信息
 * @param shopPackages 商铺包
 * @param activityId 秒杀活动ID
 * @param secKillCode 秒杀随机码
 * @param orderType 订单类型
 */
export interface OrderFormSubmitType {
  receiver: Address
  shopPackages: OrderShopPackage[]
  source: 'CART' | 'PRODUCT'
  orderType: keyof typeof ORDER_TYPE
  discounts: any
  activity: any
  extra: Record<string, string>
  distributionMode: DistributionKeyType
}

export interface OrderShopPackage {
  id: Long
  name: string
  logo: string
  remark: { [x: string]: any }
  products: OrderPackageProductItem[]
}

/**
 * 商品信息
 * @param {string} id 商品id
 * @param {string} skuId 商品分类Id
 */
interface OrderPackageProductItem {
  id: Long
  skuId?: Long
  num: number
}

export type FormType = 'MOBILE' | 'CITIZEN_ID' | 'TEXT' | 'NUMBER' | 'IMAGE' | 'DATE' | 'TIME' | 'DATETIME' | 'REMARK'

interface DealSetting {
  key: string
  placeholder: string
  required: boolean
  type: FormType
}

export interface ApishopDealSetting {
  [x: string]: DealSetting[]
}

/**
 * 组装获取运费所需数据
 */
export interface ReceiverAreaDataParams {
  area: [string, string, string?]
  shopFreights: ShopFreights[]
  //  是否有会员权益
  freeRight: boolean
  address: string
  distributionMode: string[]
}

interface ShopFreights extends Pick<OrderProductType, 'shopId'> {
  freights: Freights[]
}

interface Freights {
  templateId: Long
  skuInfos: SkuInfos[]
}

interface SkuInfos {
  weight: Long
  price: Long
  num: Long
  skuId: Long
}
