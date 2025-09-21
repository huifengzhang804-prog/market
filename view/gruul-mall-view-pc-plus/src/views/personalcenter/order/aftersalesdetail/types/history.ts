import type { PACKAGESTATUS } from '@/views/personalcenter/order/myorder/types'
import { AFSSTATUS, A_REfUND_WHY } from '@/views/personalcenter/order/aftersales/types/index'
export { ApiUserData, ApiShopInfo, ProcessingHistory }
/**
 * @description: 后端返回的User接口信息
 * @returns {*}
 */
interface ApiUserData {
  avatar: string
  createTime: string
  deleted: false
  gender: string
  id: string
  nickname: string
  updateTime: string
  userId: string
  version: 0
}
/**
 * @description: 后端返回的Shop接口信息
 * @returns {*}
 */
interface ApiShopInfo {
  id: string
  logo: string
  name: string
}
/**
 * @description: 加工后的协商历史数据
 * @returns {*}
 */
interface ProcessingHistory {
  isConsumer: boolean
  afsStatusCn?: string
  reason?: keyof typeof A_REfUND_WHY
  refundAmount?: string
  afsStatus: keyof typeof AFSSTATUS
  createTime: string
  evidences: string[]
  id: string
  logo: string
  name: string
  packageStatus: keyof typeof PACKAGESTATUS
  remark: string
  updateTime: string
}
