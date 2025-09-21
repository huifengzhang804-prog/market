export enum ShopTypeEnum {
  SELF_OWNED = '自营',
  PREFERRED = '优选',
  ORDINARY = '普通',
}

export interface ShopCoupon {
  id: string
  rowNum?: number
  shopId: Long
  sourceDesc: string
}

interface GoodMemberInfo {
  memberLabel: {
    fontColor: string
    id: string
    labelColor: string
    name: string
    priceFontColor: string
    priceLabelColor: string
    priceLabelName: string
  }
  memberType: 'FREE_MEMBER' | 'PAID_MEMBER'
  relevancyRights: {
    extendValue: string
    memberRightsId: string
    rightsName: string
    rightsType: string
  }
}
export interface GoodItemType {
  productId: Long
  productName: string
  salePrices: Long[]
  salesVolume: string
  shopName: string
  shopId: Long
  pic: string
  specs: string[]
  status: string
  productLabel?: {
    backgroundColor?: string
    fontColor?: string
    name?: string
    shopId?: Long
    shopType?: ShopTypeEnum[]
  }
  saleDescribe: string
  freightTemplateId: number
  couponList?: ShopCoupon[]
  shopType?: keyof typeof ShopTypeEnum
  shopOperationHistory?: string
  memberInfo?: GoodMemberInfo
}
