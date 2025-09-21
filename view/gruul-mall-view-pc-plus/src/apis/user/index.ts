import { get, post, put, del, patch } from '../http'
/**
 * @description: 猜你喜欢
 * @param {} params
 */
export const doGetGuessYouLike = (params: any): Promise<any> => {
  return get({ url: 'gruul-mall-user/user/GuessYouLike/list', params })
}
/**
 * @description: 看了又看
 * @param {} params
 */
export const doGetlookAndSee = (params: any, shopId: string): Promise<any> => {
  return get({ url: '/gruul-mall-goods/api/product/lookAndSee', params, headers: { 'Shop-Id': shopId } })
}
/**
 * @description: 店铺热销
 * @param {} params
 */
export const doGetshopHotSales = (shopId: string): Promise<any> => {
  return get({ url: '/gruul-mall-goods/api/product/shopHotSales', headers: { 'Shop-Id': shopId } })
}
/**
 * @description: 热门关注
 * @param {} params
 */
export const doGetpopularAttention = (shopId: string): Promise<any> => {
  return get({ url: '/gruul-mall-goods/api/product/popularAttention', headers: { 'Shop-Id': shopId } })
}
// 首页商品推荐
export const doPostProduct = (data: any) => {
  return post({ url: 'gruul-mall-search/search/product', data })
}
/**
 * 商品详情页套餐基本信息
 */
export const doGetSetMealBasicInfo = (shopId: string, productId: string): Promise<any> => {
  return get({ url: `addon-matching-treasure/consumer/setMealBasicInfo/${shopId}/${productId}` })
}
