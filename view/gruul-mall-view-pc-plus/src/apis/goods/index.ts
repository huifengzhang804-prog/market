import { FootMarkListType } from '@/views/personalcenter/assets/balance/types'
import { get, post, put, del, patch } from '../http'
import { L } from '../http.type'
/**
 * @description: 分页查询商品分类
 * @param {} params
 */
export const doGetCategory = (params?: any) => {
  return get({ url: 'gruul-mall-goods/goods/product/category', params })
}
/**
 * @description: 获取商品详情
 * @param {} id
 */
export const doGetGoodDetail = (goodId: string, shopId: string) => {
  return get({ url: `gruul-mall-goods/api/product/get/${goodId}`, headers: { 'Shop-Id': shopId } })
}

/**
 * @description: 查询商品sku
 */
export const doGetGoodSku = (productId: string, shopId: string) => {
  return get({ url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}` })
}
/**
 * @description: 销量信息
 * @param {string} productId
 * @returns {*}
 */
export const doGetShopProductStatistics = (shopId: string, productId: string) => {
  return get({ url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}/statistics` })
}
/**
 * @description: 浏览商品记录
 * @param {string} productId 当前商品id
 * @param {string} shopId 当前店铺id
 * @param {string} shopName 当前店铺名
 * @returns {*}
 */
export const doPostBrowseCommodity = (productId: string, shopId: string, shopName: string) => {
  return post({ url: 'gruul-mall-goods/product/browse', data: { productId, shopId, shopName } })
}
/**
 * @description: 加入购物车
 */
export const doAddTocar = (data: any) => {
  return post({ url: 'gruul-mall-cart/cart', data })
}
/**
 * @description: 商品评价概述
 * @param {string} productId
 * @returns {*}
 */
export const doGetOrderEvaluateInfo = (shopId: string, productId: string, type = ''): Promise<any> => {
  return get({ url: `gruul-mall-order/order/evaluate/shop/${shopId}/product/${productId}/overview`, params: { type } })
}
/**
 * @description: 商品所有评价列表
 * @param {string} productId
 * @returns {*}
 */
export const doGetOrderEvaluateInfoList = (shopId: string, productId: string, current = 1, type?: string): Promise<any> => {
  return get({ url: `gruul-mall-order/order/evaluate/shop/${shopId}/product/${productId}`, params: { type, current } })
}
/**
 * @description: 查询店铺基础信息
 * @param {string} shopId
 * @returns {*}
 */
export const doGetShopBaseInfo = (shopId: string): Promise<any> => {
  return get({ url: `gruul-mall-shop/shop/info/base/${shopId}` })
}
enum RETRIEVESORT {
  salePrice_desc,
  salePrice_asc,
  salesVolume_desc,
  salesVolume_asc,
  createTime_desc,
  createTime_asc,
  comprehensive_desc,
  comprehensive_asc,
}
export interface RetrieveParam {
  keyword: string
  categoryFirstId: string
  categorySecondId: string
  categoryThirdId: string
  platformCategoryFirstId: string
  platformCategorySecondId: string
  platformCategoryThirdId: string
  pageNum: number
  size: number
  // sort: keyof typeof RETRIEVESORT | string
  minPrice: number
  maxPrice: number
  current: number
  pages: number
  shopId: string
  // comprehensive: string
  orderByParams: Array<{
    order: string
    sort: string
  }> | null
  showCoupon?: boolean
}
/**
 * @description: 检索商品
 */
export const doGetRetrieveCom = (params: Partial<RetrieveParam>, shopId?: string): Promise<any> => {
  if (!shopId) {
    shopId = '0'
  }
  return post({ url: 'gruul-mall-search/search/product', data: { ...params, searchTotalStockGtZero: true }, headers: { 'Shop-Id': shopId } })
}
/**
 * @description: 获取足迹
 */
export const doGetComByFoot = (params: any) => {
  return get<L<FootMarkListType>>({ url: 'gruul-mall-user/user/FootMark/list', params })
}
/**
 * @description: 删除清空足迹
 */
export const doDelComFoot = (type: 'EMPTY' | 'DELETE', ids: string[]) => {
  return del({
    url: `gruul-mall-user/user/FootMark?userFootMarkStatus=${type}`,
    data: ids,
  })
}
/**
 * @description: 根据品牌id查询品牌和首字母
 */
export const doPostBrandName = (data: any) => {
  return post({ url: 'gruul-mall-search/search/product/brandInitialList', data })
}
/**
 * @description: 查询品牌详情
 */
export const doGetBrandDetail = (brandId: string) => {
  return get({ url: `gruul-mall-search/search/product/brandDetail/${brandId}` })
}
/**
 * @description: 根据类目id/三级分类id查询品牌
 */
export const doGetBrandlist = (parentCategoryId: string) => {
  return get({ url: `gruul-mall-search/search/product/brandInitial/${parentCategoryId}` })
}
/**
 * @description: 关注/取消品牌
 */
export const postBrandFollow = (data: any) => {
  return post({ url: 'gruul-mall-search/search/BrandFollow/follow', data })
}

// 获取店铺列表
export const doGetshops = (data: any): Promise<any> => {
  return post({ url: 'gruul-mall-shop/shop/info/search/shop', data })
}

/**
 * @description 获取商品详情（聚合接口）
 * @param goodId 商品ID
 * @param shopId 商家ID
 * @param skuId 规格ID
 * @returns
 */
export const doGetGoodDetailsWithSku = (goodId: string, shopId: string, skuId = '') => {
  return get({ url: `gruul-mall-goods/api/product/details/${goodId}`, params: { shopId, skuId }, headers: { 'Shop-Id': shopId } })
}
/**
 * 商品详情 聚合接口
 */
export const doGetProduct = (data: any): Promise<any> => {
  return post({ url: 'gruul-mall-goods/api/product/details', data })
}
