/**
 * 消息内容类型
 */
export type MsgType = 'NONE' | 'TEXT' | 'RICH_TEXT' | 'PAGE' | 'LINK'
/**
 * 消息渠道
 */
export type Channel = 'EXCHANGE' | 'ACCOUNT' | 'SERVICE' | 'NEWS' | 'INTERACTION' | 'ACTIVITY' | 'NOTICE' | 'OTHER'
/**
 * 发送类型
 */
export type SendType = 'BROADCAST_PLATFORM' | 'BROADCAST_SHOP' | 'MARKED_PLATFORM' | 'MARKED_SHOP' | 'MARKED_USER'
/**
 * 消息类型
 */
export type NoticeType =
    | 'ANNOUNCEMENT'
    | 'CASH_PASS'
    | 'CASH_FAIL'
    | 'PRODUCT_PASS'
    | 'PRODUCT_FAIL'
    | 'COUPON_FORBIDDEN'
    | 'NEW_ORDER'
    | 'NOTIFICATION'
    | 'OTHER'
/*
 * 接收到的消息类型
 */
interface StompMessage {
    messageId?: string
    noticeType: NoticeType
    sendType: SendType
    channel: Channel
    msgType: MsgType
    title: string
    summary?: string
    url?: string
    content?: string
    createBy: string
}
