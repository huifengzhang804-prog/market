import api from '@/libs/request'
import type { Pagination, Result } from '@/utils/types'
import type {
  BargainHelpPeopleListQuery,
  BargainSponsortQuery,
  BargainHelpBargainQuery,
  BargainSponsorProductSkuQuery,
  BargainHelpPeopleListRes,
  BargainSponsorRes,
  BargainSponsorProductSkuRes,
  BargainHelpPeopleItemRes,
} from '@/pluginPackage/bargain/apis/model'

const BASE_URL = 'addon-bargain/consumer/bargain/'

/**
 * 获取砍价好友列表
 * @param {BargainHelpPeopleListQuery} params
 */
export const doGetBargainHelpPeopleList = (params: BargainHelpPeopleListQuery) => {
  return api.get<Pagination<BargainHelpPeopleItemRes[]>>(`${BASE_URL}getBargainHelpPeopleList`, params)
}
/**
 * 发起砍价
 * @param {BargainSponsortQuery} data
 */
export const doPostBargainSponsor = (data: BargainSponsortQuery) => {
  return api.post<BargainSponsorRes>(`${BASE_URL}sponsor`, data)
}
/**
 * 帮好友砍价
 * @param {BargainSponsortQuery} data
 */
export const doPostBargainHelpBargain = (data: BargainHelpBargainQuery) => {
  return api.post<string>(`${BASE_URL}helpBargain`, data)
}
/**
 * 获取已发起砍价商品sku信息
 * @param {BargainSponsortQuery} params
 */
export const doGetSponsorProductSku = (params: BargainSponsorProductSkuQuery) => {
  const { sponsorId, activityId, shopId, productId, skuId, bargainOrderId } = params
  return api.get<BargainSponsorProductSkuRes>(
    `${BASE_URL}getSponsorProductSku/${sponsorId}/${activityId}/${shopId}/${productId}/${skuId}/${bargainOrderId}`,
  )
}
