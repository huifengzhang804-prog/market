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
 * @description: 收藏列表的每一项
 * @returns {*}
 */
export interface ApiAssessItem {
  productId: string
  productName: string
  productPic: string
  productPrice: string
  salesVolume: string
  shopId: string
  status: string | keyof typeof STATUS
  evaluatePerson: string
}
