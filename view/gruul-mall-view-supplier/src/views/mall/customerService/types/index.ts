import { BaseMessage } from '@/composables/stomp/typs'
import { Page } from '@/components/pageManage'

export class IPage<T> {
    current = 1
    size = 30
    total = 0
    records: Array<T> = []
    load: (page: Page) => Promise<any>
    constructor(load: (page: Page) => Promise<any>, size = 30) {
        this.load = load
        this.size = size
    }
    loadMore() {
        if (this.total <= this.current * this.size) {
            return
        }
        this.current = this.current + 1
        this.load({ current: this.current, size: this.size }).then(({ code, data }) => {
            if (!data) return
            const { total, records } = data
            this.total = total
            this.records = this.records.concat(records)
        })
    }
    async initLoad() {
        this.current = 1
        this.load({ current: this.current, size: this.size })
            .then(({ code, data }) => {
                if (!data) return
                const { total, records } = data
                this.total = total
                this.records = records
            })
            .catch((e) => {
                console.log('e')
            })
        return this.records
    }
    concatData(res: T) {
        this.records = [res, ...this.records]
    }
}

/**
 * 消息类型
 */
export enum MessageType {
    //文本
    TEXT = 'TEXT',
    //图片
    IMAGE = 'IMAGE',
    //商品
    PRODUCT = 'PRODUCT',
    // 订单
    ORDER = 'ORDER',
}

export interface MessageUser {
    chatWithShopInfo: {
        shopId: string
        shopLogo: string
        shopName: string
    }
    lastMessage: Message
}

/**
 * 工具栏内容类型枚举
 */
export enum ToolbarMessageType {
    //表情
    EXPRESSION,
    //图片
    IMAGE,
}

/**
 * 工具栏内容
 */
export interface ToolbarMessage {
    type: ToolbarMessageType
    content: string
}

export interface ChatMessage {
    messageType: MessageType
    message: string
}

export interface SendMessage {
    messageType: MessageType
    receiverShopId: string
    content: string
}

// 接收消息
export interface ReceiverMessage {
    senderShopId: string
}
/**
 * 聊天消息
 */
interface shopInfo {
    shopId: string
    shopLogo: string
    shopName: string
}

export interface Message {
    messageType: MessageType
    message: string
    read: boolean
    handled: boolean
    show: boolean
    receiver: {
        receiverType: string
        receiverShopInfo: shopInfo
    }
    sendTime: string
    sender: {
        senderShopInfo: shopInfo
        senderType: string
        senderUserInfo: {
            avatar: string
            nickname: string
            userId: string
            userKey: string
        }
    }
}

/**
 * 店铺管理员与消息列表
 */
export type MessageAndShopAdmin = Message

/**
 * 客服消息
 */
export interface CustomerServiceMessage extends BaseMessage {
    /**
     * 消息发送方shopid
     */
    sender: {
        senderShopInfo: shopInfo
        senderType: string
        senderUserInfo: {
            avatar: string
            nickname: string
            userId: string
            userKey: string
        }
    }

    /**
     * 接收方类型
     */
    receiver: {
        receiverType: string
        receiverShopInfo: shopInfo
    }

    /**
     * 消息内容类型
     */
    messageType: MessageType

    /**
     * 消息内容
     */
    message: string
}
