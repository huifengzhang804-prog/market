export enum STATUS {
  /**
   * 下架
   */
  SELL_OFF,
  /**
   * 上架
   */
  SELL_ON,
}
/**
 * 收藏列表的每一项
 */
export interface ApiAssessItem {
  productId: Long
  productName: string
  productPic: string
  productPrice: string
  salesVolume: string
  shopId: Long
  status: string | keyof typeof STATUS
}
