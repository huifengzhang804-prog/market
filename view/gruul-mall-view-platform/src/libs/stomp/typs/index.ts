export enum ConnectStatus {
    SUCCESS = 'success',
    FAIL = 'fail',
    SUBSCRIBE = 'subscribe',
}

/**
 * stomp 配置
 */
export interface StompConfig {
    shopId: Long
    userId: Long
    token: string
}

export interface StompHook {
    success: (response: any) => void
    fail: (response: any) => void
    subscribe: (message: BaseMessage) => void
}

export enum Channel {
    /**
     * 新订单
     */
    NEW_ORDER = 'NEW_ORDER',
    /**
     *系统公告
     */
    NOTICE = 'NOTICE',
    /**
     * 客服服务消息
     */
    CUSTOMER_SERVICE = 'CUSTOMER_SERVICE',
    /**
     * 客服服务消息改
     */
    PLATFORM_SHOP_AND_USER = 'PLATFORM_SHOP_AND_USER',
}

export interface BaseMessage {
    /**
     * 消息频道
     */
    channel: Channel
}
