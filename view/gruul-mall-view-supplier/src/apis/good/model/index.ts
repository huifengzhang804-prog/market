import type { Limit } from '@/views/goods/types'
export enum RETRIEVESORT {
    salePrice_desc,
    salePrice_asc,
    salesVolume_desc,
    salesVolume_asc,
    createTime_desc,
    createTime_asc,
    comprehensive_desc,
    comprehensive_asc,
}
export enum ACTIVITY_TYPE {
    // 普通订单
    COMMON = 'COMMON',
    // 秒杀
    SPIKE = 'SPIKE',
    // 拼团
    TEAM = 'TEAM',
    // 砍价
    BARGAIN = 'BARGAIN',
    // 套餐
    PACKAGE = 'PACKAGE',
}
export interface RetrieveParam {
    keyword: string
    categoryFirstId: string
    categorySecondId: string
    categoryThirdId: string
    platformCategoryFirstId: string
    platformCategorySecondId: string
    platformCategoryThirdId: string
    productId: string[] | string
    excludeProductIds: string[]
    pageNum: number
    size: number
    sort: keyof typeof RETRIEVESORT | string
    minPrice: number | string
    maxPrice: number | string
    current: number
    pages: number
    shopId: string
    activity?: {
        activityType?: ACTIVITY_TYPE
        startTime?: string
        endTime?: string
    }
}
/**
 * @description: 商品检索返回类型
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
    salesVolume: string
    shopId: string
    shopName: string
    specs: string
    skuIds: string[]
    status: 'SELL_ON' | 'SELL_OFF'
    stockTypes: keyof (typeof Limit)[]
    widePic: string
    stocks: string[]
    isCheck?: boolean
}
export interface ApiRetrieve {
    list: ApiRetrieveComItemType[]
    total: number
    pageNum: number
    pageSize: number
    pages: number
    size: number
}
