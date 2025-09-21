/**
 * @description: 商品规格结构
 * @param id 规格id
 * @param name 规格名称
 * @param order 排序方式
 * @param parentId 父规格id
 * @param updateTime 更新时间
 * @param version 版本号
 */
export interface NormType {
    id?: number
    name: string
    order?: number
    parentId?: number
    updateTime?: string
    children: NormType[]
    version?: string
    inputValue: string
    inputVisble: boolean
}

export interface NormListType {
    specs: any
    originalPrice: number
    price: number
    stock: number
    perLimit: number
    initStock: number
    id: string
    pic: string
    sale: number
    weight: number
    productSpecNames: string[] | string
    image: string
    salePrice: string
    limitType: string
    limitNum: number
    stockType: string
    initSalesVolume: number
    minimumPurchase: number
}

export enum ExtraLabelKeyEnum {
    划线价 = 'price',
    供货价 = 'salePrice',
    重量 = 'weight',
    限购类型 = 'limitType',
    限购数量 = 'limitNum',
    库存类型 = 'stockType',
    库存 = 'initStock',
    sku销量 = 'initSalesVolume',
    起批数 = 'minimumPurchase',
}
