export interface BargainInfoData {
    bargainActivityProducts: BargainActivityProduct[]
    bargainingPeople: number
    bargainValidityPeriod: number
    endTime: string
    helpCutAmount: keyof typeof HelpCutAmount
    id: string
    isSelfBargain: boolean
    name: string
    shopId: string
    stackable: Stackable
    startTime: string
    status: keyof typeof AddBargainStatus
}

export interface BargainActivityProduct {
    activityId: string
    floorPrice: number
    productId: string
    productName: string
    productPic: string
    shopId: string
    skuId: string
    skuName: string
    skuPrice: number
    skuStock: number
    stock: number
    stockType: 'UNLIMITED' | 'LIMITED'
}

export interface Stackable {
    coupon: boolean
    full: boolean
    vip: boolean
}

export enum AddBargainStatus {
    NOT_STARTED = '未开始',
    PROCESSING = '进行中',
    OVER = '已结束',
    ILLEGAL_SELL_OFF = '违规下架',
    SHOP_SELL_OFF = '已下架',
}

export enum HelpCutAmount {
    RANDOM_BARGAIN = '随机砍价',
    FIX_BARGAIN = '固定砍价',
}

export interface BargainData {
    current: number
    keyword: string
    pages: number
    platformVisit: boolean
    records: BargainItem[]
    size: number
    total: number
}

export interface BargainItem {
    endTime?: string
    id: string
    name?: string
    payOrder?: number
    peopleNum?: number
    productNum?: number
    shopId?: string
    shopName?: string
    startTime?: string
    status?: string
}
