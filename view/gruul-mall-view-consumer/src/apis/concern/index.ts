import api from '@/libs/request'
import type { AdvertisementFormType, HomeBulletFrameFormType } from './types'
/**
 * 获取店铺关注状态
 * @param {Long} shopId
 */
export const doGetOrderConcernStatusByShopId = (shopId: Long) => {
  return api.get(`gruul-mall-goods/shop/follow/or/not/${shopId}`)
}
/**
 * 取消关注|关注
 * @param {Long} shopId
 */
export const doCancelAttentionAndAttention = (shopName: string, shopId: Long, isFollow: boolean, shopLogo: string) => {
  return api.put(`gruul-mall-goods/shop/follow`, {
    name: shopName,
    shopId,
    isFollow,
    shopLogo,
  })
}

/**
 * 获取全部关注列表
 * @param {string} status ALL_SHOP 全部店铺 RECENTLY 最近查看 NEW_PRODUCTS 上新商品
 * @param {string} shopName
 * @param {number} current
 * @param {number} size
 */
export const doGetConcernList = (current?: number, size?: number) => {
  return api.get(`gruul-mall-goods/shop/follow/myFollow`, {
    current,
    size,
  })
}
/**
 * 获取我的页面中关注列表
 * @param {string} status ALL_SHOP 全部店铺 RECENTLY 最近查看 NEW_PRODUCTS 上新商品
 * @param {string} shopName
 * @param {number} current
 * @param {number} size
 */
export const doGetConcernListFromMine = (params: any) => {
  const { current, size, shopName, status } = params
  return api.get(`gruul-mall-goods/shop/follow/myFollow/${status}`, {
    current,
    size,
    shopName,
  })
}
/**
 * 获取首页关注下的店铺
 * @param {number} current
 * @param {number} size
 */
export const doGetConcernShopListInfo = (current?: number, size?: number) => {
  return api.get(`gruul-mall-goods/shop/follow/shopInfo`, {
    current,
    size,
  })
}
/**
 * 获取推荐店铺列表
 * @param params 参数
 * @returns
 */
export const doGetConcernRecommandShops = (params: any = {}) => {
  return api.post('gruul-mall-shop/shop/info/searchRecommendationShop', params)
}

/**
 * 根据终端查询广告信息
 * @param endPoint 参数
 * @returns
 */
export const doGetNotification = (endPoint: string) => {
  return api.get<HomeBulletFrameFormType>(`gruul-mall-addon-platform/home/page/win/use/${endPoint}`)
}

/**
 * 根据终端查询开屏广告信息
 * @param endPoint 参数
 * @returns
 */
export const doGetOpenScreenAdvertisement = (endPoint: string) => {
  return api.get<AdvertisementFormType>(`gruul-mall-addon-platform/splash/use/${endPoint}`)
}
