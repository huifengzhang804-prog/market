export interface ShippedGoodsList {
  deleted: boolean
  extra: {
    consignmentPriceSetting: {
      sale: string
      scribe: string
      type: keyof typeof ConsignmentPriceSettingType
    }
    customDeductionRatio: string
    platformCategory: {
      one: string
      three: string
      two: string
    }
    shopCategory: {
      one: string
      three: string
      two: string
    }
    productViolation?: {
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
  status: keyof typeof GoodsStatusEnum
  supplierId: string
  supplierName: string
  storageSkus: StorageSku[]
}

enum ConsignmentPriceSettingType {
  RATE = '比例',
  REGULAR = '固定',
}
export enum GoodsStatusEnum {
  REFUSE = '已拒绝',
  UNDER_REVIEW = '审核中',
  SELL_OFF = '下架',
  SELL_ON = '上架',
  SELL_OUT = '已售完',
  PLATFORM_SELL_OFF = '违规下架',
  UNUSABLE = '店铺不可用',
  SUPPLIER_SELL_OFF = '供应商下架',
  SUPPLIER_DISABLE = '供应商禁用',
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
