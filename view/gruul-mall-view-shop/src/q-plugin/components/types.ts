import { PageParams } from '@/utils/types'

/**
 * 打印机列表搜索参数
 */
export interface PrinterPageParams extends PageParams {
    mode: keyof typeof PrinterDTOMode // 关联的业务模式
}
/**
 * 打印机模板列表搜索参数
 */
export interface PrintTemplatePageParams extends PrinterPageParams {
    link?: keyof typeof PrintTaskLink | '' // 打印类型 某联
    ticketSize?: keyof typeof PrinterSize | '' // 纸张尺寸
}

/**
 * 新增打印机参数
 */
export interface PrinterDTO {
    mode: keyof typeof PrinterDTOMode // 关联的业务模式
    brand: keyof typeof PrinterBrand // 打印机品牌
    name: string // 打印机名称
    place?: string // 机器位置
    sn: string // 打印机sn码 编辑时不填
    key?: string // 打印机密钥 打印机有则必填 编辑时不填
    flowCard?: string // 流量卡号 编辑时不填
}
/**
 * 修改打印机参数
 */
export interface PrinterEditDTO {
    id: Long // 编辑时必填
    mode: keyof typeof PrinterDTOMode // 关联的业务模式
    name: string // 打印机名称
    place?: string // 机器位置
}
export enum PrinterDTOMode {
    INTRA_CITY = '同城配送',
    STORE_PICKUP_SELF = '门店自提',
}
/**
 * 打印机列表项
 */
export interface Printer {
    id: Long
    brand: keyof typeof PrinterBrand // 打印机品牌
    size: keyof typeof PrinterSize // 纸张尺寸
    type: keyof typeof PrinterType // 机型
    status: keyof typeof PrinterStatus // 打印机状态
    place: string // 机器位置
    createDate: string // 创建时间
    name: string
    bound: boolean // 是否绑定打印任务
}

export enum PrinterBrand {
    FEIE = '飞鹅打印机', // 飞鹅打印机
}
export enum PrinterSize {
    V58 = '58mm', // 58小票机 58mm的机器,一行打印16个汉字,32个字母
    V80 = '80mm', // 80小票机 80mm的机器,一行打印24个汉字,48个字母
}
export enum PrinterType {
    P_58 = '58mm小票机', // 58小票机 58mm的机器,一行打印16个汉字,32个字母
    P_80 = '80mm小票机', // 80小票机 80mm的机器,一行打印24个汉字,48个字母
    P_LABEL = '标签机', // 标签机
    P_CCB = '出餐宝', // 出餐宝
}
export enum PrinterStatus {
    OFFLINE = '离线', // 打印机离线 离线的判断是打印机与服务器失去联系
    ONLINE_OK = '正常', // 在线，工作状态正常
    ONLINE_NOT_OK = '异常', // 在线，工作状态不正常 备注：异常一般是无纸
}

/**
 * 更新打印机任务参数
 */
export interface PrintTaskDTO {
    id?: Long // 编辑时必填
    mode: keyof typeof PrinterDTOMode // 关联的业务模式
    name: string // 打印机名称
    printerId: Long // 关联的打印机id
    templateId: Long // 关联的打印机模板 id
    scene: keyof typeof PrintTaskScene // 打印场景
    times: number // 打印次数
    link: keyof typeof PrintTaskLink | string // 打印类型 某联
    templateName?: string // 打印模板名称
}
/**
 * 打印任务列表项
 */
export interface PrintTask {
    id: Long
    name: string // 任务名称
    printerName: string // 绑定的打印机名称
    link: keyof typeof PrintTaskLink //打印类型 某联
    templateName: string // 绑定的打印模板名称
    scene: keyof typeof PrintTaskScene // 打印场景
    times: number // 打印次数
    createDate: string // 创建时间
    printerId: Long // 关联的打印机id
}
// 同城
export enum PrintTaskScene {
    MANUAL = '手动打印',
    AUTO_PAID = '付款成功自动打印',
    AUTO_DELIVERY = '核销成功自动打印',
}
// 到店
export enum ToStorePrintTaskScene {
    MANUAL = '手动打印',
    AUTO_PAID = '付款成功自动打印',
    AUTO_DELIVERY = '确认发货自动打印',
}
export enum PrintTaskLink {
    CUSTOMER = '顾客联',
    MERCHANT = '商家联',
    KITCHEN = '后厨联',
    REMIND = '催单联',
}

/**
 * 打印模板内容样式枚举配置
 */
export enum PrintTemplateConfigStyle {
    ALIGN_LEFT = '居左',
    ALIGN_CENTER = '居中',
    ALIGN_RIGHT = '居右',
    HEIGHT_1 = '正常高度',
    HEIGHT_2 = '双倍高度',
    WIDTH_1 = '正常宽度',
    WIDTH_2 = '双倍宽度',
    SIZE_1 = '正常字体大小',
    SIZE_2 = '双倍字体大小',
    WEIGHT_1 = '正常字体粗细',
    WEIGHT_2 = '字体加粗',
}

/**
 * 打印模板列表项
 */
