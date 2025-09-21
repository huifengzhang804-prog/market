import api from '@/libs/request'
import type { ParamsAssess } from '@/basePackage/pages/releaseAssess/types'
/**
 * 获取收藏列表
 * @param {string} size
 * @param {string} current
 */
export const doGetAssessOrderList = (page: { size: number; current: number }) => {
  return api.get('gruul-mall-user/user/collect/getUserCollectInfo', page)
}
/**
 * 买家评价
 * @param {string} size
 * @param {string} current
 */
export const doPostbuyersEvaluation = (data: ParamsAssess) => {
  return api.post('gruul-mall-order/order/evaluate', data)
}
/**
 * 评价中心评价列表
 * @param {string} size
 * @param {string} current
 */
export const doGetbuyersEvaluation = (params?: any) => {
  return api.get('gruul-mall-order/order/evaluate', params)
}
