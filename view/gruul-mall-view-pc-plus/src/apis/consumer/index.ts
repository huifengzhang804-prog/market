import { ConcernItem } from '@/views/personalcenter/follow/shopcollection/index.vue'
import { get, post, put, del, patch } from '../http'
import { ApiUserFootprint } from '@/views/detail/types'
import { L } from '../http.type'

/**
 * @description: 添加用户足迹
 * @param {number} userFootprint ApiUserFootprint
 * @returns {*}
 */
export const doPostUserFootprint = (userFootprint: ApiUserFootprint) => {
  return post({ url: `gruul-mall-user/user/FootMark`, data: userFootprint })
}
/**
 * @description: 获取余额
 * @returns {*}
 */
export const doGetUserBalance = () => {
  return get<string>({ url: `gruul-mall-user/user/balance` })
}
/**
 * @description: 添加收藏
 * @param {number} assessData
 * @returns {*}
 */
export const doAddAssess = (assessData: any) => {
  return post({ url: `gruul-mall-user/user/collect`, data: assessData })
}
/**
 * @description: 查询收藏
 * @param {string} shopId
 * @param {string} productId
 * @returns {*}
 */
export const doGetAssessOrder = (shopId: string, productId: string): Promise<any> => {
  return get({ url: 'gruul-mall-user/user/collect/findUserIsCollect', params: { shopId, productId } })
}
/**
 * @description: 取消收藏
 * @param {string} shopId
 * @param {string} productId
 * @returns {*}
 */
export const doCancelAssessOrder = (shopId: string, productId: string) => {
  return get({ url: 'gruul-mall-user/user/collect/cancel', params: { shopId, productId } })
}
/**
 * @description: 获取关注店铺
 * @returns {*}
 */
export const doGetConcernListFromMine = (params: any) => {
  return get<L<ConcernItem>>({ url: `gruul-mall-goods/shop/follow/myFollow/ALL_SHOP`, params })
}
/**
 * @description: 修改个人资料
 * @returns {*}
 */
export const doPostUserData = (data: any) => {
  return post({ url: `gruul-mall-uaa/uaa/user/data`, data })
}
/**
 * @description: 获取店铺关注状态
 * @param {string} shopId
 * @returns {*}
 */
export const doGetOrderConcernStatusByShopId = (shopId: string): Promise<any> => {
  return get({ url: `gruul-mall-goods/shop/follow/or/not/${shopId}` })
}
/**
 * @description: 取消关注|关注
 * @param {string} shopId
 * @returns {*}
 */
export const doCancelAttentionAndAttention = (shopName: string, shopId: string, isFollow: boolean, shopLogo: string) => {
  return put({
    url: `gruul-mall-goods/shop/follow`,
    data: {
      name: shopName,
      shopId,
      isFollow,
      shopLogo,
    },
  })
}
