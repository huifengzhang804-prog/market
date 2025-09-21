import { AFSSTATUS } from '@/views/afs/types'
import { ApiLogistics01 } from '@/views/afs/types/api'
import type { PLATFORM } from '../types'
/**
 * @description: 订单状态
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

export enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}
/**
 * @description: 订单tab状态
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
 * @description: 订单类型
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
 * @description: 订单支付状态
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
 * @description: 优惠类型
 * @param PLATFORM_COUPON 平台优惠券
 * @param SHOP_COUPON 店铺优惠券
 */
enum DISCOUNTSOURCETYPE {
    PLATFORM_COUPON,
    SHOP_COUPON,
}
/**
 * @description: 优惠状态
 */
enum DISCOUNTSOURCESTATUS {
    NORMAL,
    CLOSED,
}
/**
 * @description: 商铺订单状态
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
 * @description:
 * @returns {*}
 */
export enum SHOPITEMSTATUS {
    OK,
    CLOSED,
}
/**
 * @description: 包裹状态
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
 * @description: 配送方式
 */
export enum DISTRIBUTION {
    MERCHANT,
    EXPRESS, //快递配送
    INTRA_CITY_DISTRIBUTION, //同城配送
    SHOP_STORE, //店铺门店
    VIRTUAL, // 无需物流
}

/**
 * 订单平台
 */
export const enum Platform {
    WECHAT_MINI_APP = 'WECHAT_MINI_APP',
    WECHAT_MP = 'WECHAT_MP',
    PC = 'PC',
    H5 = 'H5',
    IOS = 'IOS',
    ANDROID = 'ANDROID',
    HARMONY = 'HARMONY',
}

/**
 * 商家自配 MERCHANT
 * 快递配送 EXPRESS
 * 同城配送 INTRA_CITY_DISTRIBUTION
 * 店铺门店 SHOP_STORE
 * 虚拟配送 VIRTUAL
 */
export const enum DistributionMode {
    MERCHANT = 'MERCHANT',
    EXPRESS = 'EXPRESS',
    INTRA_CITY_DISTRIBUTION = 'INTRA_CITY_DISTRIBUTION',
    SHOP_STORE = 'SHOP_STORE',
    VIRTUAL = 'VIRTUAL',
}

/**
 * ShopOrderPackage
 */
export interface ShopOrderPackage {
    /**
     * 评论时间
     */
    commentTime?: string
    /**
     * 确认收货时间
     */
    confirmTime?: string
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * 发货方店铺、供应商id
     */
    deliverShopId?: number
    /**
     * 发货方
     */
    deliverShopName?: string
    /**
     * 发货时间
     */
    deliveryTime?: string
    /**
     * 快递公司代码
     */
    expressCompanyCode?: string
    /**
     * 快递公司名称
     */
    expressCompanyName?: string
    /**
     * 快递单号
     */
    expressNo?: string
    /**
     * id
     */
    id?: number
    /**
     * 订单号
     */
    orderNo?: string
    /**
     * 收货人详细地址
     */
    receiverAddress?: string
    /**
     * 收货人电话
     */
    receiverMobile?: string
    /**
     * 收货人名称
     */
    receiverName?: string
    /**
     * 发货备注
     */
    remark?: string
    /**
     * 自营店铺类型
     */
    selfShopType?: keyof typeof SelfShopType
    /**
     * 店铺id
     */
    shopId?: number
    /**
     * 包裹状态
     */
    status?: keyof typeof PackageStatus
    /**
     * 配送方式
     */
    type?: keyof typeof DeliverType
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 乐观锁版本号
     */
    version?: number
}

/**
 * 配送方式
 * @param: WITHOUT  无需物流发货
 * @param: EXPRESS 普通发货 自己填 物流公司与 单号
 * @param: PRINT_EXPRESS 打印发货
 */
export enum DeliverType {
    WITHOUT = 'WITHOUT',
    EXPRESS = 'EXPRESS',
    PRINT_EXPRESS = 'PRINT_EXPRESS',
    IC_MERCHANT = 'IC_MERCHANT', // 商家自提
    IC_OPEN = 'IC_OPEN', // 第三方平台配送
}

/**
 * 包裹状态
 */
