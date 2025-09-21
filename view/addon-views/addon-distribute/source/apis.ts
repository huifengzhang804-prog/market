import { get, post, put, del, patch } from '@/apis/http'
import type { DistributeParams, DistributeConfigParamsType, DistributionResponse } from '.'
import { L, Obj } from '@/utils/types'

export const doPostDistribute = (data: DistributeConfigParamsType) => {
  return put({
    url: 'addon-distribute/distribute/config',
    data,
  })
}

/**
 * 新增分销商品
 */
export const doPostDistributeCom = (data: DistributeParams) => {
  return post({
    url: 'addon-distribute/distribute/product',
    data,
  })
}
/**
 * 修改分销商品
 */
export const doPutDistributeCom = (id: string, data: DistributeParams) => {
  return put({
    url: `addon-distribute/distribute/product/${id}`,
    data,
  })
}
/**
 * 获取分销商品列表
 */
export const doGetDistributeCom = <T>(data: any) => {
  return post<L<T>>({
    url: 'addon-distribute/distribute/product/page',
    data,
  })
}
/**
 * 删除分销商品
 */
export const doDelDistributeCom = (ids: string[]) => {
  return del({
    url: `addon-distribute/distribute/product/${ids.join(',')}`,
  })
}
/**
 * 取消分销商品
 */
export const doDelDistributeCancel = (bindIds: string[]) => {
  return put({
    url: `addon-distribute/distribute/product/cancel/${bindIds.join(',')}`,
  })
}
/**
 * 重新分销商品
 */
export const doDelDistributeAgain = (bindIds: string) => {
  return put({
    url: `addon-distribute/distribute/product/again/${bindIds}`,
  })
}
/**
 * 获取商品列表
 */
export const doGetCommodity = (params: any) => {
  return get({
    url: 'gruul-mall-goods/manager/product/list',
    params,
  })
}
/**
 * 获取平台分销配置
 */
export const doGetDistributeConfig = () => {
  return get<Obj>({
    url: 'addon-distribute/distribute/config',
  })
}
/**
 * 分页查询分销订单
 */
export const doGetDisOrder = (params: any) => {
  return get<DistributionResponse>({
    url: `addon-distribute/distribute/order`,
    params,
  })
}
/**
 * 获取分销商列表
 */
export const doGetDistributorList = (params: any): Promise<any> => {
  return get({
    url: 'addon-distribute/distribute/distributor',
    params,
  })
}
/**
 * 获取分销商下线
 */
export const doGetDisMember = (params: any): Promise<any> => {
  return get({
    url: `addon-distribute/distribute/distributor/team`,
    params,
  })
}
/**
 * 分页查询分销排行榜
 */
export const doGetDisRank = (params: any): Promise<any> => {
  return get({
    url: `addon-distribute/distribute/distributor/rank`,
    params,
  })
}

/**
 * 下架分销商品
 */
export const doPutDistributeStatus = (data: any) => {
  return put({
    url: 'addon-distribute/distribute/product/forbidden',
    data,
  })
}
/**
 * 取消分销
 */
export const doPutCancelDistribute = (bindIds: string[]) => {
  return put({ url: `addon-distribute/distribute/product/cancel/${bindIds}` })
}
/**
 * 同意拒绝分销商申请(批量)
 * @param pass true为同意 false 拒绝
 */
export const doPutDistributorApply = (pass: boolean, ids: string[], rejectReason?: string) => {
  return post({
    url: `addon-distribute/distribute/distributor/distributor/apply`,
    data: {
      pass: pass,
      ids: ids,
      rejectReason: rejectReason,
    },
  })
}

/**
 * 会员详情获取上级分销信息
 */
export const doGetParentDisInfo = (userId: string) => {
  return get({
    url: `addon-distribute/distribute/distributor/distributor/parent/${userId}`,
  })
}

/**
 * 查询分销商申请被拒绝的原因
 * @param id
 */
export const doGetRejectReason = (id: string) => {
  return get<string>({
    url: `addon-distribute/distribute/distributor/rejectReason/${id}`,
  })
}

/**
 * 导出分销订单
 */
export const doExportDisOrder = (data: any) => {
  return post({
    url: `addon-distribute/distribute/order/export`,
    data,
  })
}
