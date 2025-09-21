import { post, del } from '@/apis/http'
import type { Nullable, Long, DoGetApplyDiscountListParams, AddDiscountActiveParams, ViolationParam, PromotionFullReduction } from './index'
import { L } from '@/utils/types'

const baseUrl = 'addon-full-reduction/full/reduction'

/**
 * 分页查询满减活动
 */
export const doGetApplyDiscountList = (params: DoGetApplyDiscountListParams) => {
  return post<L<PromotionFullReduction>>({
    url: `${baseUrl}/page`,
    data: params,
  })
}

/**
 * 编辑满减活动
 */
export const doPostDiscountActive = (data: AddDiscountActiveParams) => {
  return post({
    url: baseUrl,
    data,
  })
}
/**
 * 查询单个满减活动
 */
export const doGetApplyDiscountById = (data: { shopId?: Nullable<Long>; id: Long }): Promise<any> => {
  return post({
    url: `${baseUrl}/detail`,
    data,
  })
}

/**
 * 下架满减活动
 */
export const doPutSellOff = (data: ViolationParam) => {
  return post({
    url: `${baseUrl}/shelf`,
    data,
  })
}

/**
 * 批量删除满减活动
 */
export const doDelApplyDiscountShopBatch = (ids: Long[]) => {
  return del({
    url: `${baseUrl}/batch/delete`,
    data: ids,
  })
}
