import type { StorageSku } from '@/apis/good/model/index'
import { ADD_RESS_TYPES } from '@/apis/address/model'

// 套餐详情响应参数
export interface TResponseGetSetMealDetail {
  distributionMode: [ADD_RESS_TYPES.DISTRIBUTION]
  setMealId: number
  setMealName: string
  setMealDescription: string
  setMealType: string
  saveAtLeast: number
  endTime: string
  setMealProductDetails: setMealProductDetails[]
  shopId: Long
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
export interface setMealProductDetails {
  productId: Long
  productPic: string
  productName: string
  productAttributes: string
  skuName: string[]
  setMealProductSkuDetails: TSetMealProductSkuDetails[]
  saveAtLeast: string
  checked?: boolean
  flag?: boolean
}
// 组合套餐sku
interface TSetMealProductSkuDetails {
  matchingPrice: string
  matchingStock: string
  skuId: Long
  skuName: string[]
  saveAtLeast: string
  stockType: 'UNLIMITED' | 'LIMITED'
  storageSku: StorageSku
}

// 运费相关信息响应参数
export interface TResponseGproductDelivery {
  shopId: number | string
  productId: number | string
  skuId: number | string
  distributionMode: string[]
  freightTemplateId: number | string
  weight: number | string
}
