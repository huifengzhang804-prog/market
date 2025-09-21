import api from '@/libs/request'
const BASE_URL = 'addon-rebate/'
export const doGetRebateTransactions = () => {
  return api.get(BASE_URL + `rebateTransactions`)
}
