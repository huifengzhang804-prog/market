export interface PlatformDescriptionMap {
    [key: string]: string
}
export interface SearchFromData {
    orderNo: string // 订单号
    buyerNickname: string // 买家名称
    clinchTime: string
    productName: string // 商品名称
    receiverName: string // 收货人姓名
    distributionMode: string // 配送方式
    shopType: string // 店铺类型
    shopId: string
    platform: string
}
export interface ShopType {
    address: string
    bankAccount: {
        bankAccount: string
        bankName: string
        createTime: string
        openAccountBank: string
        payee: string
        updateTime: string
        version: number
    }
    briefing: string
    companyName: string
    contractNumber: string
    createTime: string
    drawPercentage: number
    extractionType: string
    id: string
    location: {
        type: string
        coordinates: [number, number]
    }
    logo: string
    name: string
    no: string
    registerInfo: {
        createTime: string
        legalPersonIdBack: string
        legalPersonIdFront: string
        license: string
        updateTime: string
        version: number
    }
    score: string
    shopBalance: {
        total: number
        uncompleted: number
        undrawn: number
    }
    shopMode: string
    shopType: string
    status: string
    updateTime: string
    userId: number
    userMobile: string
    version: number
}
