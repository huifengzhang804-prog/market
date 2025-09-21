export type DeCategoryType = {
  style: number
  listStyle: number
  buyBtnStyle: number
  goodsMargin: number
  fontSelected: string
  bgSelected: string
  fontNotSelected: string
  bgNotSelected: string
  categoryList: DeCategoryItem[]
  goodsShow: number[]
  commodityShow: number
  searchShow: number
  classificationTitle: string
  navShow: boolean
  categoryShow: boolean
}
type DeCategoryItem = {
  platformCategoryFirstId?: string
  platformCategoryFirstName?: string
  productNum: number
  name: string
  id: string
  platformCategorySecondId: string
  children: DeCategoryItem[]
}
export interface ApiCategoryData {
  id: string
  name: string
  categoryImg?: string
  shopId?: string
  children?: ApiCategoryData[]
  ads?: { img: string; link: any }[]
}
export type CommodityItem = {
  shopId: Long
  pic: string
  name: string
  productId: Long
  productName: string
  id: string
  salePrices: string[]
  widePic?: string
}
