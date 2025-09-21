enum ShopTypeEnum {
  SELF_OWNED = '自营',
  PREFERRED = '优选',
  ORDINARY = '普通',
}
export interface ConcernItem {
  logo: string
  newProducts: boolean
  numberFollowers: number
  shopId: Long
  shopName: string
}
export interface ConcernShopInfoItem {
  goodsCreateTime?: string
  createTime: string
  name: string
  logo: string
  newTips: string
  productList: ProductItem[]
  shopId: Long
  shopType?: keyof typeof ShopTypeEnum
  pv?: number
  buyMore?: number
}
export interface ProductItem {
  pic: string
  id: string
}
