export interface FootprintItem {
  date: string
  id: string
  productId: Long
  productPic: string
  productPrice: string
  shopId: Long
}
export interface Date {
  year: number
  month: number
  day: number
}
/**
 * 分月处理之后的数据
 */
export interface FootprintList {
  date: string
  records: {
    date: string
    id: string
    productId: Long
    productPic: string
    productPrice: string
    done: boolean
    shopId: Long
    supplierId?: Long
  }[]
}
