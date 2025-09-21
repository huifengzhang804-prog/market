// 接口
import api from '@/libs/request'
// 请求params 类型
import type { TParamsSetMealBasicInfo, TParamsGetSetMealDetail } from './model/api-params-types'
import type { TResponseGetSetMealDetail } from './model/api-response-type'

const BASE_URL = 'addon-matching-treasure/consumer/'

/**
 * 商品详情页套餐基本信息
 * @param {number} shopId
 * @param {number} productId
 */
export const doGetSetMealBasicInfo = ({ shopId, productId }: TParamsSetMealBasicInfo) => {
  return api.get(BASE_URL + `setMealBasicInfo/${shopId}/${productId}`)
}
/**
 * 套餐详情页套餐信息
 * @param {TParamsGetSetMealDetail} param1
 */
export const doGetSetMealDetail = ({ shopId, setMealId }: TParamsGetSetMealDetail) => {
  return api.get<TResponseGetSetMealDetail>(BASE_URL + `setMealDetail/${shopId}/${setMealId}`)
}
