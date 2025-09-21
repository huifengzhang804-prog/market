/**
 * @param {*} comment 文本描述
 * @param {*} image  商品图片
 * @param {*} isExcellent 精选
 * @param {*} medias 评价图片
 * @param {*} name 商品名称
 * @param {*} productId 产品id
 * @param {*} rate 评价星级
 * @param {*} specs 规格
 * @param {*} shopReply 商家回复
 */
export interface Evaluate {
    comment: string
    createTime: string
    deleted: boolean
    id: string
    image: string
    isExcellent: boolean
    itemId: string
    medias: string[]
    name: string
    orderNo: string
    packageId: string
    productId: string
    rate: number
    shopId: string
    skuId: string
    specs: string[]
    updateTime: string
    userId: string
    shopReply?: string
    avatar?: string
    nickname?: string
}

export interface EvaluateSearchData {
    name: string
    nickname: string
    clinchTime: string
    rate: Long
}
export interface EvaluateSearchParams {
    name: string
    nickname: string
    rate: Long
    startTime: string
    endTime: string
}
