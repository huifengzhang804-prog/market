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
  userId: string
  token: string
}

export interface StompHook {
  success: (response: any) => any
  fail: (response: any) => any
  subscribe: (response: BaseMessage) => any
}

export enum Channel {
  /**
   *系统公告
   */
  NOTICE = 'NOTICE',
  /**
   * 客服服务消息
   */
  CUSTOMER_SERVICE = 'CUSTOMER_SERVICE',

  /**
   * 客服服务消息
   */
  PLATFORM_SHOP_AND_USER = 'PLATFORM_SHOP_AND_USER',
}

/**
 * 用户类型
 */
enum UserType {
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
  //未及时处理的消息
  UN_HANDLE = 'UN_HANDLE',
  // 订单信息
  ORDER = 'ORDER',
}

export interface BaseMessage {
  /**
   * 消息频道
   */
  channel: Channel

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

  /**
   * 商铺id
   */
  shopId: Long
}

export enum ConnectType {
  SUCCESS,
  FAIL,
  SUBSCRIBE,
}

export interface SystemMessage {
  connectType: ConnectType
  msg: any
}
