enum Services {
    NO_FREIGHT,
    SEVEN_END_BACK,
    TWO_DAY_SEND,
    FAKE_COMPENSATE,
    ALL_ENSURE,
}
export enum Limit {
    UNLIMITED,
    PRODUCT_LIMITED,
    SKU_LIMITED,
}
export enum Stock {
    UNLIMITED,
    LIMITED,
}
/**
 * @description: 商品基本信息部分类型
 * @param platformCategoryId 平台分类id
 * @param categoryId 店铺分类id
 * @param providerId 供应商id
 */
interface CommodityBasicType {
    name: string
    saleDescribe: string
    platformCategoryId: string
    categoryId: string
    providerId: string
    attributeId: string
    videoUrl: string
    albumPics: string
    freightTemplateId: number
    pic: string
    shopId: string
}
/**
 * @description: 商品规格组
 * @param {string} inputValue 规格值
 * @param {string} name 规格名称
 */
export interface CommoditySpecGroup {
    inputValue: string
    inputVisble: boolean
    name: string
    children: CommoditySpecGroup[]
}
/**
 * @description: 商品规格信息
 */
interface CommoditySpecInfo {
    image: string
    price: string | number
    salePrice: string | number
    initSalesVolume: number
    limitNum: number
    weight: number
    limitType: keyof typeof Limit
    specs?: string[]
    stockType: keyof typeof Stock
}
/**
 * @description: 商品销售信息部分类型
 * @param price 划线价
 * @param salePrice 销售价
 * @param initSalesVolume 初始销量
 * @param serviceIds 服务保障
 */
interface CommoditySaleType {
    serviceIds: (keyof typeof Services)[]
    specGroups: CommoditySpecGroup[]
    skus: CommoditySpecTable[]
}
export interface CommoditySpecTable extends CommoditySpecInfo {
    id: string
    stockType: keyof typeof Stock
    stock: string
    num: number
    salesVolume: string
    providerId: string
    productId: string
    countNum: any
}
interface CommodityInfoType {
    detail: string
}

export interface ApiCommodityType extends CommodityBasicType, CommoditySpecInfo, CommoditySaleType {
    [key: string]: any
    id: string
    status: string
    shopId: string
    providerName: string
    storageSkus: CommoditySpecTable[]
}

export enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}
