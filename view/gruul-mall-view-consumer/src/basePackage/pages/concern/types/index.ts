export interface ConcernItem {
  logo: string
  newProducts: boolean
  numberFollowers: number
  shopId: Long
  shopName: string
}
export type RequestParams = {
  current: number
  size: number
  status: string
  shopName: string
}
