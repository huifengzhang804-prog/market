declare module "@/views/message/components/types" {
  // 分页参数
  export interface Page {
    size: number;
    current: number;
  }
  export class IPage<T> {
    constructor(load: (page: Page) => Promise<any>, size?: number);
    public load: (page: Page) => Promise<any>;
    public loadMore(): void;
    public initLoad(): Promise<T[]>;
    public concatData(res: T): void;
    records: Array<T>;
  }

  /**
   * 用户类型
   */
  export enum UserType {
    //消费者
    CONSUMER = "CONSUMER",
    //店铺管理员
    SHOP_ADMIN = "SHOP_ADMIN",
    //平台管理员
    PLATFORM_ADMIN = "PLATFORM_ADMIN",
    // 供应商管理员
    SUPPLIER_ADMIN = "SUPPLIER_ADMIN",
  }

  /**
   * 消息类型
   */
  export enum MessageType {
    //文本
    TEXT = "TEXT",
    //图片
    IMAGE = "IMAGE",
    //商品
    PRODUCT = "PRODUCT",
    // 订单
    ORDER = "ORDER",
  }

  export interface ChatMessage {
    messageType: MessageType;
    message: string;
  }

  export interface Message {
    messageType: MessageType;
    message: string;
    read: boolean;
    handled: boolean;
    show: boolean;
    receiver: {
      receiverType: string;
      receiverShopInfo: shopInfo;
    };
    sendTime: string;
    sender: {
      senderShopInfo: shopInfo;
      senderType: string;
      senderUserInfo: {
        avatar: string;
        nickname: string;
        userId: string;
        userKey: string;
      };
    };
  }
  /**
   * 聊天消息
   */
  interface shopInfo {
    shopId: string;
    shopLogo: string;
    shopName: string;
  }
  export interface MessageUser {
    chatWithShopInfo: {
      shopId: string;
      shopLogo: string;
      shopName: string;
    };
    lastMessage: Message;
  }
  // 接收消息
  export interface ReceiverMessage {
    senderShopId: string;
  }
  // 发送消息
  export interface SendMessage {
    messageType: MessageType;
    // senderUserId: string
    // senderShopId: string
    receiverShopId: string;
    content: string;
  }
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
  export interface BaseMessage {
    /**
     * 消息频道
     */
    channel: Channel;
  }
  /**
   * 客服消息
   */
  export interface CustomerServiceMessage extends BaseMessage {
    /**
     * 消息发送方shopid
     */
    sender: {
      senderShopInfo: shopInfo;
      senderType: string;
      senderUserInfo: {
        avatar: string;
        nickname: string;
        userId: string;
        userKey: string;
      };
    };

    /**
     * 接收方类型
     */
    receiver: {
      receiverType: string;
      receiverShopInfo: shopInfo;
    };

    /**
     * 消息内容类型
     */
    messageType: MessageType;

    /**
     * 消息内容
     */
    message: string;
  }
}
