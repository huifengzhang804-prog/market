import api from '@/libs/request'
import type { GetShopInfoRes, ShopType } from './model'
import type { Geometry } from '../address/type'

/**
 * 获取购物车列表
 */
export const doGetShopCarList = (shopId: Long) => {
  return api.get(`gruul-mall-cart/cart?shopId=${shopId}`)
}

/**
 * 获取购物车列表价格
 */
export const doGetShopCarPrice = (shopId: Long) => {
  return api.get(`gruul-mall-cart/cart/money?shopId=${shopId}`)
}

/**
 * 获取指定店铺的详情(距离、销量 兼容非020店铺 包含是否已关注，在售商品数 销量  距离 等等)
 */
export const doGetShopInfo = (params: { shopId: Long; location?: Geometry; type: keyof typeof ShopType }) => {
  return api.post<GetShopInfoRes>('gruul-mall-shop/shop/info/detail', params)
}

/**
 * 清空购物车列表
 */
export const doClearCart = (shopId: Long) => {
  return api.delete(`gruul-mall-cart/cart?shopId=${shopId}`)
}

/**
 * 删除购物车数据
 */
export const doDeleteCart = (data: { shopId: Long; uniqueIds: string[]; skuIds: Long[] }[]) => {
  return api.put(`gruul-mall-cart/cart?shopId=${data[0].shopId}`, data)
}

/**
 * 根据销量获取商家列表
 */
export const doGetShopListBySalesVolume = (params: { sortAsc: boolean }) => {
  return api.get('gruul-mall-shop/shop/info/search/shop/sales', params)
}

/**
 * 根据距离获取商家列表
 */
export const doGetShopListByDistance = (params: {
  sortAsc: boolean
  longitude?: number
  latitude?: number
  showHeaderShopIds?: any[]
  moreCount?: number
}) => {
  return api.post('gruul-mall-shop/shop/info/search/shop/distance', params)
}
/**
 * 获取商家销量钱6的商品列表
 * @param { Long[] } shopIds 商家id数组
 */
export const doGetCommodityByShop = (shopIds: Long[]) => {
  return api.post('gruul-mall-search/search/product/top6', shopIds)
}
