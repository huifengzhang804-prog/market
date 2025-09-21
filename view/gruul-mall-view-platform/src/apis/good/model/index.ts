import type { Limit } from '@/views/good/hooks/types'

export enum RETRIEVESORT {
    salePrice_desc = 'salePrice_desc',
    salePrice_asc = 'salePrice_asc',
    salesVolume_desc = 'salesVolume_desc',
    salesVolume_asc = 'salesVolume_asc',
    createTime_desc = 'createTime_desc',
    createTime_asc = 'createTime_asc',
    comprehensive_desc = 'comprehensive_desc',
    comprehensive_asc = 'comprehensive_asc',
}

export interface RetrieveParam {
    ids: string[]
    keyword: string
    categoryFirstId: string
    categorySecondId: string
    categoryThirdId: string
    platformCategoryFirstId: string
    platformCategorySecondId: string
    platformCategoryThirdId: string
    productId: string[] | string
    pageNum: number
    size: number
    sort: keyof typeof RETRIEVESORT
    searchTotalStockGtZero?: boolean
    minPrice: number | string
    maxPrice: number | string
    current: number
    pages: number
    shopId: string
    activityType: string
    startTime: string
    endTime: string
    showCoupon?: boolean
}
/**
 * 商品检索返回类型
 */
export interface ApiRetrieveComItemType {
    createTime: string
    id: string
    initSalesVolume: number
    pic: string
    categoryFirstId?: string
    categorySecondId?: string
    categoryThirdId?: string
    platformCategoryFirstId: string
    platformCategorySecondId: string
    platformCategoryThirdId: string
    productId: string
    productName: string
    salePrices: string[]
    prices: string[]
    salesVolume: string
    shopId: string
    shopName: string
    specs: string[]
    skuIds: string[]
    status: 'SELL_ON' | 'SELL_OFF'
    stockTypes: keyof (typeof Limit)[]
    widePic: string
    stocks: string[]
    isCheck?: boolean
    couponList: any[]
}
export interface ApiRetrieve {
    list: ApiRetrieveComItemType[]
    total: number
    pageNum: number
    pageSize: number
    pages: number
    size: number
    current: number
    id: string
}
