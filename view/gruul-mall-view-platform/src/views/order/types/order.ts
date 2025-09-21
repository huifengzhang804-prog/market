import Decimal from 'decimal.js'
import type { Order as ApiOrder, OrderReceiver, Remark, ShopOrderItem } from '@/apis/order/types'

export { ShopOrderItem, ApiOrder, OrderReceiver }

/**
 * 订单状态
 * @param  UNPAID 未支付
 * @param  PAID 已支付
 * @param  BUYER_CLOSED 买家关闭订单
 * @param  SYSTEM_CLOSED 系统关闭订单
 * @param  SELLER_CLOSED 卖家关闭订单
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
 * @param FULL_REDUCTION 满减
 */
enum DISCOUNTSOURCETYPE {
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
    OK,
    SYSTEM_CLOSED,
    BUYER_CLOSED,
    SELLER_CLOSED,
}

/**
 * 包裹状态
 * @param WAITING_FOR_DELIVER 待发货
 * @param WAITING_FOR_RECEIVE 已发货待收货
 * @param BUYER_WAITING_FOR_COMMENT 买家确认收货 待评价
 * @param SYSTEM_WAITING_FOR_COMMENT 系统确认收货 待评价
 * @param BUYER_COMMENTED_COMPLETED 买家已评论 已完成
 * @param SYSTEM_COMMENTED_COMPLETED 系统自动好评 已完成
 * @param BUYER_REQUEST_REFUND 买家申请退款 仅退款
 * @param  REFUNDED  卖家同意退款
 * @param  BUYER_REQUEST_RETURNS_REFUND   买家申请退货退款
 */
export enum PACKAGESTATUS {
    WAITING_FOR_DELIVER = 'WAITING_FOR_DELIVER',
    WAITING_FOR_RECEIVE = 'WAITING_FOR_RECEIVE',
    BUYER_WAITING_FOR_COMMENT = 'BUYER_WAITING_FOR_COMMENT',
    SYSTEM_WAITING_FOR_COMMENT = 'SYSTEM_WAITING_FOR_COMMENT',
    BUYER_COMMENTED_COMPLETED = 'BUYER_COMMENTED_COMPLETED',
    SYSTEM_COMMENTED_COMPLETED = 'SYSTEM_COMMENTED_COMPLETED',
    BUYER_REQUEST_REFUND = 'BUYER_REQUEST_REFUND',
    REFUNDED = 'REFUNDED',
    BUYER_REQUEST_RETURNS_REFUND = 'BUYER_REQUEST_RETURNS_REFUND',
}

/**
 * 包裹配置方式
 * @param EXPRESS 快递
 */
enum PACKAGETYPE {
    EXPRESS,
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

/**
 *
 */
export enum SHOPITEMSTATUS {
    OK,
    CLOSED,
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
    updateTime: string
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

export enum SHOP_TYPE_ENUM {
    SELF_OWNED = '自营',
    PREFERRED = '优选',
    ORDINARY = '普通',
}

/**
 * 店铺订单相关
 * @param no 店铺订单号
 * @param remark 店铺订单备注
 */
export interface ShopOrder {
    packageMap: Map<string | undefined, ShopOrderItem[]>
    id: string
    no: string
    status: keyof typeof SHOPORDERSTATUS
    shopId: string
    orderId: string
    shopName: string
    shopLogo: string
    remark: Remark
    shopOrderItems: ShopOrderItem[]
    orderReceiver?: OrderReceiver
    extra: {
        deliverTime?: string
        receiveTime?: string
    }
    shopType?: keyof typeof SHOP_TYPE_ENUM
    // shopOrderPackages: ShopOrderPackage[]
}

/**
 * 商铺订单包裹相关
 * @param no 店铺订单包裹id
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
    type: string
    updateTime: string
    deliverShopName?: string
}

export interface ApiLogistics01 {
    createTime: string
    deleted: false
    deliveryTime?: string
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
    status: PACKAGESTATUS
    type: keyof typeof DeliverType
    updateTime: string
    version: 0
    // 确认收货时间
    confirmTime?: string
    //  评论时间
    commentTime?: string
    success: true
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
