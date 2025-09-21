interface StorageSku {
    activityType: string
    id: string
    image: string
    initSalesVolume: string
    limitNum: number
    limitType: 'UNLIMITED' | 'LIMITED'
    minimumPurchase: number
    price: string
    productId: string
    salePrice: string
    salesVolume: string
    shopId: string
    specs: string[]
    stock: string
    stockType: 'LIMITED' | 'UNLIMITED'
}

export enum Limit {
    UNLIMITED,
    PRODUCT_LIMITED,
    SKU_LIMITED,
}