export interface PrintTemplate {
    id: Long
    name: string // 模板名称
    brand: keyof typeof PrinterBrand // 打印机品牌
    link: keyof typeof PrintTaskLink //打印类型 某联
    size: keyof typeof PrinterSize // 纸张尺寸
    createDate: string // 创建时间
}
/**
 * 更新打印模板参数
 */
export interface PrintTemplateDetail extends PrintTemplateDTO {
    id: Long
    createTime: string // 创建时间
    updateTime: string // 更新时间
    version: number // 乐观锁版本号
    deleted: boolean // 逻辑删除标记
    shopId: Long // 所属店铺id
    template: string // 原始模板配置对应的模板
}
/**
 * 更新打印模板参数
 */
export interface PrintTemplateDTO {
    id?: Long // 编辑时必填
    mode: keyof typeof PrinterDTOMode // 关联的业务模式
    name: string // 模板名称
    brand: keyof typeof PrinterBrand // 打印机品牌
    link: keyof typeof PrintTaskLink //打印类型 某联
    size: keyof typeof PrinterSize // 纸张尺寸
    /**
     * 原始模板配置
     */
    config: PrintTemplateDTOConfig
}
export interface PrintTemplateDTOConfig {
    title: PrintTemplateConfigTitle // 头部内容
    shopName: PrintTemplateConfigShopName // 店铺名称
    orderRemark: PrintTemplateConfigOrderRemark // 订单备注
    products: PrintTemplateConfigProducts // 商品信息
    orderStatistic: PrintTemplateConfigOrderStatistic // 结算信息
    orderInfo: PrintTemplateConfigOrderInfo // 订单信息
    targetInfo: PrintTemplateConfigTargetInfo // 收货人或门店信息
    code: PrintTemplateConfigCode // 条码信息
    desc: string // 底部信息
}
/**
 * 打印模板通用配置
 */
export interface PrintTemplateConfig {
    b: boolean // 结尾处是否需要换行
    selected: boolean // 是否选中
    style: (keyof typeof PrintTemplateConfigStyle)[] // 样式
}
/**
 * 打印模板头部内容配置
 */
export interface PrintTemplateConfigTitle {
    platformName: true // 是否展示平台名称
    pickupCode: boolean // 是否展示取货码
    style: (keyof typeof PrintTemplateConfigStyle)[] // 样式
}
/**
 * 打印模板店铺名称配置
 */
// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface PrintTemplateConfigShopName extends PrintTemplateConfig {}
/**
 * 打印模板订单备注配置
 */
// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface PrintTemplateConfigOrderRemark extends PrintTemplateConfig {}
/**
 * 打印模板商品信息配置
 */
export interface PrintTemplateConfigProducts {
    type: keyof typeof PrintTemplateConfigProductsType // 商品打印样式
    skuId: boolean // 是否展示skuId
    productName: boolean // 是否展示商品名称
    specs: boolean // 是否展示规格
}
/**
 * 打印模板结算信息配置
 */
export interface PrintTemplateConfigOrderStatistic {
    payPrice: PrintTemplateConfig // 实付款
    productNum: boolean // 是否展示商品总数
    totalPrice: boolean // 是否展示商品总金额
    freight: boolean // 是否展示运费
    platformDiscount: boolean // 是否展示平台折扣
    shopDiscount: boolean // 是否展示店铺折扣
    totalDiscount: boolean // 是否展示总折扣
}
/**
 * 打印模板订单信息配置
 */
export interface PrintTemplateConfigOrderInfo {
    orderNo: PrintTemplateConfig // 订单编号
    payType: PrintTemplateConfig // 支付方式 支持样式
    orderTime: PrintTemplateConfig // 下单时间 支持样式
    payTime: PrintTemplateConfig // 支付时间 支持样式
}
/**
 * 打印模板收货人或门店信息配置
 * 同城配送时为收货人信息 自提时为门店
 */
export interface PrintTemplateConfigTargetInfo {
    name: PrintTemplateConfig
    mobile: PrintTemplateConfig
    address: PrintTemplateConfig
}
/**
 * 打印模板条码信息配置
 */
export interface PrintTemplateConfigCode {
    type: keyof typeof PrintTemplateConfigCodeType | '' // 二维码还是条形码
    content: string // 内容 仅支持数字和大小写字母
    desc: string // 说明
}
export enum PrintTemplateConfigCodeType {
    QR_CODE = '二维码',
    BAR_CODE = '条形码',
}
export enum PrintTemplateConfigProductsType {
    /**
     * 两端对齐
-------商品信息---------
测试商品       x1 ￥2.00
     */
    SPACE_BETWEEN = '样式一：商品名称较【长】时选用',
    /**
     * 表格形式的对齐
---------------------
商品名称     数量  单价
---------------------
测试商品     x1   ￥2.00
     */
    FORMAT = '样式二：商品名称较【短】时选用',
}

/**
 * 根据枚举映射出下拉选项
 * @param enumeration 枚举
 */
export const makeOptions = (enumeration: Obj) => {
    return Object.keys(enumeration).map((item) => {
        return {
            label: enumeration[item as keyof typeof enumeration],
            value: item,
        }
    })
}
