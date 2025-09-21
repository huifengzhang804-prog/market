import api from '@/libs/request'
import type {
  ProductParam,
  ProductResponse,
  ProductSpecsSkusVO,
  ActivityDetailVO,
  TParamsGetGproductDelivery,
  TdoGetQrcode,
  TdoGetshops,
  Shops,
  RetrieveCommodityResult,
} from './model'
import type { EvaluationType } from '@/pluginPackage/goods/commodityInfo/types'
import type { Geometry } from '../address/type'
import { type GetShopInfoRes, ShopType } from '../o2oshop/model'
import type { Pagination, Result } from '@/utils/types'

/**
 * @deprecated
 * 获取商品详情
 * @param {Long} goodId
 */

export const doGetGoodDetail = (goodId: Long, shopId: Long): Promise<any> => {
  return api.get<ProductResponse>(`gruul-mall-goods/api/product/get/${goodId}`, {}, { 'Shop-Id': shopId })
}

/**
 * 加入购物车
 */
export const doAddTocar = (data: any) => {
  return api.post('gruul-mall-cart/cart', data)
}
/**
 * 查询商品sku
 */
export const doGetGoodSku = (productId: Long, shopId: Long) => {
  return api.get<ProductSpecsSkusVO>(`gruul-mall-storage/storage/shop/${shopId}/product/${productId}`)
}
/**
 * 查询收藏
 * @param {Long} shopId
 * @param {Long} productId
 */
export const doGetAssessOrder = (shopId: Long, productId: Long) => {
  return api.get<ActivityDetailVO | null>('gruul-mall-user/user/collect/findUserIsCollect', { shopId, productId })
}

/**
 * 商品评价概述
 * @param {Long} productId
 */
export const doGetOrderEvaluateInfo = (shopId: Long, productId: Long, type = '') => {
  return api.get(`gruul-mall-order/order/evaluate/shop/${shopId}/product/${productId}/overview`, { type })
}

/**
 * 商品所有评价列表
 * @param {Long} productId
 */
export const doGetOrderEvaluateInfoList = (shopId: Long, productId: Long, current = 1, type?: keyof typeof EvaluationType) => {
  if (!type) {
    type = ''
  }
  return api.get(`gruul-mall-order/order/evaluate/shop/${shopId}/product/${productId}`, { type, current })
}
/**
 * 查询运费相关信息
 */
export const doGetGproductDelivery = (data: TParamsGetGproductDelivery[]) => {
  return api.post('gruul-mall-goods/api/product/productDelivery', data)
}
// salePrice_desc 价格降序
// salePrice_asc 价格升序
// salesVolume 销量排序
// createTime_desc/asc 新品
enum RETRIEVESORT {
  salePrices,
  salesVolume,
  createTime,
  comprehensive,
}

enum SORT {
  ASC,
  DESC,
}

export interface RetrieveParam {
  keyword: string
  categoryFirstId: string | null
  categorySecondId: string
  categoryThirdId: string
  platformCategoryFirstId: string | null
  platformCategorySecondId: string
  platformCategoryThirdId: string
  size: number
  orderByParams: RetrieveOrderByParams
  minPrice: number
  maxPrice: number
  current: number
  shopId: Long
  productId: Long[]
  showCoupon?: boolean
  showHistory?: boolean
  searchTotalStockGtZero?: boolean
  ids: string[]
  excludeProductId?: Long
  showSku?: boolean
  platformCategoryThirdName?: string
}

export interface RetrieveItemType {
  categoryId: string
  categoryName: string
  productId: Long
  id: string
  name: string
  productName: string
  pic: string
  salePrices: string[]
  salesVolume: string
  shopName: string
  shopId: Long
}

type RetrieveOrderByParamsItem = { order: keyof typeof RETRIEVESORT; sort: keyof typeof SORT }
export type RetrieveOrderByParams = Array<RetrieveOrderByParamsItem | Array<RetrieveOrderByParamsItem>>

/**
 * 删除用户检索记录
 */
export const doDeleteUserHistory = () => {
  return api.delete('gruul-mall-search/search/product/history')
}

/**
 * 搜索建议
 */
export const doGetSearchSuggest = (params: any) => {
  return api.get(`gruul-mall-search/search/product/suggest`, params)
}

/**
 * 查询搜索历史与热词
 */
export const doGetHistoriesAndHotWords = () => {
  return api.get('gruul-mall-search/search/product/hhw')
}

/**
 * 商品检索
 */
export const doGetRetrieveCommodity = (params: Partial<RetrieveParam>) => {
  delete params.platformCategoryThirdName
  return api.post<RetrieveCommodityResult>('gruul-mall-search/search/product', {
    ...params,
    searchTotalStockGtZero: true,
  })
}

/**
 * 获取指定店铺的详情(距离、销量 兼容非020店铺 包含是否已关注，在售商品数 销量  距离 等等)
 */
export const doGetShopInfo = (params: { shopId: Long; location?: Geometry; type: keyof typeof ShopType }) => {
  return api.post<GetShopInfoRes>('gruul-mall-shop/shop/info/detail', params)
}

/**
 * 批量查询店铺信详情
 * @param {Long} shopIds
 */
export const doPostShopInfo = (shopIds: Long[]) => {
  return api.post(`gruul-mall-shop/shop/info/batch`, shopIds)
}

// 获取二维码
export const doGetQrcode = (data: TdoGetQrcode) => {
  return api.post(`gruul-mall-uaa/uaa/scan/qrcode/redirect`, data)
}

// 获取店铺列表
export const doGetshops = (params: TdoGetshops) => {
  return api.post<Pagination<Shops[]>>('gruul-mall-shop/shop/info/search/shop', params)
}

/**
 * 商品详情 聚合接口
 */
export const doGetProduct = (data: ProductParam) => {
  return api.post<ProductResponse, ProductParam>('gruul-mall-goods/api/product/details', data, {
    cancelLast: true,
  })
}
