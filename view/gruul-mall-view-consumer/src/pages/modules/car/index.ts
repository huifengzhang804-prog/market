import STORAGE from '@/utils/storage'
import type { GoodItemType, StoragePackage } from './types'
import { toConfirmOrderValid } from '@/apis/order'

/**
 * 存缓存 跳页面
 * @param {*} params
 */
export async function navigateToConfirmOrder(params: StoragePackage[]) {
  STORAGE.set('commodityInfo', params)
  const navFn = () => {
    uni.navigateTo({
      url: '/pluginPackage/order/confirmOrder/confirmOrder?source=CART',
    })
  }
  const res = await toConfirmOrderValid(params)
  if (res.success) {
    navFn()
  }
  return res
}
