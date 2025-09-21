import type { StorageProducts } from '@/views/shoppingcart/types/index'
import { ApiOrderCouponVO } from '@/types/coupon/index'
import { Geometry } from '@/apis/address/types'
export interface AddressItemType {
  id: string
  address: string
  area: [string, string, string?]
  isDefault: boolean
  mobile: string
  name: string
  location: Geometry
}
export enum ORDER_TYPE {
  COMMON,
  SPIKE,
  TEAM,
  BARGAIN,
  PACKAGE,
}
export interface OrderType {
  coupon: null | ApiOrderCouponVO
  shopId: string
  shopLogo: string
  shopName: string
  shopType: string
  showShopCouponList?: any
  products: StorageProducts[]
  couponOrderList?: ApiOrderCouponVO[]
  checkedItemCoupon: ApiOrderCouponVO
}
/**
 * @description: 下单提交表单类型
 * @param receiver 用户地址信息
 * @param shopPackages 商铺包
 */
export interface OrderFormSubmitType {
  receiver: Omit<AddressItemType, 'isDefault'>
  shopPackages: OrderShopPackage[]
  source: 'CART' | 'PRODUCT'
  orderType: keyof typeof ORDER_TYPE
  distributionMode: string
  // activityId: string
  secKillCode?: string
  // shopCoupons: Record<string, string>

  discounts: any
  activity: {
    activityId?: any
    extra?: {
      userNum?: any
      setMealProducts?: SetMealProducts[]
      teamNo?: string | number
      activityId?: string
    }
  }
  extra: Record<string, string>
}
export interface SetMealProducts {
  activityId: string
  productAttributes: 'MAIN_PRODUCT' | 'MATCHING_PRODUCTS'
  productId: string
  shopId: string
  skuId: string
}
export interface OrderShopPackage {
  id: string
  name: string
  logo: string
  remark: { [x: string]: any }
  products: OrderPackageProductItem[]
}
/**
 * @description: 商品信息
 * @param {string} id 商品id
 * @param {string} skuId 商品分类Id
 */
interface OrderPackageProductItem {
  id: string
  skuId?: string
  num: number
}
export type FormType = 'MOBILE' | 'CITIZEN_ID' | 'TEXT' | 'NUMBER' | 'IMAGE' | 'DATE' | 'TIME' | 'DATETIME'
export interface DealSetting {
  key: string
  placeholder: string
  required: boolean
  type: FormType
}
export interface ApishopDealSetting {
  [x: string]: DealSetting[]
}

export interface OrderParamsType {
  productId: string
  type: OrderType
  extra: any
  activityId: string
  stackable: Record<'vip' | 'coupon' | 'full', boolean>
}
export interface OrderDetails {
  freight: number
  memberDiscount: number
  payAmount: number
  platformDiscount: number
  shopDiscount: number
  shopFreight: {
    [key: string]: number
  }
  shopFull: {
    [key: string]: number
  }
  total: number
}
