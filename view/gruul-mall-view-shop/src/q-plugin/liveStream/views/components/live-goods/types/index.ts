/**
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

/**
 * 价格类型，1：一口价（只需要传入price，price2不传）
 * 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传）
 * 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传）
 */
/**
 * 直播商品新增类型
 */
export interface GoodsItemQuery {
    goodsId: string
    productId: string
    productName: string
    ossImgUrl: string
    coverImgUrl: string
    priceType: 1 | 2 | 3
    price: any
    price2: any
    url: string
}
/**
 * 商品
 */
export interface ApiGoods {
    auditId: string
    coverImgUrl: string
    auditStatus: GoodsJointType
    createTime: string
    goodsId: string
    id: string
    ossImgUrl: string
    price: string
    priceType: 1 | 2 | 3
    productId: string
    productName: string
    shopId: string
    updateTime: string
    price2?: string
    url: string
    verifyTime: string
}
/**
 * 选择商品弹窗类型
 */
export interface ChooseGoodsPopup extends ApiGoods {
    isCheck: boolean
}
export interface LiveGoodsType extends Omit<ApiGoods, 'auditStatus'> {
    auditStatus: 'APPROVED' | 'VIOLATION__OFF_SHELF'
}
