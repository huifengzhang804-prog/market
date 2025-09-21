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
enum Stock {
    UNLIMITED,
    LIMITED,
}
/**
 * 商品基本信息部分类型
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
 * 商品规格组
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
 * 商品规格信息
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
    StorageNum?: number
}
/**
 * 商品销售信息部分类型
 * @param price 划线价
 * @param salePrice 销售价
 * @param initSalesVolume 初始销量
 * @param serviceIds 服务保障
 */
export interface CommoditySaleType {
    serviceIds: (keyof typeof Services)[]
    specGroups: CommoditySpecGroup[]
    skus: CommoditySpecTable[]
}
export interface CommoditySpecTable extends CommoditySpecInfo {
    id: string
    stock: string
    num: number
    salesVolume: string
    providerId: string
    productId: string
}
interface CommodityInfoType {
    detail: string
}
/**
 * 新增商品信息类型总和
 */
export interface SubmitCommodityType extends CommodityBasicType, CommoditySpecInfo, CommoditySaleType, CommodityInfoType {
    id: string
}

/**
 * 供应商列表类型
 */
export interface ApiSupplierType {
    address: string
    area: string
    city: string
    country: string
    id: string
    mobile: string
    name: string
    productInfo: string
    province: string
    status: string
}

export interface ApiCommodityType extends CommodityBasicType, CommoditySpecInfo, CommoditySaleType {
    [key: string]: any
    id: string
    status: string
    shopId: string
    providerName: string
    storageSkus: CommoditySpecTable[]
}
export interface TableTypes {
    page: { size: number; current: number }
    goods: GoodsType[]
    total: number
}
export interface GoodsType {
    albumPics: string
    categoryId: string
    createTime: string
    detail: string
    checked?: boolean
    distributionMode: string[]
    extra: {
        consignmentPriceSetting: {
            sale: string
            scribe: string
            type: string
        }
        customDeductionRatio: string
        platformCategory: {
            one: string
            three: string
            two: string
        }
        productAttributes: any[]
        productParameters: any[]
        shopCategory: {
            one: string
            three: string
            two: string
        }
        supplierCustomDeductionRatio: string
    }
    freightTemplateId: string
    id: string
    name: string
    pic: string
    score: number
    sellType: string
    serviceIds: string[]
    shopId: string
    sort: number
    status: string
    storageSkus: StorageSku[]
    supplierId: string
    videoUrl: string
}

interface StorageSku {
    activityId: string
    activityType: string
    createTime: string
    deleted: boolean
    id: string
    image: string
    initSalesVolume: string
    initStock: string
    limitNum: number
    limitType: string
    minimumPurchase: number
    price: string
    productId: string
    salePrice: string
    salesVolume: string
    shopId: string
    sort: number
    specs: string[]
    stock: string
    stockType: string
    updateTime: string
    version: number
    weight: number
}

// 供货商搜索表单
export interface SearchFormType {
    name: string
    mobile: string
    supplierSn: string
}
export interface AddressInfo {
    address: string
    addressCode: string[]
    area: string
    city: string
    createTime: string
    id: string
    mobile: string
    name: string
    productInfo: string
    province: string
    score: number
    status: string
    supplierSn: string
    checked?: boolean
}
export interface ProductLabel {
    backgroundColor: string
    createTime: string
    deleted: boolean
    fontColor: string
    id: string
    name: string
    shopId: number
    shopType: string[]
    updateTime: string
    version: number
}
