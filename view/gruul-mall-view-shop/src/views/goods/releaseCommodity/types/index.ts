import type { Ref } from 'vue'

export interface SubmitFormInterface {
    id?: string
    name: string
    saleDescribe: string
    platformCategoryId: string
    categoryId: string
    providerId: string
    labelId: string
    widePic: string
    distributionMode: (keyof typeof DistributionMode)[]
    videoUrl: string
    albumPics: string
    productType: keyof typeof ProductType
    specGroups: SpecGroup[]
    collectionUrl?: string
    platformCategory: {
        one: string | null
        two: string | null
        three: string | null
    }
    shopCategory: {
        one: string | null
        two: string | null
        three: string | null
    }
    skus: SkuInterface[]
    productParameters: ProductParametersInterface[]
    productAttributes: ProductAttributesInterface[]
    serviceIds: (keyof typeof ServiceIdsEnum)[]
    detail: string
    freightTemplateId: string
    status: keyof typeof StatusEnum
    brandId: string
    sellType: keyof typeof SellTypeEnum
    extra?: any
}

enum SellTypeEnum {
    PURCHASE, // 采购
    CONSIGNMENT, // 代销
    OWN, // 自有
}
// 配送方式枚举
enum DistributionMode {
    MERCHANT,
    EXPRESS,
    INTRA_CITY_DISTRIBUTION,
    SHOP_STORE,
    VIRTUAL,
}
// 产品类型枚举
enum ProductType {
    REAL_PRODUCT, // 真实商品
    VIRTUAL_PRODUCT, // 虚拟商品
}

interface SpecGroup {
    id?: string
    name: string
    order: number
    children?: SpecGroup[]
}

interface SkuInterface {
    id?: string
    image: string
    initSalesVolume: number
    limitNum: number
    limitType: keyof typeof LimitTypeEnum
    price: number
    productId: string
    initStock: number
    salePrice: number
    shopId: string
    stockType: keyof typeof StockTypeEnum
    weight: number
    specs: string[]
}

enum LimitTypeEnum {
    UNLIMITED,
    PRODUCT_LIMITED,
    SKU_LIMITED,
}

enum StockTypeEnum {
    UNLIMITED,
    LIMITED,
}

interface ProductParametersInterface {
    id: string
    featureName: string
    isRequired: boolean
    isMultiSelect: boolean
    featureValues: FeatureValue[]
}

interface ProductAttributesInterface {
    id: string
    featureName: string
    isRequired: boolean
    isMultiSelect: boolean
    featureValues: FeatureValue[]
}

interface FeatureValue {
    featureValueId: string
    firstValue: string
    secondValue: string
}

export enum ServiceIdsEnum {
    NO_FREIGHT,
    SEVEN_END_BACK,
    TWO_DAY_SEND,
    FAKE_COMPENSATE,
    ALL_ENSURE,
}

enum StatusEnum {
    REFUSE, // 已拒绝
    UNDER_REVIEW, // 审核中
    SELL_OFF, // 下架
    SELL_ON, // 上架
    SELL_OUT, // 已售完
    PLATFORM_SELL_OFF, // 平台下架商品
    UNUSABLE, // 店铺不可用
}

export interface FormInject {
    submitForm: Ref<SubmitFormInterface>
    commodityImgList: Ref<string[]>
    copyGoodsJD: Ref<string>
    copyGoodsAL: Ref<string>
    copyGoodsTB: Ref<string>
    editProductPreviousStep: Ref<boolean>
    productLabel: Ref<string>
}
