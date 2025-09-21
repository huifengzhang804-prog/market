enum APPLY_TYPES {
  'APPLY_USER_PRICE' = '会员价',
  'APPLY_COUPON' = '优惠劵',
  'APPLY_FULL_REDUCED' = '满减',
}
export type ApplyTypesJoint = keyof typeof APPLY_TYPES
/**
 * @description: 商品详情页秒杀活动商品信息
 * @param applyTypes 适用优惠
 * @param start
 * @param end
 * @param productId
 * @param productStock 总库存
 * @param secKillCode 随机码
 * @param secKillId
 * @param secKillProductSkuVOList
 */
export interface ApiSeckILLGoodsDetails {
  applyTypes: ApplyTypesJoint[]
  start: string
  end: string
  productId: string
  productStock: string
  secKillCode?: string
  secKillId: string
  secKillProductSkuVOList: SecKillProductSkuVO[]
}
interface SecKillProductSkuVO {
  secKillPrice: string
  skuId: string
  secKillStock: string
}
export interface SetMealNum {
  endTime: string
  saveAtLeast: string
  setMealId: string
  setMealMainPicture: string
  setMealName: string
}
