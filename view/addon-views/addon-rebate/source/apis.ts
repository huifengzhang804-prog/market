import { get, put, post } from '@/apis/http'
import type { PutRebateConfData } from './'

/**
 * 查询消费返利配置
 */
export const doGetRebateConf = () => {
  return get({
    url: 'addon-rebate/rebateConf',
  })
}
/**
 * 编辑消费返利配置
 */
export const doPutRebateConf = (data: PutRebateConfData) => {
  return put({
    url: 'addon-rebate/rebateConf',
    data,
  })
}
/**
 * 分页查询消费返利订单
 */
export const doGetRebateOrder = (params: any): Promise<any> => {
  return get({
    url: 'addon-rebate/rebateOrder',
    params,
  })
}
/**
 * 导出消费返利订单
 */
export const doExportRebateOrder = (data: any): Promise<any> => {
  return post({ url: 'addon-rebate/rebateOrder/export', data })
}
