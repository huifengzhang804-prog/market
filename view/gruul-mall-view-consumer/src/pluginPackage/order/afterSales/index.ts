import { ARefundType, A_REfUND_WHY, AFSSTATUS } from '@pluginPackage/order/applyAfterSales/types'
import { PACKAGESTATUS } from '@pluginPackage/order/orderList/types'
import type { ServicesType } from '@pluginPackage/goods/commodityInfo/types'

/**
 * 售后列表接口返回
 */
export interface ApiOrderAfsItem {
  afsOrderReceiver?: AfsOrderReceiver
  afsOrderItem: AfsOrderItem
  buyerId: string
  buyerNickname: string
  createTime: string
  explain: string
  id: string
  no: string
  orderNo: string
  timeout: KeyNodeTimeout
  packageStatus: keyof typeof PACKAGESTATUS
  reason: keyof typeof A_REfUND_WHY
  refundAmount: string
  shopId: Long
  shopOrderItemId: string
  status: keyof typeof AFSSTATUS
  type: keyof typeof ARefundType
  updateTime: string
  packageId?: string
  shopName?: string
  shopLogo?: string
}
interface KeyNodeTimeout {
  confirmReturnedTimeout: string
  requestAgreeTimeout: string
  returnedTimeout: string
}
interface AfsOrderReceiver {
  address: string
  area: string[]
  mobile: string
  name: string
}
export interface AfsOrderItem {
  createTime: string
  dealPrice: string
  image: string
  num: number
  productId: Long
  productName: string
  salePrice: Long
  skuId: Long
  specs: string[]
  updateTime: string
  version: 0
  services: ServicesType[]
}
