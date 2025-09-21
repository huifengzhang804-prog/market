export type searchFormType = {
    platformCategoryId?: string
    name: string
    createBeginTime?: string
    createEndTime?: string
    date: string
    shopId: string | null
    productType: string
    status: string
    sellType: string
    cascaderModel?: string | undefined
}

export type searchAuditingFormType = {
    platformCategoryId: string
    name: string
    shopId: string
    productType: string
    cascaderModel: string | undefined
}

/**
 * 平台商品api类型
 * @param {string} albumPics 画册
 * @param {string} name 商品名称
 * @param {string} productStatus 商品状态
 * @param {string} providerName 供货商
 */
export interface ApiGoodType {
    albumPics: string
    createTime: string
    id: string
    name: string
    saleDescribe: string
    shopId: string
    shopName: string
    providerName: string
    pic: string
    storageSkus: ApiGoodSkuType[]
    productStatus?: string
    status?: string
    sellType: keyof typeof SellTypeEnum
    supplierId?: string
    productType: keyof typeof ProductTypeEnum
    productLabel: ProductLabelType
}

enum ProductTypeEnum {
    REAL_PRODUCT = '实物商品',
    VIRTUAL_PRODUCT = '虚拟商品',
}

export type ProductLabelType = {
    backgroundColor: string
    createTime: string
    deleted: boolean
    fontColor: string
    id: string
    name: string
    shopId: number
}

export enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}

/**
 * 平台商品sku类型
 *  @param {string} stock 库存
 *  @param {string} stockType 库存类型
 *  @param {string}salePrice 单价
 */
export interface ApiGoodSkuType {
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

export enum Stock {
    LIMITED = 'LIMITED',
    UNLIMITED = 'UNLIMITED',
}
