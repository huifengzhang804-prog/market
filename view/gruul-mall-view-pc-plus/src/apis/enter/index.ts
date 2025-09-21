import { get, post, put, del, patch } from '../http'
/**
 * @description: 新增商户/供应商等
 * @param {*} data
 * @returns {*}
 */
export const doAddShops = (data: any) => {
  return post({ url: 'gruul-mall-shop/shop', data })
}
