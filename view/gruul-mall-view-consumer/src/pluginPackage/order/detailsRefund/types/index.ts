import { A_REfUND_WHY, AFSSTATUS, ARefundType } from '@pluginPackage/order/applyAfterSales/types'
import { PACKAGESTATUS } from '@pluginPackage/order/orderList/types'
import type { AfsOrderItem } from '../../afterSales'

export type { ApiAfsOrder }

/** 售后详情后端返回参数
 * @param buyerId 用户id
 * @param explain 退款说明
 * @param histories 协商历史
 * @param no 售后单号
 * @param orderNo 售后工单类型
 * @param reason 说明
 * @param refundAmount 申请退款金额
 * @param shopId
 * @param shopOrderItemId 售后单id
 * @param status 售后状态
 * @param type 退款类型
 * @param updateTime 更新时间
 */
interface ApiAfsOrder {
  afsOrderItem: AfsOrderItem
  buyerId: string
  createTime: string
  explain: string
  histories: []
  id: string
  no: string
  orderNo: string
  reason: keyof typeof A_REfUND_WHY
  refundAmount: string
  timeout: AfsKeyNodeTimeout
  shopId: Long
  shopOrderItemId: string
  status: keyof typeof AFSSTATUS
  type: keyof typeof ARefundType
  updateTime: string
  packageId?: string
  afsPackage?: AfsPackage
  packageStatus?: keyof typeof PACKAGESTATUS
}

interface AfsKeyNodeTimeout {
  confirmReturnedTimeout: string
  requestAgreeTimeout: string
  returnedTimeout: string
}

interface AfsPackage {
  createTime: string
  id: string
  receiverAddress: string
  receiverMobile: string
  receiverName: string
  remark: string
  senderMobile: string
  type: string
  updateTime: string
  expressCompanyName: string
  expressCompanyCode: string
  expressNo: string
}
