export interface ApiFinanceItem {
    amount: string
    changeType: string
    createTime: string
    orderNo: string
    shopId: string
    shopName: string
    tradeDetail: TradeDetail
    tradeNo: string
    tradeTime: string
    tradeType: string
    userId: string
    id?: string
}
interface TradeDetail {
    afsNo?: string
    shopOrderItemId?: string
    packageId: string
}
export interface QueryFrom {
    transactionType: any
    changeType: any
    createTime: string[]
    startDate: any
    endDate: any
    current: number
    size: number
    shopId: Long
    orderNo: string
    tradeNo: string
}
