import { Stock } from '@/components/q-choose-goods-popup/types'

declare module '@/views/detail/types/index' {
    export enum Limit {
        UNLIMITED,
        PRODUCT_LIMITED,
        SKU_LIMITED,
    }
    export interface ApiGoodSkus {
        salePrices: string
        attributePirce: string
        id: string
        image: string
        initSalesVolume: number
        limitNum: number
        limitType: keyof typeof Limit
        price: string
        productId: string
        salePrice: string
        shopId: string
        stock: string
        stockType: keyof typeof Stock
        totalStock: string
        weight: number
        specs: string[]
        salesVolume: string
        secKillPrice?: string // 秒杀价格
    }

    export interface ApiProductSpecs {
        id: string
        name: string
        children: ApiProductSpecs[]
    }

    /**
     * 商品sku组合
     */
    export interface ApiGoodSkuCombination {
        skus: ApiGoodSkus[]
        specGroups: ApiProductSpecs[]
    }
}
