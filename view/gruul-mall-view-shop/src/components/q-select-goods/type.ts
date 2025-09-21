export type RetrieveType = {
    maxPrice?: number
    minPrice?: number
    platformCategoryFirstId?: string
    keyword?: string
}

/**
 * 选择分类
 */
export type ClassItemType = {
    platformCategoryFirstId: string
    platformCategoryFirstName: string
    productNum: number
}

export interface itemType {
    id: string
    productId: string
    shopId: string
    pic: string
    salePrices: string[]
    prices: string[]
    productName: string
    platformCategoryFirstId: string
}
