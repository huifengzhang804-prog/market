import type { ProductPrice } from '@/apis/good/model'
import type { OrderStatusPlus } from '@/hooks'
import { MessageType, type BaseMessage } from '@/hooks/stomp/typs'

export interface ApiMessageShop {
  chatWithUserInfo: IChatWithUserInfo
  chatWithShopInfo: {
    shopId: Long
    shopLogo: string
    shopName: string
  }
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
interface IReceiver {
  receiverShopInfo: { shopId: Long; shopName: string }
  receiverType: string
}
interface ISender {
  senderType: string
  senderUserInfo?: IChatWithUserInfo
  senderShopInfo?: { shopId: Long; shopLogo: string; shopName: string }
}
interface IChatWithUserInfo {
  avatar: string
  nickname: string
  userId: string
  userKey: string
}

export interface ChatMessage {
  messageType: keyof typeof MessageType
  content: string
  receiverId: string
  senderId: string
}
export interface ShopChatMessage {
  messageType: keyof typeof MessageType
  message: string
}
/**
 * 聊天消息
 */
export interface Message {
  handled: boolean
  message: string
  messageType: 'IMAGE' | 'PRODUCT' | 'ORDER' | 'TEXT'
  read: boolean
  receiver: IReceiver
  sendTime: string
  sender: ISender
  show: boolean
}

export type ShopData = { shopId: Long; shopLogo: string; shopName: string }
export interface CurrentProduct {
  pic: string
  id: string
  name: string
  price: ProductPrice
  no?: string
  amountRealPay?: number
  shopId?: string
  h5: string
  afsStatu: OrderStatusPlus
}
