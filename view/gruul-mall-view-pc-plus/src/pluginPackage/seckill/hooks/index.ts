import type { ApplyTypesJoint } from '@pluginPackage/scondsKill/components/types/goods-details-secods-kill'
/**
 * @description: 判断是不是秒杀商品
 */
export const useIsSeckillGoodsFn = (seckillId?: string, seckillPrice?: string) => {
  return !!(seckillId && seckillPrice)
}
/**
 * @description: 根据传入类型决定秒杀商品是否可用传入的优惠
 * @param {ApplyTypesJoint} type
 * @param {ApplyTypesJoint} typeArr
 * @returns {*}
 */
export const useWhetherAvailableByApplyType = (type: ApplyTypesJoint, typeArr?: ApplyTypesJoint[]) => {
  if (Array.isArray(typeArr) && type) {
    return typeArr.includes(type)
  }

  // 如果类型数组没传 或者 没传 type 默认可以使用活动优惠
  return true
}
