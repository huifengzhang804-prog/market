import type { ComputedRef, Ref } from 'vue'
export type AppPluginName =
  | 'addon-matching-treasure'
  | 'gruul-mall-addon-intraCityDistribution'
  | 'addon-full-reduction'
  | 'addon-team'
  | 'addon-shop-store'
  | 'addon-distribute'
  | 'gruul-mall-addon-platform'
  | 'addon-coupon'
  | 'addon-seckill'
  | 'addon-integral'
  | 'addon-member'
  | 'ship-cost'
  | 'addon-platform-coupon'
type MaybeReactive<T> = ComputedRef<T> | Ref<T> | T
export interface ProductInfo {
  shopId: string
  productId: string
  productPrice: string
  quantity: number
}
export interface ChainHandleParam {
  shopId: string
  totalPrice?: string
  discountPrice: string
  // 优惠项
  preferTreatment: Partial<Record<AppPluginName, { discount: string }>>
}
export interface ChainHandler {
  setNext(handler: ChainHandler): ChainHandler
  handle(params: ChainHandleParam): ChainHandleParam
}
/**
 * @description: 叠加优惠项目公共方法和属性
 * @param isExist 优惠项是否存在（是否存在插件 + 接口是否有返回值 ）
 */
export interface DiscountPublicAttr {
  isExist: MaybeReactive<boolean>
}
/**
 * @description: 活动公共方法和属性
 * @param getPrice 获取当前活动商品的价格（活动会影响商品成交价）
 */
export interface ActivityPublicAttr {
  getPrice: () => string | null
}
/**
 * @description: 活动Dispacher父接口
 * @param  isShowGoodsPrice 展示商品详情中的原价
 */
export interface ActivityDispatcherType {
  doRequest: () => void
  getSkuIds: ComputedRef<string[]>
  getPrice: () => string | null
  isShowGoodsPrice: ComputedRef<boolean>
}
