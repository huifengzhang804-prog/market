declare module "@/views/order/types/order" {
  /**
   * @param NONE 无售后
   * @param REFUND_REQUEST 申请退款
   * @param SYSTEM_REFUND_AGREE 系统自动同意退款申请
   * @param REFUND_AGREE 已同意退款申请
   * @param REFUND_REJECT 拒绝了退款申请
   * @param REFUNDED 退款已到账
   * @param RETURN_REFUND_REQUEST 申请退货退款
   * @param SYSTEM_RETURN_REFUND_AGREE 系统自动同意退货退款申请
   * @param RETURN_REFUND_AGREE 已同意退货退款申请
   * @param RETURN_REFUND_REJECT 拒绝了退货退款申请
   * @param SYSTEM_RETURNED_REFUND_CONFIRM 退货退款 系统自动确认收货
   * @param RETURNED_REFUND   退货已发出
   * @param SYSTEM_CLOSED 系统自动关闭
   * @param RETURNED_REFUND_CONFIRM 确认退货已收到
   * @param RETURNED_REFUND_REJECT 已拒绝收货
   * @param  RETURNED_REFUNDED 退货退款已完成
   * @param  BUYER_CLOSED  主动撤销了售后申请
   */
  export enum AFSSTATUS {
    NONE,
    REFUND_REQUEST,
    SYSTEM_REFUND_AGREE,
    REFUND_AGREE,
    REFUND_REJECT,
    SYSTEM_RETURN_REFUND_AGREE,
    REFUNDED,
    SYSTEM_RETURNED_REFUND_CONFIRM,
    RETURN_REFUND_REQUEST,
    RETURN_REFUND_AGREE,
    RETURN_REFUND_REJECT,
    RETURNED_REFUND,
    SYSTEM_CLOSED,
    RETURNED_REFUND_CONFIRM,
    RETURNED_REFUND_REJECT,
    RETURNED_REFUNDED,
    BUYER_CLOSED,
  }
  /**
   * 订单接收
   */
  export interface OrderReceiver {
    address: string;
    area: string[];
    id: string;
    mobile: string;
    name: string;
  }
  /**
   * @param: WITHOUT  无需物流发货
   * @param: EXPRESS 普通发货 自己填 物流公司与 单号
   * @param: PRINT_EXPRESS 打印发货
   */
  export enum DeliverType {
    WITHOUT,
    EXPRESS,
    PRINT_EXPRESS,
  }
  export interface ApiLogistics01 {
    createTime: string;
    deleted: false;
    deliveryTime?: string;
    expressCompanyCode: string;
    expressCompanyName: string;
    expressNo: string;
    id: string;
    orderNo: string;
    receiverAddress: string;
    receiverMobile: string;
    receiverName: string;
    remark: string;
    shopId: string;
    status: keyof typeof PACKAGESTATUS;
    type: keyof typeof DeliverType;
    updateTime: string;
    version: 0;
    // 确认收货时间
    confirmTime?: string;
    //  评论时间
    commentTime?: string;
    success: true;
  }
  export type PLATFORM =
    | "WECHAT_MINI_APP"
    | "WECHAT_MP"
    | "PC"
    | "H5"
    | "IOS"
    | "ANDROID"
    | "HARMONY";

  /**
   * 订单状态
   * @param  UNPAID 未支付
   * @param  PAID 已支付
   * @param  BUYER_CLOSED 买家关闭订单
   * @param  SYSTEM_CLOSED 系统关闭订单
   * @param  SELLER_CLOSED 卖家关闭订单
   * @param  TEAMING 拼团中
   * @param  TEAM_FAIL 拼团失败
   */
  export enum ORDERSTATUS {
    UNPAID,
    PAID,
    BUYER_CLOSED,
    SYSTEM_CLOSED,
    SELLER_CLOSED,
    TEAMING,
    TEAM_FAIL,
  }
  /**
   * 订单tab状态
   * @param UNPAID 待支付
   * @param UN_DELIVERY 待发货
   * @param UN_RECEIVE 待收货
   * @param COMPLETED 已完成
   * @param CLOSED 已关闭
   */
  export enum QUERYORDERSTATUS {
    UNPAID,
    UN_DELIVERY,
    UN_RECEIVE,
    COMPLETED,
    CLOSED,
  }
  /**
   * 订单类型
   * @param COMMON 商品订单
   * @param SPIKE 秒杀
   */
  export enum ORDERTYPE {
    COMMON,
    SPIKE,
  }
  export enum ORDERPAYMENT {
    WECHAT,
    ALIPAY,
    BALANCE,
  }
  /**
   * 订单支付状态
   * @param CLOSED 取消支付
   * @param UNPAID 未支付
   * @param PAID 已支付
   */
  export enum ORDERPAYMENTSTATUS {
    CLOSED,
    UNPAID,
    PAID,
  }
  /**
   * 优惠类型
   * @param PLATFORM_COUPON 平台优惠券
   * @param SHOP_COUPON 店铺优惠券
   */
  enum DISCOUNTSOURCETYPE {
    PLATFORM_COUPON,
    SHOP_COUPON,
  }
  /**
   * 优惠状态
   */
  enum DISCOUNTSOURCESTATUS {
    NORMAL,
    CLOSED,
  }
  /**
   * 商铺订单状态
   * @param UNPAID 未支付
   * @param PAID 支付
   * @param SYSTEM_CLOSED 系统关闭
   * @param BUYER_CLOSED  买家关闭订单
   * @param SELLER_CLOSED  卖家关闭订单
   */
  export enum SHOPORDERSTATUS {
    OK,
    SYSTEM_CLOSED,
    BUYER_CLOSED,
    SELLER_CLOSED,
  }
  export enum SHOPITEMSTATUS {
    OK,
    CLOSED,
  }
  /**
   * 包裹状态
   * @param WAITING_FOR_DELIVER 待发货
   * @param WAITING_FOR_RECEIVE 已发货待收货
   * @param BUYER_WAITING_FOR_COMMENT 买家确认收货 待评价
   * @param SYSTEM_WAITING_FOR_COMMENT 系统确认收货 待评价
   * @param BUYER_COMMENTED_COMPLETED 买家已评论 已完成
   * @param SYSTEM_COMMENTED_COMPLETED 系统自动好评 已完成
   */
  export enum PACKAGESTATUS {
    WAITING_FOR_DELIVER,
    WAITING_FOR_RECEIVE,
    BUYER_WAITING_FOR_COMMENT,
    SYSTEM_WAITING_FOR_COMMENT,
    BUYER_COMMENTED_COMPLETED,
    SYSTEM_COMMENTED_COMPLETED,
  }
  /**
   * 配送方式
   */
  export enum DISTRIBUTION {
    MERCHANT,
    EXPRESS, //快递配送
    INTRA_CITY_DISTRIBUTION, //同城配送
    SHOP_STORE, //店铺门店
    VIRTUAL, // 无需物流
  }
  /**
   * 订单类型
   * @param {string} buyerId 买家用户id
   * @param {string} no 订单号
   * @param {ORDERSTATUS} status 订单状态
   * @param {ORDERTYPE} type 订单类型
   * @param {OrderPayment} orderPayment 订单支付相关信息
   * @param {OrderDiscount} orderDiscounts 订单优惠相关
   * @param {ShopOrder} shopOrders 店铺订单相关
   */
  export interface ApiOrder {
    id: string;
    shopId: string;
    buyerId: string;
    buyerNickname: string;
    createTime: string;
    updateTime: string;
    no: string;
    status: keyof typeof ORDERSTATUS;
    type: keyof typeof ORDERTYPE;
    remark: string;
    orderPayment: OrderPayment;
    orderDiscounts: OrderDiscount[];
    shopOrders: ShopOrder[];
    orderReceiver: OrderReceiver;
    shopOrderPackages: ApiLogistics01[];
    checked: boolean;
    platform: PLATFORM;
    extra: {
      distributionMode: keyof typeof DISTRIBUTION;
    };
  }
  /**
   * 订单接收
   */
  export interface OrderReceiver {
    address: string;
    area: string[];
    id: string;
    mobile: string;
    name: string;
  }
  /**
   * 支付相关信息
   * @param payerId 支付用户id
   * @param type 支付类型
   * @param status 支付状态
   * @param totalAmount 订单总金额
   * @param freightAmount 总运费
   * @param discountAmount 优惠总金额
   * @param payTime 支付时间
   * @param payAmount 支付总金额金额 = 订单总金额 - 优惠总金额
   */
  export interface OrderPayment {
    createTime: string;
    shopId: string;
    orderId: string;
    payerId: string;
    type: keyof typeof ORDERPAYMENT;
    status: keyof typeof ORDERPAYMENTSTATUS;
    totalAmount: number;
    freightAmount: number;
    discountAmount: number;
    payAmount: number;
    payTime: string;
  }
  /**
   * 订单优惠
   * @param sourceType 优惠类型
   * @param sourceStatus 优惠状态
   * @param sourceId 优惠源Id
   * @param sourceAmount 优惠金额
   * @param sourceDesc 优惠信息描述
   * @param discountItems 优惠项对应商品
   */
  export interface OrderDiscount {
    shopId: string;
    orderId: string;
    sourceType: keyof typeof DISCOUNTSOURCETYPE;
    sourceStatus: keyof typeof DISCOUNTSOURCESTATUS;
    sourceId: string;
    sourceAmount: number;
    sourceDesc: string;
    discountItems: OrderDiscountItem[];
  }
  /**
   * 优惠对应的商品
   * @param packageId 店铺包裹id
   * @param packageItemId 店铺包裹商品id
   * @param discountId 优惠项id
   */
  export interface OrderDiscountItem {
    itemId: string;
    shopId: string;
    packageId: string;
    packageItemId: string;
    discountId: string;
    discountAmount: string;
  }
  /**
   * 店铺订单相关
   * @param no 店铺订单号
   * @param remark 店铺订单备注
   */
  export interface ShopOrder {
    packageMap?: Map<string | undefined, ShopOrderItem[]>;
    no: string;
    status: keyof typeof SHOPORDERSTATUS;
    shopId: string;
    orderId: string;
    shopName: string;
    shopLogo: string;
    remark: string;
    id: string;
    orderReceiver?: OrderReceiver;
    shopOrderItems: ShopOrderItem[];
  }
  export interface ShopOrderItem {
    afsNo: string;
    afsStatus: keyof typeof AFSSTATUS;
    dealPrice: string;
    freightPrice: string;
    freightTemplateId: string;
    status: keyof typeof SHOPITEMSTATUS;
    id: string;
    image: string;
    num: number;
    packageStatus: keyof typeof PACKAGESTATUS;
    orderId: string;
    productId: string;
    productName: string;
    salePrice: string;
    shopId: string;
    skuId: string;
    specs: string[];
    weight: number;
    packageId?: string;
    sellType: keyof typeof SellTypeEnum;
  }

  export enum SellTypeEnum {
    CONSIGNMENT = "代销商品",
    PURCHASE = "采购商品",
    OWN = "自有商品",
  }

  /**
   * 商铺订单包裹相关
   * @param shopOrderId 店铺订单id
   * @param freightTemplateId 运费模板id
   * @param freightPrice 用户支付的运费
   * @param  status 包裹状态
   * @param  type  配送方式
   * @param  receiverName  收货人名称
   * @param  receiverMobile  收货人电话
   * @param  receiverAreaCode  省市区code
   * @param  receiverAddress  收货人详细地址
   * @param  receiveTime  确认收货时间
   * @param  shopOrderPackageItems  确认收货时间
   */
  /**
   * 店铺订单包裹商品相关
   * @param packageId 配送包裹id
   * @param specs 商品规格
   * @param num 购买数量
   * @param image 该商品sku 图片
   * @param salePrice 销售单价
   * @param dealPrice 成交价(活动价: 如秒杀,等等)
   */
  /**
   * 订单列表的类型
   */
  export interface OrderDataType {
    records: ApiOrder[];
  }
  export interface OrderListSearchData {
    no: string;
    buyerNickname: string;
    productName: string;
    receiverName: string;
    startTime: string;
    endTime: string;
    distributionMode: string;
    platform: keyof typeof PlatformList;
  }
  export enum PlatformList {
    WECHAT_MINI_APP = "小程序",
    WECHAT_MP = "公众号",
    H5 = "H5商城",
    IOS = "IOS端",
    PC = "PC商城",
    ANDROID = "安卓端",
    HARMONY = "鸿蒙端",
  }
  export interface Apipackage {
    confirmTime: string;
    createTime: string;
    deleted: boolean;
    deliveryTime: string;
    expressCompanyCode: string;
    expressCompanyName: string;
    expressNo: string;
    id: string;
    orderNo: string;
    receiverAddress: string;
    receiverMobile: string;
    receiverName: string;
    remark: string;
    shopId: string;
    status: string;
    type: "EXPRESS" | "PRINT_EXPRESS" | "WITHOUT";
    updateTime: string;
    deliverShopName?: string;
  }
  export interface ExtraMap {
    AllDeliveryCount: string;
    miniDeliveryCount: string;
  }
}
