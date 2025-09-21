/**
 *
 * @param {} UNAPPROVED  未审核
 * @param {} UNDER_REVIEW  审核中
 * @param {} APPROVED  审核通过
 * @param {} FAILED_APPROVED  审核不通过
 * @param {} VIOLATION__OFF_SHELF  违规下架
 */
enum GOODS_TYPE {
    UNAPPROVED = 'UNAPPROVED',
    UNDER_REVIEW = 'UNDER_REVIEW',
    APPROVED = 'APPROVED',
    FAILED_APPROVED = 'FAILED_APPROVED',
    VIOLATION__OFF_SHELF = 'VIOLATION__OFF_SHELF',
}
export type GoodsJointType = keyof typeof GOODS_TYPE
export interface ApiGoodsItem {
    id: string
    auditStatus: GoodsJointType
    createTime: string
    goodsId: string
    ossImgUrl: string
    price: number
    priceType: 1 | 2 | 3
    productId: string
    productName: string
    shopId: string
    shopName: string
    price2?: number
    verifyTime: string
}
