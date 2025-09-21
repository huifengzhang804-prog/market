import api from '@/libs/request'

/**
 * 获取分销配置
 */
export const doGetDistributeConfig = () => {
  return api.get('addon-distribute/distribute/config')
}

/**
 * 获取分销码
 */

export const doGetDistributeCode = () => {
  return api.get('addon-distribute/distribute/distributor/mine')
}

/**
 * 分销申请表单
 */
export const doPostDistributeForm = (data: any) => {
  return api.post('addon-distribute/distribute/distributor/affairs/apply', data)
}

/**
 * 获取分销中心数据
 */

export const doGetDistributeCenter = () => {
  return api.get('addon-distribute/distribute/distributor/mine/statistics')
}

/**
 * 获取分销客户
 */
export const doGetDistributeCustomer = (params: any) => {
  return api.get('addon-distribute/distribute/consumer/mine/customer', params)
}
/**
 * 获取分销订单
 */

export const doGetDistributeOrder = (params: any) => {
  return api.get(`addon-distribute/distribute/order`, params)
}
/**
 * 佣金提现检查
 */
export const doGetCheckWithdrawOrder = () => {
  return api.get('addon-distribute/distribute/bonus/withdraw')
}
/**
 * 佣金提现
 */
export const doPostWithdraw = (data: any) => {
  return api.post('addon-distribute/distribute/bonus/withdraw', data)
}
/**
 * 佣金明细
 */
export const doGetWithdrawList = (params: any) => {
  return api.get(`gruul-mall-overview/overview/withdraw/distribute/mine`, params)
}
/**
 * 分页查询分销商品
 */
export const doPostGoods = (data: any) => {
  return api.post('addon-distribute/distribute/product/page', data)
}
/**
 * 分页查询分销排行榜
 */
export const doGetdisRank = (params: any) => {
  return api.get(`addon-distribute/distribute/distributor/rank`, params)
}
/**
 * 根据分销商用户id查询下线分销员
 */
export const doGetDistributeTeam = (params: any) => {
  return api.get('addon-distribute/distribute/distributor/team', params)
}
/**
 * 查询我的佣金提现账户
 */
export const doGetaccounts = () => {
  return api.get('gruul-mall-overview/withdraw/bonus/accounts')
}
/**
 * 查询我的佣金提现账户
 */
export const doPutaccounts = (data: any) => {
  return api.put('gruul-mall-overview/withdraw/bonus/account', data)
}

/**
 * 查询分销配置
 */
export const doGetDistributionEnable = () => {
  return api.get('gruul-mall-payment/pay/valid/service/enable')
}
