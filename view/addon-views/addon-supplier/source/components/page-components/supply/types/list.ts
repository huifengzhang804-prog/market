export interface ListInterface {
  albumPics: string
  id: string
  productName: string
  salePrices: number[]
  sellType: 'PURCHASE' | 'CONSIGNMENT'
  shopId: string
  shopName: string
  shopOwnProductStockNum: number
  freightTemplateId: number
  purchaseNum: number
  storageSkus: StorageSkusInterface[]
}

export interface StorageSkusInterface {
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
