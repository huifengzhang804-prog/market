import { get, post, put, del, patch } from '../http'
import { ParamsAfs } from '@/views/personalcenter/order/aftersales/types'
import { RefundRequestType } from './types'
import { L, R } from '../http.type'
import { ApiOrderAfsItem } from '@/views/personalcenter/order/aftersales/types/type'
/**
 * @description: 分页查询售后工单
 * @param {any} params
 * @returns {*}
 */
export const doGetAfsOrder = (params: any) => {
  return get<L<ApiOrderAfsItem>>({ url: `gruul-mall-afs/afs/order`, params })
}
/**
 * @description: 提交售后
 */
export const doSubmitAfss = (data: ParamsAfs) => {
  return post({ url: 'gruul-mall-afs/afs/order', data })
}
/**
 * @description: 获取售后订单信息
 * @param {any} afsNo
 */
export const doGetAfssInfo = (afsNo: string) => {
  return get<RefundRequestType>({ url: `gruul-mall-afs/afs/order/${afsNo}` })
}
/**
 * @description: 撤销申请
 * @param {any} afsNo
 */
export const doPutAfssCancel = (afsNo: string) => {
  return put({ url: `gruul-mall-afs/afs/order/${afsNo}/close` })
}
/**
 * @description: 协商历史
 * @param {any} afsNo
 */
export const doGetAfssHistory = (afsNo: string) => {
  return get<RefundRequestType[]>({ url: `gruul-mall-afs/afs/order/${afsNo}/history` })
}
/**
 * @description: 查询用户昵称与头像
 */
export const doGetUserData = () => {
  return get({ url: `gruul-mall-uaa/uaa/user/data` })
}
/**
 * @description:查询 店铺 名称
 * @param {string} shopId
 * @returns {*}
 */
export const doGetShopInfo = (shopId: string) => {
  return get({ url: `gruul-mall-shop/shop/info/base/${shopId}` })
}
/**
 * @description: 用户退货退款
 * @param {string} afsNo
 * @returns {*}
 */
export const doGetReturnedByAfsNo = (afsNo: string, type: 'EXPRESS_REFUND' | 'GO_STORE_REFUND', data: any) => {
  return put({ url: `gruul-mall-afs/afs/order/${afsNo}/${type}/returned`, data })
}
/**
 * @description: 确认收货
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @param {string} packageId
 * @returns {*}
 * (`gruul-mall-order/order/${orderNo}/shopOrder/${shopId}/confirm`)
 */
export const doconfirmGoods = (orderNo: string, shopOrderNo: string, packageId: string) => {
  // return put({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/package/${packageId}/confirm` })
  return put({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${packageId}/confirm` })
}
