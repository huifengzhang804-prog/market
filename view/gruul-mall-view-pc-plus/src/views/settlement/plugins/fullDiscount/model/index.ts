import type { Ref } from 'vue'
// import type { GoodListType, GoodItemSpec } from '@/pages/modules/car/types'
import { DiscountPublicAttr } from '../../types'
import { GoodItemSpec, GoodListType } from '@/views/shoppingcart/types'
// import type { DiscountPublicAttr } from '@/pages/plugin/types'

export interface DoGetFullReductionConfirmOrderParams extends Pick<GoodListType, 'shopId'> {
  productAmounts: ProductAmounts[]
}
interface ProductAmounts extends Pick<GoodItemSpec, 'productId'> {
  amount: string
}
// 购物车 / 结算页 满减响应参数
export interface CartFullReductionResponseParameters extends Pick<GoodListType, 'shopId'> {
  productType: ProductType
  productIds: string[]
  fullReductionId: string
  fullReductionRules: FullReductionRules[]
}
export interface FullReductionRules {
  fullReductionRule: FullReductionRule
  conditionAmount: number //  满减条件金额
  discountAmount?: number // 优惠金额
  discountRatio?: number // 折扣比例
  orderDiscount?: number // 订单优惠金额
  id: string
}
export type ProductType = keyof typeof PRODUCT_TYPE
enum PRODUCT_TYPE {
  ALL_PRODUCT = '全部商品',
  SPECIFIED_PRODUCT_PARTICIPATE = '指定商品',
  SPECIFIED_PRODUCT_NOT_PARTICIPATE = '指定商品不参与',
}
export type FullReductionRule = keyof typeof FULL_REDUCTION_RULE
enum FULL_REDUCTION_RULE {
  FULL_REDUCTION = '满减',
  FULL_DISCOUNT = '满折',
}
// 结算页满减 Map
export type FullReductionConfirmOrderMap = Map<string, { rules: CartFullReductionResponseParameters; discountAmount: string; discountId: string }>
export interface FullDispatcherType extends DiscountPublicAttr {
  doRequest: () => void
  fullReduction: Ref<FullReductionRules[]>
}