export const enum PackageStatus {
    WAITING_FOR_DELIVER = 'WAITING_FOR_DELIVER',
    WAITING_FOR_RECEIVE = 'WAITING_FOR_RECEIVE',
    BUYER_WAITING_FOR_COMMENT = 'BUYER_WAITING_FOR_COMMENT',
    SYSTEM_WAITING_FOR_COMMENT = 'SYSTEM_WAITING_FOR_COMMENT',
    BUYER_COMMENTED_COMPLETED = 'BUYER_COMMENTED_COMPLETED',
    SYSTEM_COMMENTED_COMPLETED = 'SYSTEM_COMMENTED_COMPLETED',
}

/**
 * 自营店铺类型
 */
export const enum SelfShopType {
    SELF_SHOP = 'SELF_SHOP',
    SELF_SUPPLIER = 'SELF_SUPPLIER',
}

/**
 * 订单类型
 */
export const enum OrderType {
    COMMON = 'COMMON',
    SPIKE = 'SPIKE',
    TEAM = 'TEAM',
    BARGAIN = 'BARGAIN',
    PACKAGE = 'PACKAGE',
}

/**
 * Order
 * 关联订单信息
 */
export interface ApiOrder {
    /**
     * 活动id
     */
    activityId?: number
    /**
     * 业务处理字段
     */
    activityResp?: ActivityResp
    /**
     * 买家头像
     */
    buyerAvatar?: string
    /**
     * 买家用户id
     */
    buyerId: string
    /**
     * 买家昵称
     */
    buyerNickname?: string
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    distributionMode: keyof typeof DistributionMode
    /**
     * 附加数据
     */
    extra: {
        distributionMode: keyof typeof DistributionMode
        packUpTime: string
        shopStoreId: string
    }
    /**
     * id
     */
    id?: number
    /**
     * 是否拥有优先权(发货)
     */
    isPriority?: boolean
    /**
     * 订单号
     */
    no: string

    orderDiscounts?: OrderDiscount[]

    orderPayment?: OrderPayment

    orderReceiver: OrderReceiver
    /**
     * 订单平台
     */
    platform: keyof typeof Platform
    /**
     * 订单备注
     */
    remark?: string

    shopOrderPackages?: ShopOrderPackage[]

    shopOrders: ShopOrder[]
    /**
     * 订单来源
     */
    source?: keyof typeof Source
    /**
     * 订单状态
     */
    status?: keyof typeof OrderStatus
    /**
     * 关键节点超时时间 key_node_timeout
     */
    timeout?: OrderTimeout
    /**
     * 订单类型
     */
    type?: keyof typeof OrderType
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 乐观锁版本号
     */
    version?: number
    checked?: boolean
}

/**
 * 订单状态
 *  UNPAID: '未支付',
 PAID: '已支付',
 BUYER_CLOSED: '买家关闭订单',
 SYSTEM_CLOSED: '超时未支付 系统关闭',
 SELLER_CLOSED: '卖家关闭订单',
 TEAMING: '拼团中',
 TEAM_FAIL: '拼团失败',
 */
export const enum OrderStatus {
    UNPAID = 'UNPAID',
    PAID = 'PAID',
    BUYER_CLOSED = 'BUYER_CLOSED',
    SYSTEM_CLOSED = 'SYSTEM_CLOSED',
    SELLER_CLOSED = 'SELLER_CLOSED',
    TEAMING = 'TEAMING',
    TEAM_FAIL = 'TEAM_FAIL',
}

/**
 * 关键节点超时时间 key_node_timeout
 */
export interface OrderTimeout {
    /**
     * 售后审核超时时间 单位秒
     * 最少1天 最长30天
     */
    afsAuditTimeout?: number
    /**
     * 评价超时时间 单位秒
     * 最少3天 最长30天
     */
    commentTimeout?: number
    /**
     * 确认收获超时时间 单位 秒
     * 最少3天 最长30天
     */
    confirmTimeout?: number
    /**
     * 支付超时时间 单位/秒
     * 最少3分钟 最长30天
     */
    payTimeout?: number
}

/**
 * 订单来源
 */
export const enum Source {
    PRODUCT = 'PRODUCT',
    CART = 'CART',
}

/**
 * 业务处理字段
 * ActivityResp
 */
export interface ActivityResp {
    /**
     * 额外信息
     */
    extra?: JSONObject
    /**
     * sku对应的价格 map
     */
    skuKeyPriceMap?: Obj
    /**
     * 是否可用优惠
     */
    stackable?: StackableDiscount
}

