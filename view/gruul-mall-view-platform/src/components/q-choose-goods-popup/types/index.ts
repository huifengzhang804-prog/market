import { Stock } from '@/views/good/types'
enum Limit {
    UNLIMITED,
    PRODUCT_LIMITED,
    SKU_LIMITED,
}
/**
 * @param highestPrice: 最高价
 * @param lowestPrice: 最低价
 * @param productId: 商品ID
 * @param productName: 商品名称
 * @param productPic: 商品图
 * @param shopId: 店铺ID
 * @param skus: SkuItem[]
 */
export interface ApiGoodsRetrieve {
    highestPrice?: string
    lowestPrice?: string
    productId: string
    productName: string
    productPic: string
    shopId: string
    isCheck: boolean
    skus: SkuItem[]
    pic: string
}
export interface SkuItem {
    limitType?: keyof typeof Limit
    productId: string
    skuId: string
    skuName?: string[]
    skuPrice: string
    skuStock: string
    stockType: keyof typeof Stock
}

export type GoodsListItem = Omit<ApiGoodsRetrieve, 'skus' | 'isCheck'>
