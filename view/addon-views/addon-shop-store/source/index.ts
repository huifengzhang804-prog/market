export interface storeListType {
  id: string
  storeName: string
  shopId: string
  shopName: string
  detailedAddress: string
  functionaryName: string
  functionaryPhone: string
  status: string
}
export interface salesclerkListType {
  assistantPhone: string
  id: string
  storeId: string
  storeName: string
}
export interface StoreFormType {
  id: string
  storeName: string
  storeLogo: string
  storeImg: string
  storePhone: string
  functionaryName: string
  functionaryPhone: string
  businessStartTime: string
  businessEndTime: string
  startDeliveryDay: number
  endDeliveryDay: number
  location: LocationType
  detailedAddress: string
  shopAssistantList: any[]
}
export type LocationType = {
  type: string
  coordinates: string[]
}
