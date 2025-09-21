import api from '@/libs/request'

/**
 * 获取购物车列表
 */
export const doGetShopCarList = () => {
  return api.get('gruul-mall-cart/cart')
}
/**
 * 删除购物车数据
 */
export const doDeleteShopCarData = (data: { shopId: Long; uniqueIds: string[]; skuIds: Long[] }[]) => {
  return api.put('gruul-mall-cart/cart', data)
}
/**
 * 清空购物车失效商品
 */
export const doEmptyShopCarData = () => {
  return api.delete('gruul-mall-cart/cart/invalid')
}

/**
 * 修改购物车商品
 */
export const doUpdateShopCarGood = (uniqueId: Long, data: any) => {
  return api.put(`gruul-mall-cart/cart/${uniqueId}`, data)
}
