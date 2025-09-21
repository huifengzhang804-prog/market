import api from '@/libs/request'
/**
 * 根据店铺id查询购物车满减活动
 * @param {} data
 */
export const doPostFullReductionShopCart = (data: Long[]) => {
  return api.post(`addon-full-reduction/full/reduction/shop/rule/labels`, data)
}
