import api from '@/libs/request'
import type { ParamsAfs } from '@pluginPackage/order/applyAfterSales/types'
/**
 * 分页查询售后工单
 * @param {any} params
 */
export const doGetAfsOrder = (params: any) => {
  return api.get(`gruul-mall-afs/afs/order`, params)
}
/**
 * 等待接收
 * 提交售后
 * @param {any} data
 */
export const doSubmitAfss = (data: ParamsAfs) => {
  return api.post('gruul-mall-afs/afs/order', data)
}
/**
 * 获取售后订单信息
 * @param {any} afsNo
 */
export const doGetAfssInfo = (afsNo: string) => {
  return api.get(`gruul-mall-afs/afs/order/${afsNo}`)
}
/**
 * 撤销申请
 * @param {any} afsNo
 */
export const doPutAfssCancel = (afsNo: string) => {
  return api.put(`gruul-mall-afs/afs/order/${afsNo}/close`)
}
/**
 * 协商历史
 * @param {any} afsNo
 */
export const doGetAfssHistory = (afsNo: string) => {
  return api.get(`gruul-mall-afs/afs/order/${afsNo}/history`)
}
/**
 * 查询用户昵称与头像
 */
export const doGetUserData = () => {
  return api.get(`gruul-mall-uaa/uaa/user/data`)
}

/**
 * 用户退货退款
 * @param {string} afsNo
 */
export const doGetReturnedByAfsNo = (afsNo: string, type: 'EXPRESS_REFUND' | 'GO_STORE_REFUND', data: any) => {
  return api.put(`gruul-mall-afs/afs/order/${afsNo}/${type}/returned`, data)
}
/** * 确认收货
 * @param {string} orderNo
 */
export const doconfirmGoods = (orderNo: string, shopId: Long) => {
  return api.put(`gruul-mall-order/order/${orderNo}/shopOrder/${shopId}/confirm`)
}
