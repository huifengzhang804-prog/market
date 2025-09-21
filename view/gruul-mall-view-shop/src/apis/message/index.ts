import { get, post } from '../http'
import { Page } from '@/components/BetterPageManage'
import { ChatMessage } from '@/views/message/customerService/types'

/**
 * 分页查询当前店铺通知
 */
export const messagePage = (params: any): Promise<any> => {
    return get({
        url: '/gruul-mall-carrier-pigeon/pigeon/notice',
        params,
        showLoading: false,
        errorImmediately: false,
    })
}

/**
 * 根据id获取消息详情
 */
export const getMessageById = (messageId: string) => {
    return get({
        url: `/gruul-mall-carrier-pigeon/pigeon/notice/${messageId}`,
    })
}

/**
 * 分页查询用户列表
 */
export const getMessageUsers = (keywords: string, page: any, shopId: Long) => {
    return get({
        url: `/gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-rooms`,
        params: { ...page, keywords, shopId },
        showLoading: false,
    })
}

/**
 * 分页查询用户聊天记录
 */
export const getMessages = (userId: string, page: any) => {
    return get({
        url: `/gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-room`,
        params: { userId, ...page },
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
