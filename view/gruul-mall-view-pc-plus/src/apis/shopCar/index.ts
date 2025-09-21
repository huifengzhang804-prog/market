import { get, post, put, del, patch } from '../http'
/**
 * @description: 获取购物车列表
 */
export const doGetShopCarList = (): Promise<any> => {
  return get({ url: 'gruul-mall-cart/cart' })
}
/**
 * @description: 删除购物车数据
 */
export const doDeleteShopCarData = (data: { shopId: string; uniqueIds: string[]; skuIds: string[] }[]) => {
  return put({ url: 'gruul-mall-cart/cart', data })
}
/**
 * @description: 清空购物车失效商品
 */
export const doEmptyShopCarData = () => {
  return del({ url: 'gruul-mall-cart/cart/invalid' })
}

/**
 * @description: 修改购物车商品
 */

export const doUpdateShopCarGood = (skuId: string, data: any) => {
  return put({ url: `gruul-mall-cart/cart/${skuId}`, data })
}
/**
 * 检验订单异常
 */
export const doPostOrderValid = (data: any) => {
  return post({ url: `gruul-mall-order/order/valid`, data })
}
/**
 * @description: 获取商品sku
 */
// export const doGetGoodSku = (shopId: string, productId: string) => {
//     return get(`gruul-mall-goods/manager/product/${productId}/spec/specs-skus`, {}, { 'Shop-Id': shopId })
// }
