import { get, post } from '../http'
import { ChatMessage } from '@/views/personalcenter/set/customerservice/types'

/**
 * 分页查询用户列表
 */
export const getMessageUsers = (keywords: string, page: any) => {
  return get({
    url: `/gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-rooms`,
    params: { ...page, keywords },
    showLoading: false,
  })
}

/**
 * 分页查询用户聊天记录
 */
export const getMessages = (shopId: string, page: any) => {
  return get({
    url: `/gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-room`,
    params: { shopId, ...page },
  })
}

/**
 * 发送消息给用户
 * @param userId 用户id
 * @param data 消息体
 */

export const sendMessages = (data: ChatMessage) => {
  return post({
    url: `/gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/message`,
    data,
    showLoading: false,
  })
}

// / 创建聊天室 pigeon/platform-chat-rooms/{shopId}/{userId
export const doGetMessagesChatRoom = (shopId: Long, userId: string | null) => {
  return post({
    url: `gruul-mall-carrier-pigeon/pigeon/platform-chat-rooms/${shopId}${userId ? '/' + userId : ''}`,
  })
}
