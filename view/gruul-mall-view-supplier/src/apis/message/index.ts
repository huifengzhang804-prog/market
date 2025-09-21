import { get, post, put } from '../http'
import { Page } from '@/components/pageManage'
import { SendMessage, ReceiverMessage } from '@/views/mall/customerService/types'
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
export const getMessageUsers = (keywords: string, page: any) => {
    return get({
        url: `/gruul-mall-carrier-pigeon/pigeon/group-chat-room-messages/chat-rooms`,
        params: { ...page, keywords },
        showLoading: false,
    })
}

/**
 * 分页查询用户聊天记录
 */

export const getMessages = (receiverMessage: ReceiverMessage, page: Page) => {
    return get({
        url: `/gruul-mall-carrier-pigeon/pigeon/group-chat-room-messages/chat-room`,
        params: { ...receiverMessage, ...page },
        showLoading: false,
    })
}

/**
 * 发送消息给用户
 * @param userId 用户id
 * @param data 消息体
 */
export const sendMessages = (data: SendMessage) => {
    return post({
        url: `/gruul-mall-carrier-pigeon/pigeon/group-chat-room-messages/message`,
        data,
        showLoading: false,
    })
}