/**
 * 额外信息
 * JSONObject
 * 附加数据
 */
export interface JSONObject {
    /**
     * 配置项
     */
    config?: JSONConfig
    /**
     * No comments found.(map data)
     */
    raw?: Obj
}

/**
 * 配置项
 * JSONConfig
 */
export interface JSONConfig {
    /**
     * 是否检查重复key
     */
    checkDuplicate?: boolean
    /**
     * 日期格式，null表示默认的时间戳
     */
    dateFormat?: string
    /**
     * 是否忽略键的大小写
     */
    ignoreCase?: boolean
    /**
     * 是否忽略转换过程中的异常
     */
    ignoreError?: boolean
    /**
     * 是否忽略null值
     */
    ignoreNullValue?: boolean
    /**
     * 键排序规则，{@code null}表示不排序，不排序情况下，按照加入顺序排序
     */
    keyComparator?: Obj
    /**
     * 是否去除末尾多余0，例如如果为true,5.0返回5
     */
    stripTrailingZeros?: boolean
    /**
     * 是否支持transient关键字修饰和@Transient注解，如果支持，被修饰的字段或方法对应的字段将被忽略。
     */
    transientSupport?: boolean
}

/**
 * 是否可用优惠
 * StackableDiscount
 */
export interface StackableDiscount {
    /**
     * 是否使用优惠券 默认为true
     */
    coupon?: boolean
    /**
     * 是否能使用满减 默认为true
     */
    full?: boolean
    /**
     * 支付超时时间 为空使用订单默认的超时时间
     */
    payTimeout?: Duration
    /**
     * 是否能使用会员价 默认为true
     */
    vip?: boolean
}

/**
 * 支付超时时间 为空使用订单默认的超时时间
 * Duration
 */
export interface Duration {
    nanos?: number

    seconds?: number

    units?: any
}

/**
 * @description: 订单接收
 * @returns {*}
 */
export interface OrderReceiver {
    address: string
    area: [string, string, string?]
    id: string
    mobile: string
    name: string
}
/**
 * @description: 支付相关信息
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
 * @description: 订单优惠
 * @param sourceType 优惠类型
 * @param sourceStatus 优惠状态
 * @param sourceId 优惠源Id
 * @param sourceAmount 优惠金额
 * @param sourceDesc 优惠信息描述
 * @param discountItems 优惠项对应商品
 */
