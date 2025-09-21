import type { ShopOrderItem, ApiOrder } from '@/pluginPackage/order/orderList/types'
import { orderMapComputed } from './orderHooks'
import type { DistributionKeyType } from '@/apis/good/model'
import storage from '@/utils/storage'

type ShopOrderMap = {
  items: ShopOrderItem[]
  merged: ShopOrderItem
}
type SetStorageSyncExtra = {
  payStatus: ApiOrder['status']
  orderNo: ApiOrder['no']
  distributionMode: DistributionKeyType
}
type ReturnTypeMap = ReturnType<typeof orderMapComputed>

const STORAGE_KEY = 'ORDER_DETAIL_TO_AFS'
function useOrderDetailToAfs() {
  function setStorageSync(params: any, extra: SetStorageSyncExtra) {
    storage.set(STORAGE_KEY, { ...params, extra })
  }
  function getStorageSync() {
    return storage.get(STORAGE_KEY) || {}
  }
  function toAfsPage() {
    uni.navigateTo({ url: '/pluginPackage/order/orderDetail/AfterSalesRequest' })
  }
  return {
    setStorageSync,
    getStorageSync,
    toAfsPage,
  }
}
export { useOrderDetailToAfs, type ShopOrderMap, type ReturnTypeMap, type SetStorageSyncExtra }
