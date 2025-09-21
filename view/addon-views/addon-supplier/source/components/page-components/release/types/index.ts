export interface ReleaseList {
  deleted: boolean
  supplierName: string
  extra: {
    customDeductionRatio: string
    platformCategory: {
      one: string
      three: string
      two: string
    }
    productAttributes: any[]
    productParameters: any[]
    shopCategory: {
      one: string
      three: string
      two: string
    }
    productViolation: {
      examineDateTime: string
      rummager: string
      violationEvidence: string
      violationExplain: string
      violationType: string[]
    }
  }
  id: string
  name: string
  pic: string
  salePrices: string[]
  shopId: string
  status: 'SELL_ON' | 'SELL_OFF' | 'PLATFORM_SELL_OFF'
  storageSkus: StorageSku[]
}

interface StorageSku {
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
  specs: string[]
  stock: string
  stockType: 'UNLIMITED' | 'LIMITED'
  updateTime: string
  version: number
  weight: number
}
