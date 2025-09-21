export interface ProductList {
    createBeginTime: string
    createEndTime: string
    current: number
    name: string
    pages: number
    records: ProductItem[]
    size: number
    total: number
}

export interface ProductItem {
    createTime: string
    extra: Extra
    id: string
    name: string
    pic: string
    productStatus: string
    saleDescribe: string
    sellType: string
    shopId: string
    shopName: string
    storageSkus: StorageSkus[]
}

export interface Extra {
    auditTime: string
    customDeductionRatio: number
    platformCategory: PlatformCategory
    productAttributes: any[]
    productParameters: any[]
    shopCategory: ShopCategory
    submitTime: string
}

export interface PlatformCategory {
    one: Long
    three: Long
    two: Long
}

export interface ShopCategory {
    one: Long
    three: Long
    two: Long
}

export interface StorageSkus {
    activityId: number
    activityType: string
    createTime: string
    deleted: boolean
    id: string
    image: string
    initSalesVolume: number
    limitNum: number
    limitType: string
    minimumPurchase: number
    price: number
    productId: string
    salePrice: number
    salesVolume: number
    shopId: string
    sort: number
    specs: string[]
    stock: number
    stockType: string
    updateTime: string
    version: number
    weight: number
}
export interface DataType {
    shopId: string
    productId: string
}

export interface LabelType {
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

export interface ProductType {
    auditStatus: string
    auditTime: string
    id: string
    name: string
    pic: string
    salePrice: number
    salePrices: number[]
    shopId: string
    shopName: string
    submitTime: string
}

export interface Location {
    type: string
    coordinates: number[]
}

export interface Extra {
    auditTime: string
    operatorName: string
    operatorPhone: string
    operatorUserId: number
    reasonForRejection: string
    settledWay: string
}

export interface ShopInfoType {
    address: string
    area: string[]
    briefing: string
    companyName: string
    contractNumber: string
    createTime: string
    deleted: boolean
    end: string
    extra: Extra
    extractionType: string
    headBackground: string
    id: string
    location: Location
    logo: string
    name: string
    newTips: string
    no: string
    shopMode: string
    shopType: string
    start: string
    status: string
    updateTime: string
    userId: string
    userPhone: string
    version: number
}
