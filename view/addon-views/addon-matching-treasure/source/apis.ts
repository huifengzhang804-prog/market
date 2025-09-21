import { get, post, put, del, patch } from '@/apis/http'
import { DoPostSetMealQuery } from './index'
const baseUrl = 'addon-matching-treasure/setMeal'

/**
 * 违规下架套餐活动
 */
export const doPostSetMealDel = (data: { shopId: string; setMealId: string; violationExplain: string }) => {
  return post({
    url: `${baseUrl}/${data.shopId}/${data.setMealId}?violationExplain=${data.violationExplain}`,
  })
}
/**
 * 下架套餐活动
 */
export const doSetMealDel = (data: { shopId: string; setMealId: string }) => {
  return post({
    url: `${baseUrl}/${data.shopId}/${data.setMealId}`,
  })
}

/**
 * 新增套餐
 */
export const doPostSetMeal = (data: DoPostSetMealQuery) => {
  return post({
    url: baseUrl,
    data,
  })
}
/**
 * 分页查询套餐活动
 */
export const doGetSetMeal = (params: any) => {
  return get({
    url: baseUrl,
    params,
  })
}
/**
 * 批量删除套餐
 */
export const doDelSetMeal = (data: any) => {
  return del({
    url: baseUrl,
    data,
  })
}
/**
 * 查询套餐活动详情
 */
export const doGetSetMealDetail = (shopId: string, setMealId: string) => {
  return get({
    url: `${baseUrl}/${shopId}/${setMealId}`,
  })
}
const BASE_URL = 'addon-matching-treasure/consumer/'
/**
 * 商品详情页套餐基本信息
 */
export const doGetSetMealBasicInfo = (shopId: string, productId: string): Promise<any> => {
  return get({ url: BASE_URL + `setMealBasicInfo/${shopId}/${productId}` })
}
/**
 * 套餐详情页套餐信息
 */
export const doGetSetMealDetailPc = (shopId: string, setMealId: string): Promise<any> => {
  return get({ url: BASE_URL + `setMealDetail/${shopId}/${setMealId}` })
}
/**
 * 查询商品sku
 */
export const doGetGoodSku = (shopId: string, productId: string) => {
  return get({ url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}` })
}
/**
 * @LastEditors: latiao
 *  获取商品详情
 * @param {} id
 */
export const doGetGoodDetail = (goodId: string, shopId: string): Promise<any> => {
  return get({ url: `gruul-mall-goods/api/product/get/${goodId}`, headers: { 'Shop-Id': shopId } })
}
/**
 * 查询 店铺 名称
 * @param {Long} shopId
 */
export const doGetShopInfo = (shopId: string | null) => {
  return get({ url: `gruul-mall-shop/shop/info/base/${shopId}` })
}
/**
 * 查询运费相关信息
 */
export const doGetGproductDelivery = (data: any[]) => {
  return post({ url: 'gruul-mall-goods/api/product/productDelivery', data })
}
/**
 * 生成价格预算
 * @param {any} data
 */
export const doPostBudget = (data: any) => {
  return post({ url: `gruul-mall-order/order/budget`, data })
}
/**
 * 下单页查询默认地址
 */
export const doGetDefaultAddress = (): Promise<any> => {
  return get({ url: 'gruul-mall-user/user/address/default' })
}
