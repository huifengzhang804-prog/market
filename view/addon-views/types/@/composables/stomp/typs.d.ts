declare module "@/composables/stomp/typs" {
  export enum Channel {
    /**
     * 新订单
     */
    NEW_ORDER = "NEW_ORDER",
    /**
     *系统公告
     */
    NOTICE = "NOTICE",
    /**
     * 客服服务消息
     */
    CUSTOMER_SERVICE = "CUSTOMER_SERVICE",
    /* 
    供应商消息
    */
    SUPPLIER_SHOP = "SUPPLIER_SHOP",
    /**
     * 客服服务消息
     */
    PLATFORM_SHOP_AND_USER = "PLATFORM_SHOP_AND_USER",
  }
  export interface StompHook {
    success: (response: any) => void;
    fail: (response: any) => void;
    subscribe: (message: BaseMessage) => void;
  }
  export interface BaseMessage {
    /**
     * 消息频道
     */
    channel: Channel;
  }
}
