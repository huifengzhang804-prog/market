import type { OrderType } from './types'
import { ElMessage } from 'element-plus'
interface ApiData {
  shopId: string
  productId?: string
  skuId?: string
}
/**
 * @description: 限购
 * @returns {*}
 */
export const errorOrderProcessing = (list: OrderType[], data: ApiData, msg: string, title: string) => {
  let id = ''
  const errGoods = list.find((item) => item.shopId === data.shopId)
  if (!errGoods) {
    ElMessage.error(`${msg ? msg : '订单提交失败'}`)
    return id
  }
  id = data.shopId
  if (data.productId && !data.skuId) {
    const currentErrGoods = errGoods.products.find((item) => item.productId === data.productId)
    if (currentErrGoods) {
      id = data.productId
      ElMessage.error(`${msg ? currentErrGoods.productName + title : '订单提交失败'}`)
    }
    return id
  }
  if (data.skuId) {
    const currentErrGoods = errGoods.products.find((item) => item.productId === data.productId && item.skuId === data.skuId)
    if (currentErrGoods) {
      id = data.skuId
      ElMessage.error(`${msg ? currentErrGoods.productName + title : '订单提交失败'}`)
    }
    return id
  }
  ElMessage.error(`${msg ? msg : '订单提交失败'}`)
  return id
}
