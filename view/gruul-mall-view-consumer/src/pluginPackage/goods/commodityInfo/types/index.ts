import type { Ref, ComputedRef } from 'vue'
import type { HooksOfGroupType } from '@pluginPackage/group/hooks/dispatcher'
import type { HooksOfSecKillType } from '@pluginPackage/scondsKill/hooks/dispatcher'
import type { HooksOfBargainType } from '@/pluginPackage/bargain/hooks/dispatcher'
import type { DiscountHooksFn } from '@/pluginPackage/goods/commodityInfo/hooks/useGoodsInfo'
import type { OrderType } from '@/pluginPackage/order/confirmOrder/types'
import type { ChainHandleParam } from '@plugin/types'
import type { ProductExtraDTO as extra, ProductFeaturesValueDTO } from '@/apis/good/model'

export type { extra }

import type {
  ProductResponse,
  StorageSku,
  DiscountType,
  ProductDiscountVO,
  ActivityDetailVO,
  ProductSpecsSkusVO,
  ServicesType,
} from '@/apis/good/model'
import { ServiceBarrier } from '@/apis/good/model'

export type { ProductSpecsSkusVO }

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}

enum Stock {
  UNLIMITED,
  LIMITED,
}

export enum DistributionMode {
  'EXPRESS' = '快递配送',
  'INTRA_CITY_DISTRIBUTION' = '同城配送',
  'SHOP_STORE' = '到店自提',
  'VIRTUAL' = '无需物流',
}

export { type ServicesType, ServiceBarrier }

export type { ProductResponse, StorageSku }

/**
 * @param {number} productId 商品id
 * @param {number} productPic 展示图片
 * @param {number} productName 商品名称
 * @param {number} productPrice 商品价格
 * @param {number} shopId 店铺id
 * @param {number} footMarkDate 日期
 */
export interface ApiUserFootprint {
  productId: Long
  productPic: string
  productName: string
  productPrice: number
  shopId: Long
  footMarkDate?: string
  platformCategoryId: string
}

/**
 * 评价枚举
 */
export enum EvaluationType {
  //有内容
  CONTENT,
  //有图片
  IMAGE,
  //全部
  '',
}

/**
 * @param {*} contentCount 有内容数量
 * @param {*} evaluate  Evaluate
 * @param {*} mediaCount 图片数量
 * @param {*} praiseCount 好评数量
 * @param {*} praiseRatio 好评率
 * @param {*} totalCount 好评总数
 */
export interface ApiEvaluation {
  contentCount: string
  evaluate: Evaluate
  mediaCount: string
  praiseCount: string
  praiseRatio: string
  totalCount: string
}

/**
 * @param {*} comment 文本描述
 * @param {*} image  商品图片
 * @param {*} isExcellent 精选
 * @param {*} medias 评价图片
 * @param {*} name 商品名称
 * @param {*} productId 产品id
 * @param {*} rate 评价星级
 * @param {*} specs 规格
 * @param {*} shopReply 商家回复
 */
export interface Evaluate {
  comment: string
  createTime: string
  deleted: boolean
  id: string
  image: string
  isExcellent: boolean
  itemId: string
  medias: string[]
  name: string
  orderNo: string
  packageId: string
  productId: Long
  rate: 5
  shopId: Long
  skuId: Long
  specs: string[]
  updateTime: string
  userId: string
  shopReply?: string
  avatar?: string
  nickname?: string
}

export type ApiGoodSku = {
  limitNum: number
  limitType: keyof typeof Limit
  stock: Long
  stockType: keyof typeof Stock
}

export interface OrderParamsType {
  productId: Long
  type: OrderType
  extra: any
  activityId: Long
  stackable: Record<'vip' | 'coupon' | 'full', boolean>
}

export type ComOperationType = 'BUY' | 'JOINCART' | 'SWITCH' | 'NAVIGATE' | 'BARGAIN'
type ComOperationSource = 'COMMON' | 'ACTIVITY'
export type SetOption = {
  type: ComOperationType
  control: boolean
  source: ComOperationSource
  isCar: boolean
  loading: boolean
  immediately: boolean
}

export interface couponType {
  shopId: Long
  productId: Long
  amount: Long
  size?: number
  current?: number
}

/**
 * 商品调度返回类型
 */
export interface comDispatcherType {
  instruction: Ref<boolean>
  forecastPrice: Ref<Long>
  discountPlugin: DiscountHooksFn
  setOperation: SetOption
  shopId: Ref<Long>
  productId: Ref<Long>
  TEAM: ReturnType<HooksOfGroupType>
  SPIKE: ReturnType<HooksOfSecKillType>
  BARGAIN: ReturnType<HooksOfBargainType>
  isExistDiscount: ComputedRef<boolean>
  goodInfo: Ref<ProductResponse>
  skuGroup: Ref<ProductSpecsSkusVO>
  currentChoosedSku: Ref<StorageSku>
  currentGoodsExtraData: Ref<{
    currentParams: string[]
    currentSpecs: ProductFeaturesValueDTO[]
  }>
  isActivityProduct: ComputedRef<boolean>
  updateInstruction: () => void
  activityDisSkuOptions: ComputedRef<string[]>
  swiperList: { list: string[]; currenuIdx: number; swiperList: string[]; mainList: string[] }
  currentActivity: Ref<ActivityDetailVO>
  extraQuery: Ref<Record<string, string>>
  discountSet: Ref<ChainHandleParam[]>
  getParam: (productId: Long) => OrderParamsType | undefined
  addParam: (param: OrderParamsType) => void
  delParam: (productId: Long) => void
  resetParam: () => void
  getOrderPrice: () => Long
  updateIsCar: (e: boolean) => void
  updateSku: (e: StorageSku, isFirst?: boolean, currentSpecs?: Obj[]) => void
  memberPriceC: ComputedRef<string>
  discountMap: Ref<Record<keyof typeof DiscountType, ProductDiscountVO>>
  initGoodsInfo: () => void
  refreshPage: () => Promise<void>
  refreshPageFlag: Ref<boolean>
  getProductCoupons: (flag: boolean, e: couponType) => void
}
