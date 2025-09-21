import { get, post, put, del } from '../http'
/**
 * 消息类型
 */
enum MessageType {
    //文本
    TEXT = 'TEXT',
    //图片
    IMAGE = 'IMAGE',
    //商品
    PRODUCT = 'PRODUCT',
    // 订单
    ORDER = 'ORDER',
}

/**
 * 分页参数
 */
export interface Page {
    size: number
    current: number
}

interface ChatMessage {
    receiverId: string
    senderId: string
    messageType: MessageType
    content: string
}

/**
 * 分页查询用户聊天记录
 */
export const getMessages = (userId: string, page: any) => {
    return get({
        url: `gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-room`,
        params: { ...page, userId },
    })
}
/**
 * 发送消息给用户
 * @param userId 用户id
 * @param data 消息体
 */
export const sendMessages = (data: ChatMessage) => {
    return post({
        url: `gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/message`,
        data,
    })
}

/**
 * 分页查询用户列表
 */

export const getMessageUsers = (keywords: string, page: any) => {
    return get({
        url: `gruul-mall-carrier-pigeon/pigeon/platform-chat-room-messages/chat-rooms`,
        params: { ...page, keywords },
    })
}
