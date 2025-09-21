export type searchFormType = {
  supplierGoodsName: string
  shopId: string | null
  platformCategoryId: string
  sellType: 'PURCHASE' | 'CONSIGNMENT' | 'OWN' | ''
  productType: 'REAL_PRODUCT' | 'VIRTUAL_PRODUCT' | ''
  status: string
  cascaderModel?: string
  sort?: string
}

export interface SupplierListInterface {
  albumPics: string
  createTime: string
  extra: {
    customDeductionRatio: string
    platformCategory: {
      one: string
      three: string
      two: string
    }
    productAttributes: []
    productParameters: []
  }
  id: string
  productName: string
  productType: 'VIRTUAL_PRODUCT' | 'REAL_PRODUCT'
  salePrices: string[]
  sellType: 'CONSIGNMENT' | 'PURCHASE' | 'OWN'
  shopId: string
  status: 'SELL_ON' | 'SELL_OFF' | 'PLATFORM_SELL_OFF'
  storageSkus: StorageSku[]
  supplierName: string
}

interface StorageSku {
  activityType: string
  id: string
  image: string
  initSalesVolume: string
  limitNum: number
  limitType: 'UNLIMITED' | 'LIMITED'
  minimumPurchase: number
  price: string
  productId: string
  salePrice: string
  salesVolume: string
  shopId: string
  specs: string[]
  stock: string
  stockType: 'LIMITED' | 'UNLIMITED'
}
/**
 * 地址信息
 */
export interface AddressInfo {
  address: string
  area: string[]
  contactName: string
  contactPhone: string
  createTime: string
  defReceive: string
  defSelfShop: string
  defSelfSupplier: string
  defSend: string
  deleted: boolean
  id: string
  shopId: string
  updateTime: string
  version: number
  zipCode: string
}
