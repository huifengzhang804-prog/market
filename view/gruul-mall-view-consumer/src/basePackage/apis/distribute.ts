import api from '@/libs/request'
/**
 * 查询我的佣金提现账户
 */
export const doGetaccounts = () => {
  return api.get('gruul-mall-overview/withdraw/bonus/accounts')
}
