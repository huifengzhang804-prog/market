import { BaseMessage } from '@/libs/stomp/typs'
interface Page {
    size: number
    current: number
}

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
            const { total, records } = data
            this.total = total
            this.records = this.records.concat(records)
        })
    }
    async initLoad() {
        this.current = 1
        await this.load({ current: this.current, size: this.size }).then(({ code, data }) => {
            const { total, records } = data
            this.total = total
            this.records = records
        })
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
    messageType: string
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
    senderUserInfo: IChatWithUserInfo
    senderShopInfo: {
        shopId: string
        shopName: string
    }
}
export interface IChatWithUserInfo {
    avatar: string
    nickname: string
    userId: string
    userKey: string
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
    messageType: MessageType
    content: string
}

/**
 * 店铺管理员与消息列表
 */

export interface MessageAndShopAdmin {
    handled: boolean
    message: string
    messageType: 'IMAGE' | 'PRODUCT' | 'ORDER' | 'TEXT'
    read: boolean
    receiver: IReceiver
    sendTime: string
    sender: ISender
    show: boolean
}

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
interface ExtraPayment {
    rebatePay: boolean
}
interface OrderPayment {
    createTime?: string
    discountAmount?: string
    extra?: ExtraPayment
    freightAmount?: string
    id?: string
    payAmount?: string
    payTime?: string
    payerId?: string
    totalAmount?: string
    type?: string
    updateTime?: string
}

interface OrderReceiver {
    address: string
    area: string[]
    id: string
    mobile: string
    name: string
}

interface ShopOrderItem {
    afsStatus: string
    createTime: string
    dealPrice: string
    fixPrice: string
    freightPrice: string
    freightTemplateId: string
    id: string
    image: string
    num: number
    packageStatus: string
    productId: string
    productName: string
    salePrice: string
    sellType: string
    shopId: string
    skuId: string
    specs: string[]
    status: string
    weight: number
    afsNo?: string
    supplierId?: string
    supplierShopType?: string
    deliverType?: string
    packageId?: string
}

interface ShopOrder {
    createTime: string
    id: string
    no: string
    orderNo: string
    remark: string
    shopId: string
    shopLogo: string
    shopMode: string
    shopName: string
    shopOrderItems: ShopOrderItem[]
    shopType: string
    status: string
    totalAmount?: number
}

interface Timeout {
    afsAuditTimeout: string
    commentTimeout: string
    confirmTimeout: string
    payTimeout: string
}

export interface OrderListType {
    buyerId: string
    buyerNickname: string
    createTime: string
    extra: Extra
    id: string
    isPriority: boolean
    no: string
    orderPayment: OrderPayment
    orderReceiver: OrderReceiver
    platform: string
    shopOrders: ShopOrder[]
    status: string
    timeout: Timeout
    type: string
    updateTime: string
}
