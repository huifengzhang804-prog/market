import type { Fns, MaybeReactive } from '@/utils/types'
import type { ComputedRef } from 'vue'

export interface ProductInfo {
  shopId: Long
  productId: Long
  productPrice: Long
  quantity: number
}
type PreferTreatmentRecordKey = 'COUPON' | 'FULL' | 'addon-member'
export interface ChainHandleParam {
  shopId: Long
  totalPrice?: string
  discountPrice: string
  // 优惠项
  preferTreatment: Partial<Record<PreferTreatmentRecordKey, { discount: string }>>
}
export interface ChainHandler {
  setNext(handler: ChainHandler): ChainHandler
  handle(params: ChainHandleParam): ChainHandleParam
}
/**
 * 叠加优惠项目公共方法和属性
 * @param isExist 优惠项是否存在（是否存在插件 + 接口是否有返回值 ）
 */
export interface DiscountPublicAttr {
  isExist: MaybeReactive<boolean>
}

/**
 * 活动Dispacher父接口
 */
export interface ActivityDispatcherType {
  doRequest: Fns
  getSkuIds?: ComputedRef<Long[]>
  getPrice: () => Long | null | void
}
