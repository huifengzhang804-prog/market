type ShopType = 'ORDINARY' | 'SELF_OWNED' | 'PREFERRED'

export interface Shop {
  shopId: Long
  shopLogo: string
  shopName: string
  newTips: string
  status: string
  shopType: ShopType
  followCount?: Long
  score?: string
}
