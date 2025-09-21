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
 * 供应商订单
 */
interface Address {
    address: string
    area: string[]
}

export interface OrderItem {
    dealPrice: number
    freightPrice: number
    id: string
    image: string
    num: number
    orderNo: string
    packageStatus: string
    productId: string
    productName: string
    salePrice: number
    specs: string[]
    supplierId: string
}

interface Extra {
    pay: {
        orderNo: string
        payType: string
        proof: string
    }
    payTimeout: number
    receiver: {
        address: string
        area: string[]
        mobile: string
        name: string
    }
    remark: string
}

interface ExtraInfo {
    supplierName: string
    supplierPhone: string
}

interface Product {
    [key: string]: any
}

interface TimeNodes {
    deliveryTime: string
    payTime: string
    receiveTime: string
}

export interface OrderListType {
    createTime: string
    extra: Extra
    extraInfo: ExtraInfo
    hasProductStorage: boolean
    mainNo: string
    no: string
    orderItems: OrderItem[]
    payAmount: number
    products: Product
    shopContractNumber: string
    shopId: string
    shopName: string
    shopUserId: string
    status: string
    supplierId: string
    timeNodes: TimeNodes
    type: string
}

export interface OrderMessage {
    current: number
    export: boolean
    needSupplier: boolean
    no: string
    pages: number
    records: OrderListType[]
    shopId: string
    size: number
    total: number
}
