export interface SetMealNum {
  endTime: string
  saveAtLeast: string
  setMealId: string
  setMealMainPicture: string
  setMealName: string
}

// 套餐详情响应参数
export interface TResponseGetSetMealDetail {
  setMealId: string
  setMealName: string
  setMealDescription: string
  setMealType: string
  saveAtLeast: string
  endTime: string
  setMealProductDetails: SetMealProductDetails[]
  stackable: {
    payTimeout?: {
      units: any[]
      seconds: number
      nanos: number
    }
    coupon: boolean
    vip: boolean
    full: boolean
  }
  setMealMainPicture: string
}
// 优惠套餐规格数据
export interface SetMealProductDetails {
  productId: string
  productPic: string
  productName: string
  productAttributes: string
  skuName?: string[]
  setMealProductSkuDetails: TSetMealProductSkuDetails[]
  saveAtLeast?: string
  checked?: boolean
}
// 组合套餐sku
export interface TSetMealProductSkuDetails {
  matchingPrice: string
  matchingStock: string
  skuId: string
  skuName: string[]
  saveAtLeast: string
  stockType: 'UNLIMITED' | 'LIMITED'
  storageSku: ApiGoodSku
}

/**
 * @description: 商品sku规格table
 * @param initSalesVolume 初始销量
 */
export interface ApiGoodSku {
  id: string
  image: string
  initSalesVolume: number
  limitNum: number
  limitType: keyof typeof Limit
  price: string
  productId: string
  salePrice: string
  shopId: string
  stock: string
  stockType: keyof typeof Stock
  totalStock: string
  weight: number
  version: number
  specs: string[]
  salesVolume: string
  sort: number
  minimumPurchase: number
  updateTime: string
  deleted: boolean
  createTime: string
  activityType: string
  activityId: string
}

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}
enum Stock {
  UNLIMITED,
  LIMITED,
}

/**
 * @description: 商品sku组合
 */
export interface ApiGoodSkuCombination {
  skus: ApiGoodSku[]
  specGroups: ApiProductSpecs[]
}

export interface ApiProductSpecs {
  id: string
  name: string
  children: ApiProductSpecs[]
}
