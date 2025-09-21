import api from '@/libs/request'
import type { ApiUserFootprint } from '@/pluginPackage/goods/commodityInfo/types'
/**
 * 根据月份查询有足迹的日期
 * @param {number} month 月份
 */
export const doGetGoodFootprint = (month: number) => {
  return api.get(`gruul-mall-user/user/FootMark/getFootMarkBrowsDates`, { month })
}
/**
 * 获取用户足迹
 * @param {number} userFootprint ApiUserFootprint footMarkDate日期
 */
export const doGetUserFootprintList = (data: any) => {
  return api.get(`gruul-mall-user/user/FootMark/list`, data)
}

/**
 * 删除用户足迹
 * @param {number}
 * @param {number} userFootMarkStatus EMPTY 清空 DELETE 删除
 */
export const doDelUserFootprint = (ids: any, userFootMarkStatus: 'EMPTY' | 'DELETE' = 'DELETE') => {
  return api.delete(`gruul-mall-user/user/FootMark?userFootMarkStatus=${userFootMarkStatus}`, ids)
}
/**
 * 添加收藏
 * @param {number} assessData
 */
export const doAddAssess = (assessData: any) => {
  return api.post(`gruul-mall-user/user/collect`, assessData)
}
/**
 * 猜你喜欢
 * @param {*} current
 * @param {*} size
 */
export const doGetGuessYouLike = (current: number, size: number) => {
  return api.get(`gruul-mall-user/user/GuessYouLike/list`, { current, size })
}
/**
 * 首页获取余额
 */
export const doGetUserBalance = () => {
  return api.get(`gruul-mall-user/user/balance`)
}
/**
 * 修改个人资料
 */
export const doPostUserData = (updateUserName: string, commitUserGender: string, avatarimg: string, birthday: string) => {
  return api.post(`gruul-mall-uaa/uaa/user/data`, { nickname: updateUserName, gender: commitUserGender, avatar: avatarimg, birthday })
}
/**
 * 订单统计
 */
export const doGetMyCount = () => {
  return api.get(`gruul-mall-order/order/my/count`)
}
export const doGetPrivateAgreement = () => {
  return api.get('gruul-mall-addon-platform/platform/privacyAgreement')
}
/**
 * 我的获取余额、积分、收藏数量、足迹数量
 */
export const doGetuserPerson = () => {
  return api.get('gruul-mall-user/user/person')
}
/**
 * 查询用户身份（区分权限）
 */
export const doGetRoleMenu = () => {
  return api.get(`gruul-mall-uaa/uaa/role/menu/role`)
}
