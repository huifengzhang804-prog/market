import { Geometry } from '@/apis/afs/type'
import { IcStatus } from './type/order'

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
 * 商家配送 MERCHANT
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
 * 店铺订单状态
 */
export const enum ShopOrderStatus {
    OK = 'OK',
    SYSTEM_CLOSED = 'SYSTEM_CLOSED',
    BUYER_CLOSED = 'BUYER_CLOSED',
    SELLER_CLOSED = 'SELLER_CLOSED',
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
 * 订单来源
 */
export const enum Source {
    PRODUCT = 'PRODUCT',
    CART = 'CART',
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
 * 优惠生效状态
 */
export const enum DiscountStatus {
    OK = 'OK',
    CLOSED = 'CLOSED',
}

/**
 * 优惠类型
 */
export const enum DiscountType {
    PLATFORM_COUPON = 'PLATFORM_COUPON',
    SHOP_COUPON = 'SHOP_COUPON',
    MEMBER_DEDUCTION = 'MEMBER_DEDUCTION',
    FULL_REDUCTION = 'FULL_REDUCTION',
    CONSUMPTION_REBATE = 'CONSUMPTION_REBATE',
}

/**
 * 支付类型
 */
export const enum PayType {
    BALANCE = 'BALANCE',
    WECHAT = 'WECHAT',
    ALIPAY = 'ALIPAY',
}

/**
 * 自营店铺类型
 */
export const enum SelfShopType {
    SELF_SHOP = 'SELF_SHOP',
    SELF_SUPPLIER = 'SELF_SUPPLIER',
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

/**
 *
 */
export interface RemarkItem {
    key: string
    value: string
    type: FormType
}

/**
 * Order
 * 关联订单信息
 */
export interface Order {
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
    distributionMode?: keyof typeof DistributionMode
    /**
     * 附加数据
     */
    extra: {
        platform: string
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

    orderPayment: OrderPayment

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
    icStatus?: keyof typeof IcStatus // 同城配送状态
    icStatusDesc?: string // 同城配送状态描述
    /**
     * 是否关闭
     */
    close?: boolean
}

/**
 * ShopOrderItem
 */
export interface ShopOrderItem {
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
    /**
     * 发货数量
     */
    deliveryNum: number
    /**
     * 商品类型
     */
    productType: keyof typeof ProductTypeEnum
}

/**
 *  商品类型
 */
enum ProductTypeEnum {
    REAL_PRODUCT = '实物商品',
    VIRTUAL_PRODUCT = '虚拟商品',
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
    order?: Order
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
    remark?: Remark
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
 * OrderReceiver
 * 订单收货人
 */
export interface OrderReceiver {
    /**
     * 收货人详细地址
     */
    address: string
    /**
     * 省市区代码列表
     */
    area: [string, string, string?]
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * id
     */
    id: number
    /**
     * 收货人电话
     */
    mobile: string
    /**
     * 收货人名称
     */
    name: string
    /**
     * 订单号
     */
    orderNo?: string
    /**
     * 店铺店铺订单号
     */
    shopOrderNo?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 乐观锁版本号
     */
    version?: number
    location: Geometry
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
 * OrderDiscount
 */
export interface OrderDiscount {
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * 优惠项对应商品
     */
    discountItems?: any
    /**
     * id
     */
    id?: number
    /**
     * 订单号
     */
    orderNo?: string
    /**
     * 店铺商品优惠金额 map
     * 优惠信息对应的商品id列表
     */
    productIds?: any
    /**
     * 作用到的 sku key
     */
    skuKeys?: any
    /**
     * 优惠金额
     */
    sourceAmount?: number
    /**
     * 优惠信息描述
     */
    sourceDesc?: string
    /**
     * 优惠源Id 比如使用的优惠券id
     */
    sourceId?: number
    /**
     * 优惠生效状态
     */
    sourceStatus?: keyof typeof DiscountStatus
    /**
     * 优惠类型
     */
    sourceType?: keyof typeof DiscountType
    /**
     * 该优惠作用的所有商品总额
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
}

/**
 * OrderPayment
 */
export interface OrderPayment {
    /**
     * 创建时间
     */
    createTime: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * 优惠总金额
     */
    discountAmount?: number
    /**
     * 支付额外字段
     */
    extra?: Obj
    /**
     * 总运费
     */
    freightAmount?: number
    /**
     * id
     */
    id?: number
    /**
     * 关联订单信息
     */
    order?: Obj
    /**
     * 订单号
     */
    orderNo?: string
    /**
     * 包裹金额
     */
    packageAmount?: number
    /**
     * 支付总金额金额 = 订单总金额 + 运费 - 优惠总金额
     */
    payAmount?: number
    /**
     * 支付的用户id
     */
    payerId?: number
    /**
     * 支付时间
     */
    payTime?: string
    /**
     * 订单总金额
     */
    totalAmount?: number
    /**
     * 各店铺支付流水信息
     */
    transactions?: Obj
    /**
     * 支付类型
     */
    type: keyof typeof PayType
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
    remark?: string | Remark
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
 * 是否
 */
export const enum Whether {
    NO = 'No',
    YES = 'YES',
}

/**
 * ShopLogisticsAddress
 */
export interface ShopLogisticsAddress {
    /**
     * 详细地址
     */
    address?: string
    /**
     * 市级code
     */
    cityCode?: string
    /**
     * 联系人名称
     */
    contactName?: string
    /**
     * 联系人电话
     */
    contactPhone?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 是否为默认收货地址 0默认 1未默认
     */
    defReceive?: keyof typeof Whether
    /**
     * 是否 自营商家
     */
    defSelfShop?: keyof typeof Whether
    /**
     * 是否 自营供应商
     */
    defSelfSupplier?: keyof typeof Whether
    /**
     * 是否为默认发货地址 0默认 1未默认
     */
    defSend?: keyof typeof Whether
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * id
     */
    id?: number
    /**
     * 省级code
     */
    provinceCode?: string
    /**
     * 区级code
     */
    regionCode?: string
    /**
     * No comments found.
     */
    shopId?: number
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 乐观锁版本号
     */
    version?: number
    /**
     * 邮编码
     */
    zipCode?: string
}

/**
 * uupt运费详情
 */
export type IcFreightFee = {
    // 每个订单的运费 key 为订单id value为运费详情
    orderPriceMap: {
        [key: string]: {
            total: number // 单总运费
            discount: number // 优惠金额
            pay: number // 实付
        }
    }
    totalPrice: number // 总运费
    balance: number // 当前账户余额
}

export interface IcOrderDetail {
    // 收货人位置
    receiverLocation: Geometry

    // 配送历史 用户查询只有一条，管理端查询会有多条数据
    orders: IcOrder[]
}

export interface IcOrder {
    // 配送单状态
    status: keyof typeof IcStatus
    // 配送方类型
    type: keyof typeof IcType
    // 各状态时间节点
    times: IcOrderTimes
    remark?: string
    courier?: IcOrderCourier // 配送员信息
    errorHandler?: IcOrderErrorHandler // 异常状态处理
    pickupCode: number // 取餐码
    deliverSeconds: number // 配送时长 s
    statusDesc: string // 状态描述
}

export const enum IcType {
    // 商家自配
    SELF = 'SELF',
    UUPT = 'UUPT',
}

export interface IcOrderTimes {
    shippingTime: string // 发货时间
    takeOrderTime?: string // 接单时间
    arrivalShopTime?: string // 到店时间
    pickupTime?: string // 取货时间
    deliveredTime?: string //送达时间
    errorTime?: string // 发生错误的时间
    errorHandleTime?: string // 处理错误的时间
}

export interface CourierLoctionInfo {
    courier: IcOrderCourier
    distance: number // 距离目标位置的距离
    location?: Geometry // 定位
    expectTime: string // 预计送达时间
    minutes: number // 预计还有送达时长 单位分钟 为负代表已超时
}

export interface IcOrderCourier {
    name: string
    mobile: string
    avatar?: string
}

export interface IcOrderErrorHandler {
    desc: string
    status: keyof typeof IcOrderErrorHandlerStatus
}

export const enum IcOrderErrorHandlerStatus {
    RESHIP = 'RESHIP', // 重新发货
    DELIVERED = 'DELIVERED', // 已送达（待收货）
    CLOSE_REFUND = 'CLOSE_REFUND', // 关闭且退款
}

export enum HandleErrorStatus {
    RESHIP = '待发货',
    DELIVERED = '待收货（已送达）',
    CLOSE_REFUND = '已关闭（全额退款）',
}

export type HandleErrorForm = {
    orderNo: string
    status: keyof typeof HandleErrorStatus | ''
    desc: string
}

/**
 * 批量发货请求参数
 */
export interface BatchDeliverBody {
    /**
     * 发货信息
     * (object)
     */
    orderDelivery: OrderDeliveryDTO
    /**
     * 订单号
     */
    orderNo: string
    /**
     * 自营店铺类型
     */
    selfShopType?: keyof typeof SelfShopType
    /**
     * 店铺id 供应商发货比传
     */
    shopId?: number
}

/**
 * 发货信息
 */
export interface OrderDeliveryDTO {
    /**
     * 商品描述
     */
    cargo?: string
    /**
     * 发货方式
     */
    deliverType: keyof typeof DeliverType
    /**
     * 是否直接发货 当 deliverType为PRINT时必填
     */
    dropDeliver?: boolean
    /**
     * 物流公司
     */
    expressCompany?: ExpressCompanyDTO
    /**
     * 订单发货商品详情
     */
    items: OrderDeliveryItemDTO[]
    /**
     * 打印发货需要 打印机id
     */
    printId?: number
    /**
     * 收货人 当 deliverType 为 WITHOUT 时 不必填 其它必填
     */
    receiver?: UserAddressDTO
    /**
     * 备注
     */
    remark?: string
    /**
     * 自营店铺类型
     */
    selfShopType?: keyof typeof SelfShopType
    /**
     * 商品销售类型
     */
    sellType?: keyof typeof SellType
    /**
     * 打印发货需要
     */
    sender?: UserAddressDTO
    /**
     * 店铺 id 供应商操作时比传
     */
    shopId?: number
}

/**
 * 物流公司
 */
export interface ExpressCompanyDTO {
    /**
     * 快递公司代码
     */
    expressCompanyCode: string
    /**
     * 快递公司名称
     */
    expressCompanyName: string
    /**
     * 快递单号
     */
    expressNo?: string
}

/**
 * 订单发货商品详情
 */
export interface OrderDeliveryItemDTO {
    /**
     * 店铺订单商品id
     */
    itemId: number
    /**
     * 发货数量
     */
    num: number
}

/**
 * 收货人 当 deliverType 为 WITHOUT 时 不必填 其它必填
 * 打印发货需要
 */
export interface UserAddressDTO {
    /**
     * 详细地址
     */
    address: string
    /**
     * 联系方式
     */
    mobile: string
    /**
     * 姓名
     */
    name: string
    area: [string, string, string?]
}

export enum PrintTaskLink {
    CUSTOMER = '顾客联',
    MERCHANT = '商家联',
    KITCHEN = '后厨联',
    REMIND = '催单联',
}
