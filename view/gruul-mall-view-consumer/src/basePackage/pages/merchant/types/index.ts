import { ShopMode } from '@/constant/global'

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
}
type DeCategoryItem = {
  id: string
  name: string
  productNum: number
}
export interface ApiCategoryData {
  id: string
  name: string
  categoryImg?: string
  children: ApiCategoryData[]
}
export type CommodityItem = {
  name: string
  pic: string
  id: string
  salePrices: string[]
  productId?: string
}

export enum ShopStatus {
  /**
   * 审核拒绝
   */
  REJECT,
  /**
   * 禁用
   */
  FORBIDDEN,
  /**
   * 审核中
   */
  UNDER_REVIEW,
  /**
   * 正常
   */
  NORMAL,
}
