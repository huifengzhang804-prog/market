import api from '@/libs/request'
import type { ChatMessage } from '@/basePackage/pages/customerService/types'

/**
 * 获取订阅模板
 */
export const getTemplateIds = () => {
  return api.get(`gruul-mall-carrier-pigeon/appletMessageSubscribe/getTemplateIds`)
}
