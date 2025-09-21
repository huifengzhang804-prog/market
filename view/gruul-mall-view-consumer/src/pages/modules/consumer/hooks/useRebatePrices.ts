import { doGetRebateTransactions } from './rebateApi'
import { isEmpty } from 'lodash'

interface RebateTransactions {
  // 返利余额
  rebateBalance: string
  accumulatedRebate: string
  unsettledRebate: string
  expiredRebate: string
}

const { divTenThousand } = useConvert()

const useRebatePrices = () => {
  async function init(): Promise<RebateTransactions> {
    const { code, data, msg } = await doGetRebateTransactions()
    if (code !== 200 && code !== 2) {
      uni.showToast({ title: `${msg || '获取返利余额失败'}`, icon: 'none' })
      return {
        accumulatedRebate: '0',
        expiredRebate: '0',
        rebateBalance: '0',
        unsettledRebate: '0',
      }
    }
    return isEmpty(data)
      ? {
          accumulatedRebate: '0',
          expiredRebate: '0',
          rebateBalance: '0',
          unsettledRebate: '0',
        }
      : data
  }

  function formatPrice(value: string) {
    return (value && divTenThousand(value).toFixed(2)) || '0.00'
  }

  return { init, formatPrice }
}
export { useRebatePrices }