export interface OrderDiscount {
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
 * @description: 优惠对应的商品
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
 * 店铺运营模式
 */
export const enum ShopMode {
    COMMON = 'COMMON',
    SUPPLIER = 'SUPPLIER',
    O2O = 'O2O',
}

/**
 * 店铺类型
 */
export const enum ShopType {
    SELF_OWNED = 'SELF_OWNED',
    PREFERRED = 'PREFERRED',
    ORDINARY = 'ORDINARY',
}

/**
 * 额外信息
 * ShopOrderExtra
 */
export interface ShopOrderExtra {
    /**
     * 发货时间
     */
    deliverTime?: string
    /**
     * 收货时间
     */
    receiveTime?: string
}

/**
 * 店铺订单状态
 */
export const enum ShopOrderStatus {
    OK = 'OK',
    SYSTEM_CLOSED = 'SYSTEM_CLOSED',
    BUYER_CLOSED = 'BUYER_CLOSED',
    SELLER_CLOSED = 'SELLER_CLOSED',
}

/**
 * 订单备注表单
 */
export interface Remark {
    //订单播报通知
    orderNotify: boolean
    //平台备注
    platform?: string
    //店铺备注
    shop?: string
    //供应商备注
    supplier?: string
    //表单备注项
    items?: RemarkItem[]
}

export interface RemarkItem {
    key: string
    value: string
    type: FormType
}

export enum FormType {
    MOBILE = 'MOBILE',
    CITIZEN_ID = 'CITIZEN_ID',
    TEXT = 'TEXT',
    NUMBER = 'NUMBER',
    IMAGE = 'IMAGE',
    DATE = 'DATE',
    TIME = 'TIME',
    DATETIME = 'DATETIME',
    REMARK = 'REMARK',
}

/**
 * ShopOrder
 * 关联店铺订单包裹信息 以便处理数据
 */
export interface ShopOrder {
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * 店铺订单折扣总额
     */
    discountAmount?: number
    /**
     * 额外信息
     */
    extra?: ShopOrderExtra
    /**
     * 店铺订单实际运费总额
     */
    freightAmount?: number
    /**
     * id
     */
    id: Long
    /**
     * 店铺订单号
     * {主订单号}-{序号}
     */
    no: string
    /**
     * 关联订单信息
     */
    order?: ApiOrder
    /**
     * 订单号
     */
    orderNo?: string
    /**
     * 订单收货人
     */
    orderReceiver?: OrderReceiver
    /**
     * 店铺订单备注
     */
    remark: Remark
    /**
     * 店铺id
     */
    shopId: Long
    /**
     * 店铺logo
     */
    shopLogo?: string
    /**
     * 店铺运营模式
     */
    shopMode?: keyof typeof ShopMode
    /**
     * 店铺名称
     */
    shopName?: string
    /**
     * 店铺订单项
     */
    shopOrderItems: ShopOrderItem[]
    /**
     * 店铺类型
     */
    shopType?: keyof typeof ShopType
    /**
     * 店铺订单项
     * 根据skuId分组
     * 将同一个sku的订单项合并到同一个skuGroupShopOrderItems中
     * 属于上面拆单的逆操作
     */
    skuGroupShopOrderItems?: Obj
    /**
     * 店铺订单状态
     */
    status: keyof typeof ShopOrderStatus
    /**
     * 店铺订单实际总额
     */
    totalAmount?: number
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 乐观锁版本号
     */
    version?: number
    packageMap?: Map<string, ShopOrderItem[]>
}

/**
 * 售后状态
 */
export const enum AfsStatus {
    NONE = 'NONE',
    REFUND_REQUEST = 'REFUND_REQUEST',
    REFUND_AGREE = 'REFUND_AGREE',
    SYSTEM_REFUND_AGREE = 'SYSTEM_REFUND_AGREE',
    REFUND_REJECT = 'REFUND_REJECT',
    REFUNDED = 'REFUNDED',
    RETURN_REFUND_REQUEST = 'RETURN_REFUND_REQUEST',
    RETURN_REFUND_AGREE = 'RETURN_REFUND_AGREE',
    SYSTEM_RETURN_REFUND_AGREE = 'SYSTEM_RETURN_REFUND_AGREE',
    RETURN_REFUND_REJECT = 'RETURN_REFUND_REJECT',
    RETURNED_REFUND = 'RETURNED_REFUND',
    RETURNED_REFUND_CONFIRM = 'RETURNED_REFUND_CONFIRM',
    SYSTEM_RETURNED_REFUND_CONFIRM = 'SYSTEM_RETURNED_REFUND_CONFIRM',
    RETURNED_REFUND_REJECT = 'RETURNED_REFUND_REJECT',
    RETURNED_REFUNDED = 'RETURNED_REFUNDED',
    BUYER_CLOSED = 'BUYER_CLOSED',
    SYSTEM_CLOSED = 'SYSTEM_CLOSED',
}

/**
 * 额外数据
 * ItemExtra
 */
export interface ItemExtra {
    /**
     * 属性金额
     */
    attributeMoney?: number
    /**
     * 优惠的运费
     */
    discountFreight?: number
    /**
     * 分润比率
     */
    profitRate?: number
    /**
     * sku 真实价格 (如果供应商 该价格就是供应商可以得到得金额)
     */
    skuPracticalSalePrice?: number
    /**
     * 供应商分润比例
     */
    supplierProfitRate?: number
}

/**
 * ShopOrderItem
 */
export interface ShopOrderItem {
    /**
     * 商品类型
     */
    productType: string
    /**
     * 售后工单号
     */
    afsNo: string
    /**
     * 售后状态
     */
    afsStatus: keyof typeof AfsStatus
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 成交价(活动价: 如秒杀,等等)
     */
    dealPrice: number
    /**
     * 逻辑删除标记
     */
    deleted: boolean
    /**
     * 配送方式
     */
    deliverType: keyof typeof DeliverType
    /**
     * 额外数据
     */
    extra: ItemExtra
    /**
     * 修正价格  除不尽时的保留价 以当前上商品总额计算 修正价格 = 商品总额 - ((商品总额 / 商品数量) * 商品数量)
     */
    fixPrice: number
    /**
     * 运费
     */
    freightPrice: number
    /**
     * 运费模板id
     */
    freightTemplateId: number
    /**
     * id
     */
    id: number
    /**
     * 该商品sku 图片
     */
    image: string
    /**
     * 购买数量
     */
    num: number
    /**
     * 订单号
     */
    orderNo: string
    /**
     * 包裹id 发货时对应关系
     */
    packageId: number
    /**
     * 包裹状态
     */
    packageStatus: keyof typeof PackageStatus
    /**
     * 商品id
     */
    productId: number
    /**
     * 商品名称
     */
    productName: string
    /**
     * 销售单价
     */
    salePrice: number
    /**
     * 商品销售类型
     */
    sellType: keyof typeof SellType
    /**
     * 商品服务保障
     */
    services: keyof typeof Services
    /**
     * 店铺id
     */
    shopId: number
    /**
     * 关联店铺订单包裹信息 以便处理数据
     */
    shopOrder: ShopOrder
    /**
     * 商品 sku Id
     */
    skuId: number
    /**
     * 商品规格
     */
    specs: any
    /**
     * 订单商品状态
     */
    status: keyof typeof Status
    /**
     * 供应商id
     */
    supplierId: number
    /**
     * 供应商店铺类型
     */
    supplierShopType: keyof typeof SupplierShopType
    /**
     * 更新时间
     */
    updateTime: string
    /**
     * 乐观锁版本号
     */
    version: number
    /**
     * 单件商品重量
     */
    weight: any
}

/**
 * 订单商品状态
 */
export const enum Status {
    CLOSED = 'CLOSED',
    OK = 'OK',
}

/**
 * 供应商店铺类型
 */
export const enum SupplierShopType {
    SELF_OWNED = 'SELF_OWNED',
    PREFERRED = 'PREFERRED',
    ORDINARY = 'ORDINARY',
}

/**
 * 商品销售类型
 */
export const enum SellType {
    PURCHASE = 'PURCHASE',
    CONSIGNMENT = 'CONSIGNMENT',
    OWN = 'OWN',
}

/**
 * 商品服务保障
 */
export const enum Services {
    NO_FREIGHT = 'NO_FREIGHT',
    SEVEN_END_BACK = 'SEVEN_END_BACK',
    TWO_DAY_SEND = 'TWO_DAY_SEND',
    FAKE_COMPENSATE = 'FAKE_COMPENSATE',
    ALL_ENSURE = 'ALL_ENSURE',
}

/**
 * @description: 商铺订单包裹相关
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
// interface ShopOrderPackage {
//     shopId: string
//     orderId: string
//     shopOrderId: string
//     freightTemplateId: string
//     freightPrice: number
//     status: keyof typeof PACKAGESTATUS
//     type: keyof typeof PACKAGETYPE
//     receiverName: string
//     receiverMobile: string
//     receiverAreaCode: string[]
//     receiverAddress: string
//     receiveTime: string
//     shopOrderPackageItems: ShopOrderPackageItem[]
// }
/**
 * @description: 店铺订单包裹商品相关
 * @param packageId 配送包裹id
 * @param specs 商品规格
 * @param num 购买数量
 * @param image 该商品sku 图片
 * @param salePrice 销售单价
 * @param dealPrice 成交价(活动价: 如秒杀,等等)
 */
// export interface ShopOrderPackageItem {
//     shopId: string
//     orderId: string
//     packageId: string
//     productId: string
//     productImage: string
//     productName: string
//     skuId: string
//     specs: string[]
//     num: number
//     image: string
//     weight: number
//     salePrice: number
//     dealPrice: number
// }
// *-----------------
/**
 * @description 订单列表的类型
 *  @param {current} current 页码
 * @param {size} size 每页展示几条
 * @param {total} total 总数
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
    platform: PlatformList
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
export interface PlatformList {
    WECHAT_MINI_APP: '小程序'
    WECHAT_MP: '公众号'
    H5: 'H5商城'
    APP: 'APP商城'
    PC: 'PC商城'
}
export interface ExtraMap {
    AllDeliveryCount: string
    miniDeliveryCount: string
}
export interface Params {
    orderNo: string | any
    buyerNickname: string
    startTime: string
    endTime: string
    productName: string
    receiverName: string
    platform: PlatformList | string
}

export interface Query {
    status: string
    params: Params
}
