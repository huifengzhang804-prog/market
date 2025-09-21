import api from '@/libs/request'
const BASE_URL = 'addon-rebate/'
export const doGetRebatePayBalance = (params: any) => {
  return api.get(`${BASE_URL}rebateTransactions/rebatePayBalance`, params)
}
