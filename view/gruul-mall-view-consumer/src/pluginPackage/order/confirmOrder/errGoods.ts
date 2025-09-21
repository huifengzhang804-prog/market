import type { OrderProductType } from './types'
interface ApiData {
  shopId: Long
  productId?: string
  skuId?: string
}
/**
 * 限购
 */
export const errorOrderProcessing = (list: OrderProductType[], data: ApiData, msg: string, title: string) => {
  let id: Long = ''
  const errGoods = list.find((item) => item.shopId === data?.shopId)
  if (!errGoods) {
    uni.showToast({
      icon: 'none',
      title: `${msg ? title : '订单提交失败'}`,
    })
    return id
  }
  id = data.shopId
  if (data.productId && !data.skuId) {
    const currentErrGoods = errGoods.products.find((item) => item.productId === data.productId)
    if (currentErrGoods) {
      id = data.productId
      uni.showToast({
        icon: 'none',
        // title: `${msg ? currentErrGoods.productName + title : '订单提交失败'}`,
        title: `${msg ? title : '订单提交失败'}`,
      })
    }
    return id
  }
  if (data.skuId) {
    const currentErrGoods = errGoods.products.find((item) => item.productId === data.productId && item.skuId === data.skuId)
    if (currentErrGoods) {
      id = data.skuId
      uni.showToast({
        icon: 'none',
        title: `${msg ? title : '订单提交失败'}`,
      })
    }
    return id
  }
  uni.showToast({
    icon: 'none',
    title: `${msg ? msg : '订单提交失败'}`,
  })
  return id
}
