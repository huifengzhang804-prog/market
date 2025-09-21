enum Limit {
    UNLIMITED,
    PRODUCT_LIMITED,
    SKU_LIMITED,
}
export enum Stock {
    UNLIMITED,
    LIMITED,
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

export interface CommoditySpecTable extends CommoditySpecInfo {
    id: string
    stockType: keyof typeof Stock
    stock: string
    num: number
    salesVolume: string
    providerId: string
    productId: string
}
