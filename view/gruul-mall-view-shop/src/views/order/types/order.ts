import type { ShopOrderItem, Order as ApiOrder, OrderReceiver } from '@/apis/order/types'
import Decimal from 'decimal.js'
export { ShopOrderItem, ApiOrder, OrderReceiver }

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
 * @param FULL_REDUCTION 店铺满减
 */
export enum DISCOUNTSOURCETYPE {
    PLATFORM_COUPON,
    SHOP_COUPON,
    MEMBER_DEDUCTION,
    FULL_REDUCTION,
    CONSUMPTION_REBATE,
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
    OK, //正常 其他都是已关闭
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
    createTime: string
    shopId: string
    orderId: string
    payerId: string
    type: keyof typeof ORDERPAYMENT
    status: keyof typeof ORDERPAYMENTSTATUS
    totalAmount: number
    freightAmount: number
    discountAmount: number
    payAmount: number
    payTime: string
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
    id: string
    shopId: string
    orderId: string
    sourceType: keyof typeof DISCOUNTSOURCETYPE
    sourceStatus: keyof typeof DISCOUNTSOURCESTATUS
    sourceId: string
    sourceAmount: number
    sourceDesc: string
    discountItems: OrderDiscountItem[]
}
/**
 * 优惠对应的商品
 * @param packageId 店铺包裹id
 * @param packageItemId 店铺包裹商品id
 * @param discountId 优惠项id
 */
export interface OrderDiscountItem {
    itemId: string
    shopId: string
    packageId: string
    packageItemId: string
    discountId: string
    discountAmount: string
}
/**
 * 店铺订单相关
 * @param no 店铺订单号
 * @param remark 店铺订单备注
 */
export interface ShopOrder {
    packageMap?: Map<string | undefined, ShopOrderItem[]>
    no: string
    status: keyof typeof SHOPORDERSTATUS
    shopId: string
    orderId: string
    shopName: string
    shopLogo: string
    remark: string
    id: string
    orderReceiver?: OrderReceiver
    shopOrderItems: ShopOrderItem[]
}

export enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}

/**
 * 订单列表的类型
 */
export interface OrderDataType {
    records: ApiOrder[]
}
export interface OrderListSearchData {
    no: string
    buyerNickname: string
    productName: string
    receiverName: string
    startTime: string
    endTime: string
    distributionMode: string
    platform: keyof typeof PlatformList
}
export enum PlatformList {
    WECHAT_MINI_APP = '小程序',
    WECHAT_MP = '公众号',
    H5 = 'H5商城',
    IOS = 'IOS端',
    PC = 'PC商城',
    ANDROID = '安卓端',
    HARMONY = '鸿蒙端',
}
export interface Apipackage {
    confirmTime: string
    createTime: string
    deleted: boolean
    deliveryTime: string
    expressCompanyCode: string
    expressCompanyName: string
    expressNo: string
    id: string
    orderNo: string
    receiverAddress: string
    receiverMobile: string
    receiverName: string
    remark: string
    shopId: string
    status: string
    type: 'EXPRESS' | 'PRINT_EXPRESS' | 'WITHOUT'
    updateTime: string
    deliverShopName?: string
}
export interface ExtraMap {
    AllDeliveryCount: string
    miniDeliveryCount: string
}

export interface DiscountTypeItem {
    //是否是店铺折扣
    isShopDiscount: boolean
    //折扣描述
    name: string
    //用于控制订单详情折扣展示排序
    sort: number
}
export interface OrderDiscountDetail extends DiscountTypeItem {
    //折扣价
    price: Decimal
}

/**
 * 默认的折扣配置
 */
export const discountTypeConf: Record<keyof typeof DISCOUNTSOURCETYPE, DiscountTypeItem> = {
    PLATFORM_COUPON: {
        isShopDiscount: false,
        name: '平台优惠',
        sort: 1,
    },
    SHOP_COUPON: {
        isShopDiscount: true,
        name: '店铺优惠券',
        sort: 2,
    },
    MEMBER_DEDUCTION: {
        isShopDiscount: false,
        name: '会员折扣',
        sort: 3,
    },
    FULL_REDUCTION: {
        isShopDiscount: true,
        name: '满减',
        sort: 4,
    },
    CONSUMPTION_REBATE: {
        isShopDiscount: false,
        name: '消费返利',
        sort: 5,
    },
}
