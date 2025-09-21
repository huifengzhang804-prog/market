export interface DoPostStoreDistanceListRequestQuery {
  shopId: Long
  point: {
    coordinates: number[]
  }
}
export interface DoPostStoreDistanceListResponseQuery {
  detailedAddress: string
  distance: number
  storeName: string
  functionaryPhone: string
  id: string
}
/**
 * @param businessEndTime 营业结束时间
 * @param businessStartTime 营业开始时间
 * @param endDeliveryDay 结束提货天
 * @param startDeliveryDay 开始提货天
 */
export interface DoGetDeliveryTimeResponseQuery {
  businessEndTime: string
  businessStartTime: string
  endDeliveryDay: number
  startDeliveryDay: number
}
