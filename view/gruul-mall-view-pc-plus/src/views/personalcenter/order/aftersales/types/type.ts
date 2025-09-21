import type { ARefundType, A_REfUND_WHY, AFSSTATUS } from './index'
import type { PACKAGESTATUS } from '@/views/personalcenter/order/myorder/types'
/**
 * @description: 售后列表接口返回
 * @returns {*}
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
  keyNodeTimeout: KeyNodeTimeout
  packageStatus: keyof typeof PACKAGESTATUS
  reason: keyof typeof A_REfUND_WHY
  refundAmount: string
  shopId: string
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
  areaCode: string[]
  mobile: string
  name: string
}
interface AfsOrderItem {
  createTime: string
  dealPrice: string
  image: string
  num: number
  productId: string
  productName: string
  salePrice: string
  skuId: string
  specs: string[]
  updateTime: string
  version: 0
}
