import { get, post, put, del, patch } from '../http'
const BASE_URL = 'addon-matching-treasure/consumer/'
/**
 * @description: 商品详情页套餐基本信息
 * @param {number} shopId
 * @param {number} productId
 * @returns {*}
 */
export const doGetSetMealBasicInfo = (shopId: string, productId: string) => {
  return get({ url: BASE_URL + `setMealBasicInfo/${shopId}/${productId}` })
}
/**
 * @description: 套餐详情页套餐信息
 * @param {TParamsGetSetMealDetail} param1
 * @returns {*}
 */
export const doGetSetMealDetail = (shopId: string, setMealId: string): Promise<any> => {
  return get({ url: BASE_URL + `setMealDetail/${shopId}/${setMealId}` })
}
/**
 * @description: 查询商品sku
 */
export const doGetGoodSku = (shopId: string, productId: string) => {
  return get({ url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}` })
}
