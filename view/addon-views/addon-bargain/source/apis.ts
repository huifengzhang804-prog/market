import { get, post, put, del, patch } from '@/apis/http'
import { DoPostAddBargainQuery, DoGetAddBargainQuery } from '.'
import { BargainInfoData, BargainData } from './types'

const baseUrl = 'addon-bargain/bargain'
const BARGAIN_ORDER = 'addon-bargain/bargainOrder'
/**
 * 添加砍价活动
 */
export const doPostAddBargain = (data: DoPostAddBargainQuery) => {
  return post({
    url: baseUrl,
    data,
  })
}
export interface doPutBargainsellOfParamsType {
  bargainId: string
  shopId: string
}
/**
 * 商家下架砍价活动
 */
export const doPutBargainsellOf = (data: doPutBargainsellOfParamsType) => {
  return put({
    url: baseUrl + '/shop/sellOf',
    data,
  })
}
/**
 * 查看违规原因
 */
export const doPutBargainIllegalReason = (id: string) => {
  return get({
    url: baseUrl + '/illegal/reason/' + id,
  })
}
/**
 * 分页查询砍价活动
 */
export const doGetAddBargain = (params: any): Promise<any> => {
  return get<BargainData>({
    url: baseUrl,
    params,
  })
}

/**
 * 获取违规原因
 */
export const doGetIllegalReason = (bargainId: string): Promise<any> => {
  return get({
    url: `${baseUrl}/illegal/reason/${bargainId}`,
  })
}

/**
 * 批量删除砍价活动
 */
export const doDelAddBargain = (data: { shopId: string; activityId: string }[]) => {
  return del({
    url: baseUrl,
    data,
  })
}
/**
 * 分页查询砍价订单
 */
export const doGetBargainInfo = (params: { shopId: string; activityId: string }): Promise<any> => {
  return get<BargainInfoData>({
    url: baseUrl + `/${params.shopId}/${params.activityId}`,
  })
}
/**
 * 查询砍价活动详情
 */
export const doGetBargainOrder = (params: any) => {
  return get({
    url: BARGAIN_ORDER,
    params,
  })
}
/**
 * 砍价订单详情
 */
export const doGetBargainOrderDetail = (id: string) => {
  return get({
    url: BARGAIN_ORDER + `/detail/${id}`,
  })
}
/**
 * 分页查询砍价帮砍列表
 */
export const doGetBargainHelpPeople = (params: any) => {
  return get({
    url: `addon-bargain/bargainHelpPeople`,
    params,
  })
}

/**
 * 平台违规下架砍价活动
 */
export const doPutSellOfBargain = (data: any) => {
  return put({
    url: `${baseUrl}/platform/sellOf`,
    data,
  })
}

/**
 * @param BARGAINING: 砍价中
 * @param FAILED_TO_BARGAIN: 砍价失败
 * @param SUCCESSFUL_BARGAIN: 砍价成功
 */
export const bargainStatusCn = {
  BARGAINING: '砍价中',
  FAILED_TO_BARGAIN: '砍价失败',
  SUCCESSFUL_BARGAIN: '砍价成功',
}
