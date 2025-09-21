import { doGetRebateTransactions } from '../apis'
import type { RebateTransactions } from '@/pluginPackage/rebate/apis/model'

const { divTenThousand } = useConvert()

const useRebatePrices = () => {
  async function init(): Promise<RebateTransactions> {
    const basicData = {
      accumulatedRebate: '0',
      expiredRebate: '0',
      rebateBalance: '0',
      unsettledRebate: '0',
    }
    try {
      const { code, data, msg } = await doGetRebateTransactions()
      if (code !== 200) {
        uni.showToast({ title: `${msg || '获取返利余额失败'}`, icon: 'none' })
      } else {
        Object.keys(basicData).forEach((key) => {
          // @ts-ignore
          if (data?.[key]) basicData[key] = data?.[key]
        })
      }
      return basicData
    } catch (err) {
      console.log(err)
      return basicData
    }
  }
  function formatPrice(value: string) {
    return divTenThousand(value).toFixed(2) || '0.00'
  }
  return { init, formatPrice }
}
export { useRebatePrices }
