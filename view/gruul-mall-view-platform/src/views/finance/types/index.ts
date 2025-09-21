export interface QueryFrom {
    transactionType: any
    changeType: any
    createTime: string[]
    startDate: any
    endDate: any
    keyword: any
    current: number
    size: number
    orderNo: string
    tradeNo: string
    targetShopId: string
}
type Extra = {
    totalProductServiceCharge?: number
}
export interface TableFromType {
    amount: string
    changeType: string
    createTime: string
    extra: Extra
    id: string
    orderNo: string
    shared: boolean
    sharingTarget?: string
    shopId: string
    shopName: string
    tradeDetail: {
        packageId: string
    }
    tradeNo: string
    tradeTime: string
    tradeType: string
    userId: string
    num?: string
}
