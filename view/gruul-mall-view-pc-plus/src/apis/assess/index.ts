import { ApiAssessItem } from '@/views/personalcenter/follow/goodscollection/types'
import { get, post, put, del, patch } from '../http'
import { L } from '../http.type'
import { ApiEvaluate } from '@/views/personalcenter/order/comment/types'
interface ParamsAssess {
  packageId: string
  orderEvaluateItems: { itemId: string; comment: string; medias: string[]; rate: number }[]
}
/**
 * @description: 获取收藏列表
 * @param {string} size
 * @param {string} current
 * @returns {*}
 */
export const doGetAssessOrderList = (page: { size: number; current: number; productName: string }) => {
  return get<L<ApiAssessItem>>({ url: 'gruul-mall-user/user/collect/getUserCollectInfo', params: page })
}
/**
 * @description: 买家评价
 * @param {string} size
 * @param {string} current
 * @returns {*}
 */
export const doPostbuyersEvaluation = (data: ParamsAssess) => {
  return post({ url: 'gruul-mall-order/order/evaluate', data })
}
/**
 * @description: 评价中心评价列表
 * @param {string} size
 * @param {string} current
 * @returns {*}
 */
export const doGetbuyersEvaluation = (params?: any) => {
  return get<L<ApiEvaluate>>({ url: 'gruul-mall-order/order/evaluate', params })
}
