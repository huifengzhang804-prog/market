import api from '@/libs/request'
import type { ApiGroupAndComItem } from '@/apis/plugin/group/model'
import type { Pagination } from '@/utils/types'

enum API {
  getGroupInfo = 'addon-team/team/product',
}
/**
 * 获取商品拼团状态与数据
 */
export const doGetGroupInfo = (shopId: Long, productId: Long, teamNo?: string) => {
  const query = teamNo ? { teamNo } : {}
  return api.get(`${API.getGroupInfo}/${shopId}/${productId}`, query)
}
export const doGetGroupList = (shopId: Long, productId: Long, current: number, size: number) => {
  return api.get(`addon-team/team/product/${shopId}/${productId}/orders?current=${current}&size=${size}`)
}

/**
 * 查询拼团人员信息
 */
export const doGetGroupUser = (teamNo: string) => {
  return api.get(`addon-team/team/activity/order/users`, {
    size: 300,
    teamNo,
  })
}
/**
 * 轮询查询拼团状态
 */
export const doGetGroupActivityStatus = (teamNo: string) => {
  return api.get(`addon-team/team/activity/${teamNo}/valid`)
}
/**
 * 查询拼团信息
 */
export const doGetGroupActivity = (teamNo: string) => {
  return api.get(`addon-team/team/activity/order/summary`, {
    teamNo,
  })
}
/**
 * 获取拼团列表
 */
export const doGetGroupProductList = (current: number, size: number) => {
  return api.get<Pagination<ApiGroupAndComItem[]>>(`addon-team/team/product`, {
    current,
    size,
  })
}
