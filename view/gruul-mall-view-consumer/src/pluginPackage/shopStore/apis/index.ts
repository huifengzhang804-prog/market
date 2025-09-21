import api from '@/libs/request'
import type { DoPostStoreDistanceListRequestQuery } from '@/apis/plugin/shopStore/model'

const BASE_URL = 'addon-shop-store/'
/**
 * 获取门店提货点 按距离排序
 */
export const doPostStoreDistanceList = (data: DoPostStoreDistanceListRequestQuery) => {
  return api.post(`${BASE_URL}store/distance/list`, data)
}
/**
 * 根据门店提货点查询提货时间
 * @param {string} shopId
 * @param {string} params
 */
export const doGetDeliveryTime = (shopId: Long, params: { id: string }) => {
  return api.get(`${BASE_URL}store/optional/delivery/time/${shopId}`, params)
}
/**
 * 获取门店核销码
 * @param {string} storeId
 * @param {string} orderNo
 */
export const doGetOrderGetCodeByStoreId = (storeId: string, orderNo: string) => {
  return api.get(`${BASE_URL}store/order/get/code/${storeId}/${orderNo}`)
}
