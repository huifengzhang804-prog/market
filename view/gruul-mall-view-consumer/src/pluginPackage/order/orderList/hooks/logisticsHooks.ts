import storage from '@/utils/storage'

const LOGISTICS_STORAGE_KEY = 'LOGISTICS_STORAGE_KEY'
function useQueryLogistics() {
  function setLogisticsStorage(params: any) {
    storage.set(LOGISTICS_STORAGE_KEY, params)
  }
  function getLogisticsStorage() {
    return storage.get(LOGISTICS_STORAGE_KEY) || {}
  }
  function toLogisticsPage(query: string) {
    const url = '/pluginPackage/order/orderDetail/LogisticsInfo'
    uni.navigateTo({ url: query ? url + query : url })
  }
  return { setLogisticsStorage, getLogisticsStorage, toLogisticsPage }
}
export { useQueryLogistics }
