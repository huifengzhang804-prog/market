export interface ParamsAssess {
  orderNo: string
  shopId: Long
  items: {
    key: {
      productId: Long
      skuId: Long
    }
    comment: string
    medias: string[]
    rate: number
  }[]
}
