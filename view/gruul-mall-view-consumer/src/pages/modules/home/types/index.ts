import { ADD_RESS_TYPES } from '@/apis/address/model'

/**
 * 配送方式
 * @param MERCHANT 商家配送
 * @param EXPRESS 物流配送
 */
export type lodingType = '加载中' | '成功' | '未配置' | '失败'

export interface DecorateItemType {
  formData: any
  icon: string
  id: string
  label: string
  value: string
}
export interface dataItem {
  list: any[]
  status: lodingType
  name: string
  active: boolean
  renderName: string
}
