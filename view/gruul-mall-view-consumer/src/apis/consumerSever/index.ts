import api from '@/libs/request'
import type { ChatMessage, ShopChatMessage } from '@/basePackage/pages/customerService/types'

/**
 * 客服消息列表
 */
export const doGetPigeonMessageShop = (params?: any) => {
  return api.get(`gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-rooms`, params)
}

/**
 * 消息已读
 * @param {Long} shopId
 * @param {ChatMessage} data
 */
export const doPutPigeonMessageShopRead = (shopId: Long, params?: any) => {
  return api.put(`gruul-mall-carrier-pigeon/pigeon/message/shop/${shopId}/read`, params)
}
/**
 * 我的消息查询
 */
export const doGetPigeonMessageMyCount = () => {
  return api.get(`gruul-mall-carrier-pigeon/pigeon/message/my/unread/count`)
}

/**
 * 用户发送信息给平台
 * @param {ChatMessage} data
 */
export const doSendMessagePlatform = (data: ChatMessage) => {
  return api.post(`gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/message`, data)
}
// 获取平台聊天列表
export const doGetPlatformChatRoom = (params: any) => {
  return api.get(`gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-room`, params)
}
// / 创建聊天室 pigeon/platform-chat-rooms/{shopId}/{userId
export const doGetMessagesChatRoom = (shopId: Long, userId: string | null) => {
  return api.post(`gruul-mall-carrier-pigeon/pigeon/platform-chat-rooms/${shopId}${userId ? '/' + userId : ''}`)
}
