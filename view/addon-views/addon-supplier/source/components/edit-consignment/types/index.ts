export interface ListInterface {
  [key: string]: any
  albumPics: string
  id: string
  productName: string
  salePrices: string[]
  sellType: 'PURCHASE' | 'CONSIGNMENT'
  shopId: string
  shopName: string
  shopOwnProductStockNum: number
  storageSkus?: StorageSkusInterface[]
}

interface StorageSkusInterface {
  purchaseNum?: number
  activityId: string
  activityType: string
  createTime: string
  deleted: boolean
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
  sort: number
  stock: string
  stockType: 'UNLIMITED' | 'LIMITED'
  updateTime: string
  version: number
  weight: number
  shopOwnProductStockNum?: number
}

export interface SkuInterface {
  activityId: string
  activityType: keyof typeof ActivityTypeEnum
  createTime: string
  deleted: boolean
  id: string
  image: string
  initSalesVolume: string
  limitNum: number
  limitType: keyof typeof LimitTypeEnum
  minimumPurchase: number
  price: string
  productId: string
  salePrice: string
  salesVolume: string
  shopId: string
  sort: number
  specs: string[] | { name: string }
  stock: string
  stockType: 'LIMITED' | 'UNLIMITED'
  updateTime: string
  version: number
  weight: number
  actualSalePrice: string
  actualPrice: string
}

enum ActivityTypeEnum {
  COMMON,
  SPIKE,
  TEAM,
  BARGAIN,
  PACKAGE,
}
export enum LimitTypeEnum {
  UNLIMITED = '不限购',
  PRODUCT_LIMITED = '限购',
  SKU_LIMITED = '规格限购',
}
