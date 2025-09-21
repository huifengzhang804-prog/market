import { http } from '@/utils/http'
import type { ApiGroupOrderItem, ApiGroupList, ApiSearchParam, ApiGroupOrderParams, ApiGroupForm, ApiGroupActivity, ApiGroupUser } from '.'
import type { Pagination } from '@/utils/types'
import Decimal from 'decimal.js'
import { Long } from '@/apis/good'

export enum API {
  orderList = 'addon-team/team/activity/order',
  groupList = 'addon-team/team/activity',
  groupForm = 'addon-team/team/activity',
  fillGroupForm = 'addon-team/team/activity',
  delGroup = 'addon-team/team/activity/delete/batch',
}
/**
 * 获取拼团活动列表
 */
export const doGetGroupList = (params: ApiSearchParam) => {
  return http.get<Pagination<ApiGroupList[]>>(
    {
      url: API.groupList,
      params,
    },
    { isMock: false },
  )
}
/**
 * 获取拼团订单
 */
export const doGetGroupOrderList = (params: ApiGroupOrderParams) => {
  return http.get<Pagination<ApiGroupOrderItem[]>>(
    {
      url: API.orderList,
      params,
    },
    { isMock: false },
  )
}
export const doPostGroupForm = (data: ApiGroupForm) => {
  return http.post({
    url: API.groupForm,
    data,
  })
}
/**
 * 编辑回显
 */
export const doGetGroupForm = (id: string) => {
  return http.get<ApiGroupForm>({
    url: API.fillGroupForm + `/${id}`,
  })
}
/**
 * 批量删除活动
 */
export const doDelGroupActivity = (data: { shopId: Long; activityId: Long }[]) => {
  return http.post({
    url: API.delGroup,
    data,
  })
}
/**
 * 查询拼团订单详情
 */
export const doGetOrderDetail = (teamNo: string) => {
  return http.get<ApiGroupActivity>({
    url: `addon-team/team/activity/order/summary?teamNo=${teamNo}`,
  })
}
/**
 * 查询拼团订单人员信息
 */
export const doGetGroupOrderUser = (teamNo: string, pageConfig: { current: number; size: number }) => {
  return http.get<Pagination<ApiGroupUser[]>>({
    url: `addon-team/team/activity/order/users`,
    params: {
      teamNo,
      size: pageConfig.size,
      current: pageConfig.current,
    },
  })
}
/**
 * 下架
 */
export const doPutGroupActivityStatus = (data: { teamActivityId: string; shopId: string; violationReason: string }) => {
  return http.post({
    url: `addon-team/team/activity/platform/violate`,
    data,
  })
}
export const doGetGroupListPlatform = (params: ApiSearchParam) => {
  return http.get<Pagination<ApiGroupList[]>>(
    {
      url: 'addon-team/team/activity',
      params,
    },
    { isMock: false },
  )
}
/**
 * 批量删除
 */
export const doDelBatchGroup = (data: { shopId: string; activityId: string }[]) => {
  return http.post({
    url: 'addon-team/team/activity/delete/batch',
    data,
  })
}
/**
 * 违规下架
 */
export const doViolation = (shopId: string, activityId: string) => {
  return http.post({
    url: `addon-team/team/activity/${shopId}/${activityId}/violate`,
  })
}
/**
 * 违规原因
 */
export const doGetIllegalReason = (id: string) => {
  return http.get({
    url: `addon-team/team/activity/illegal/${id}/reason`,
  })
}
/**
 * 编辑回显
 */
export const doGetGroupFormPlatform = (shopId: string, activityId: string) => {
  return http.get<ApiGroupForm>({
    url: `addon-team/team/activity/${activityId}`,
    params: {
      shopId,
    },
  })
}

// 分页查询拼团活动商品列表
export const groupListApi = (params?: any) => {
  return http.get({ url: 'addon-team/team/product', params })
}
/**
 * 获取商品详情
 */
export const doGetGoodDetail = (goodId: string, shopId: string) => {
  return http.get({ url: `gruul-mall-goods/api/product/get/${goodId}`, headers: { 'Shop-Id': shopId } })
}

type DecimalType = Decimal

export function useConversionPrice(price: string | number | DecimalType = 0): DecimalType {
  if (!price) price = 0
  return new Decimal(price).div(10000)
}
// 获取商品拼团状态与数据
export const groupPurchaseNum = (shopId: string, productId: string) => {
  return http.get({ url: `addon-team/team/product/${shopId}/${productId}` })
}

//  查询拼团信息
export const doGetGroupActivity = (teamNo: string) => {
  return http.get({ url: `addon-team/team/activity/order/summary?teamNo=${teamNo}` })
}

// 查询拼团人员信息
export const doGetGroupUser = (teamNo: string) => {
  return http.get({ url: `addon-team/team/activity/order/users?size=300&teamNo=${teamNo}` })
}

/**
 * 查询商品sku
 */
export const doGetGoodSku = (productId: string, shopId: string) => {
  return http.get({ url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}` })
}
/**
 * 查询店铺基础信息
 */
export const doGetShopBaseInfo = (shopId: string) => {
  return http.get({ url: `gruul-mall-shop/shop/info/base/${shopId}` })
}

// 分页查询商品凑团订单列表
export const ordersApi = (shopId: string, productId: string, current: number, size: number) => {
  return http.get({ url: `addon-team/team/product/${shopId}/${productId}/orders?current=${current}&size=${size}` })
}
/**
 * 下单页查询默认地址
 */
export const doGetDefaultAddress = () => {
  return http.get({ url: 'gruul-mall-user/user/address/default' })
}
/**
 * 添加收藏
 */
export const doAddAssess = (assessData: any[]) => {
  return http.post({ url: `gruul-mall-user/user/collect`, data: assessData })
}
/**
 * 查询收藏
 */
export const doGetAssessOrder = (shopId: string, productId: string) => {
  return http.get({ url: 'gruul-mall-user/user/collect/findUserIsCollect', params: { shopId, productId } })
}
/**
 * 取消收藏
 */
export const doCancelAssessOrder = (shopId: string, productId: string) => {
  return http.get({ url: 'gruul-mall-user/user/collect/cancel', params: { shopId, productId } })
}
export const doGetFreightCalculation = (data: any) => {
  return http.post({ url: 'gruul-mall-order/order/distribution/cost', data })
}
export const timerApi = (shopId: string, productId: string) => {
  return http.get({ url: `gruul-mall-search/search/product/activity/${shopId}/${productId}` })
}

/**
 * 下架店铺
 */
export const doShopShelf = (id: string) => {
  return http.post({ url: `addon-team/team/activity/shop/shelf/${id}` })
}

/**
 * 下架店铺
 */
export const doGetReason = (id: string) => {
  return http.get({ url: `addon-team/team/activity/illegal/${id}/reason` })
}
