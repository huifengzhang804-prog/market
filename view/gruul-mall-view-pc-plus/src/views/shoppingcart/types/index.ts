import { ApiGoodSku } from '@/views/detail/types/index'
/**
 * @description: 商品规格类型
 * @param {string} productId 商品ID
 * @param {string} productName 商品名称
 * @param {string} id 商品规格ID
 * @param {string} image 商品规格图片
 * @param {number} price 商品规格单价
 * @param {number} num 商品规格数量
 * @param {string[]} specs  商品规格数量
 */
export interface GoodItemSpec {
  productId: string
  productName: string
  id: string
  image: string
  price: number
  salePrice: number
  num: number
  specs: string[]
  freightTemplateId: string
  skuStock: ApiGoodSku
  finalPrice: string
}
// enum GoodFailReason {
//     店铺不可用 = '11001',
//     商品不可用 = '11002',
//     sku不可用 = '11003',
//     超库存 = '11004',
//     超限购 = '11005',
// }
/**
 * @description: 商品类型
 * @param {string} editedProductName 商品名称
 * @param {string} editedImage 商品图片
 * @param {string} editedPrice 商品价格
 * @param {GoodItemSpec} editedSpecs 商品规格
 * @param {string} reason 商品失效原因
 */
export interface GoodItemType extends GoodItemSpec {
  [x: string]: any
  finalPrice: string
  editedProductName?: string
  editedImage?: string
  editedPrice?: string
  editedSpecs?: string[]
  reason?: number
  isChecked: boolean
  skuId?: string
  isCountNumberComponentShow?: boolean
  uniqueId: string
}
/**
 * @description: 购物车商品列表类型
 * @param {number} shopId
 * @param {string} shopName
 * @param {GoodItemType[]} products
 */
export interface GoodListType {
  shopId: string
  shopName: string
  products: GoodItemType[]
  isAllChecked: boolean
  enable: boolean
  shopLogo: string
  shopType: string
}
/**
 * @description: 购物车商品类型
 * @param {ApiShopCarGoodType[]} valid 可用商品列表
 * @param {ApiShopCarGoodType[]} invalid 不可用商品列表
 */
export interface ApiShopCarGoodType {
  valid: GoodListType[]
  invalid: GoodListType[]
}
type ProductsPickItem =
  | 'id'
  | 'image'
  | 'price'
  | 'num'
  | 'productId'
  | 'productName'
  | 'specs'
  | 'skuId'
  | 'freightTemplateId'
  | 'salePrice'
  | 'productFeaturesValue'
export type StorageProducts = Pick<GoodItemType, ProductsPickItem> & { weight: string | number }
type StorageShop = Pick<GoodListType, 'shopId' | 'shopLogo' | 'shopName' | 'shopType'>
export type StoragePackage = {
  activityParam?: { activityId: string; extra: any; type: 'SPIKE' | 'TEAM' | 'BARGAIN' | 'PACKAGE' }
  products: StorageProducts[]
  distributionMode?: 'EXPRESS' | 'INTRA_CITY_DISTRIBUTION' | 'SHOP_STORE' | 'VIRTUAL' | 'WITHOUT'
} & StorageShop
// export type ChoosedSpecType = Pick<
//     ApiGoodSkuType,
//     'id' | 'originalPrice' | 'price' | 'sale' | 'specs' | 'pic' | 'productId' | 'num' | 'productSpecNames' | 'productSku'
// >
