import { BaseMessage } from '@/composables/stomp/typs'
import { Page } from '@/components/BetterPageManage'

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
    async loadMore() {
        if (this.total <= this.current * this.size) {
            return
        }
        this.current = this.current + 1
        const records = await this.loadData()
        this.records = this.records.concat(records)
    }
    private async loadData() {
        const { code, data } = await this.load({ current: this.current, size: this.size })
        const { total, records } = data
        this.total = total || 0
        return records
    }
    async initLoad() {
        this.current = 1
        const records = await this.loadData()
        this.records = records
        return this.records
    }
    concatData(res: T) {
        this.records.unshift(res)
    }
}

/**
 * 用户类型
 */
export enum UserType {
    //消费者
    CONSUMER = 'CONSUMER',
    //店铺管理员
    SHOP_ADMIN = 'SHOP_ADMIN',
    //平台管理员
    PLATFORM_ADMIN = 'PLATFORM_ADMIN',
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

export type MessageTypeKeys = keyof typeof MessageType

export interface MessageUser {
    chatWithUserInfo: IChatWithUserInfo
    lastMessage: ILastMessage
}

export interface ILastMessage {
    handled: false
    message: string
    messageType: MessageTypeKeys
    read: boolean
    receiver: IReceiver
    sendTime: string
    sender: ISender
    show: boolean
}
export interface IReceiver {
    receiverShopInfo: { shopId: string; shopName: string }
    receiverType: string
}
export interface ISender {
    senderType: string
    senderUserInfo?: IChatWithUserInfo
    senderShopInfo?: {
        shopId: string
        shopName: string
    }
}

export interface IChatWithUserInfo {
    avatar: string
    nickname: string
    userId: string
    userKey: string
    // 是否专属会员
    includeRights: boolean
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
    receiverId: string
    senderId: Long
    messageType: MessageType
    content: string
}

/**
 * 聊天消息
 */
export type Message = ILastMessage

/**
 * 店铺管理员与消息列表
 */
export type MessageAndShopAdmin = ILastMessage

/**
 * 客服消息
 */
export interface CustomerServiceMessage extends BaseMessage {
    /**
     * 消息id
     */
    messageId: string

    /**
     * 发送方类型
     */
    senderType: UserType

    /**
     * 消息发送方id
     */
    senderId: string

    /**
     * 接收方类型
     */
    receiverType: UserType

    /**
     * 接收方id
     */
    receiverId: string

    /**
     * 消息内容类型
     */
    messageType: MessageType

    /**
     * 消息内容
     */
    message: string

    shopId: string
}
// 选择商品时检索出的商品类型
export type ApiGoodItemType = {
    categoryFirstId: string
    categorySecondId: string
    categoryThirdId: string
    createTime: string
    hotScore: string
    id: string
    isCheck?: boolean
    productName: string
    pic: string
    platformCategoryFirstId: string
    platformCategorySecondId: string
    platformCategoryThirdId: string
    salePrices: string[]
    salesVolume: string
    shopId: string
    shopName: string
    stockTypes: string[]
    stocks: number[]
}

interface Extra {
    packUpTime: string
    shopStoreId: string
    distributionMode: string
    teamNo?: string
}
