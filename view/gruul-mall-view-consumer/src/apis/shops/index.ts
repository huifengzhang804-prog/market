import api from '@/libs/request'
/**
 * 新增商户/供应商等
 * @param {*} data
 */
export const doAddShops = (data: any) => {
  return api.post('gruul-mall-shop/shop', data)
}
// 供应商申请、商户申请
export const doShopInfo = (type: 'SUPPLIER' | 'COMMON' | string) => {
  return api.get(`gruul-mall-shop/shop/info/audit/info/${type}`)
}
// 分销申请
export const doDistribution = () => {
  return api.get(`addon-distribute/distribute/distributor/mine/status`)
}
