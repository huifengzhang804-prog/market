export enum ShopTypeEnum {
  SELF_OWNED = '自营',
  PREFERRED = '优选',
  ORDINARY = '普通',
}

export interface RecommandShop {
  couponList?: CouponInfo[]
  id: string
  location: {
    type: string
    coordinates: number[]
  }
  logo: string
  mode: 'B2B2C' | 'O2O'
  name: string
  productList: ProductInfo[]
  salesVolume: string
  shopMode: string
  shopType: keyof typeof ShopTypeEnum
  initialDeliveryCharge?: number
  distance?: string
}

interface CouponInfo {
  id: string
  rowNum: number
  shopId: Long
  sourceDesc: string
}

interface ProductInfo {
  id: string
  albumPics: string
  name: string
  pic: string
  shopId: Long
  videoUrl: string
  widePic: string
  salePrice: Long
}
