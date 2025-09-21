import type { ApiGoodSku, ProductResponse } from '@/pluginPackage/goods/commodityInfo/types'
import type { productAttribute } from '@/apis/o2oshop/model'
import type { DistributionKeyType } from '@/apis/good/model'
import type { ShopMode } from '@/constant/global'

/**
 * 商品规格类型
 * @param {Long} productId 商品ID
 * @param {string} productName 商品名称
 * @param {string} id 商品规格ID
 * @param {string} image 商品规格图片
 * @param {number} price 商品规格单价
 * @param {number} num 商品规格数量
 * @param {string[]} specs  商品规格数量
 */
interface GoodItemSpec {
  productId: Long
  productName: string
  id: Long
  image: string
  price: Long
  productImage?: string
  salePrice: Long
  num: number
  specs: string[]
  freightTemplateId: string
  skuStock: ApiGoodSku
  finalPrice?: string
}
// enum GoodFailReason {
//     店铺不可用 = '11001',
//     商品不可用 = '11002',
//     sku不可用 = '11003',
//     超库存 = '11004',
//     超限购 = '11005',
// }
/**
 * 商品类型
 * @param {string} editedProductName 商品名称
 * @param {string} editedImage 商品图片
 * @param {string} editedPrice 商品价格
 * @param {GoodItemSpec} editedSpecs 商品规格
 * @param {string} reason 商品失效原因
 * @param {string} needUpdateNum 是否超库存
 */
export interface GoodItemType extends GoodItemSpec {
  editedProductName?: string
  editedImage?: string
  editedPrice?: string
  editedSpecs?: string[]
  reason?: number
  isChecked: boolean
  needUpdateNum: boolean
  skuId: Long
  weight: string | number
  isCountNumberComponentShow?: boolean
  distributionMode: ProductResponse['distributionMode']
  swipeAction: boolean
  productAttributes: productAttribute[]
  uniqueId: string
  sellType: keyof typeof SellTypeEnum
  supplierId?: Long
  slide?: boolean
}
/**
 * 购物车商品列表类型
 * @param {Long} shopId
 * @param {string} shopName
 * @param {GoodItemType[]} products
 */
export interface GoodListType {
  shopId: Long
  shopName: string
  products: GoodItemType[]
  isAllChecked: boolean
  enable: boolean
  shopLogo: string
  mode: keyof typeof ShopMode
  address: string
  contractNumber: string
  supplierId?: Long
  sellType: keyof typeof SellTypeEnum
}

enum SellTypeEnum {
  CONSIGNMENT = '代销商品',
  PURCHASE = '采购商品',
  OWN = '自有商品',
}

type ProductsPickItem = 'id' | 'image' | 'price' | 'num' | 'productId' | 'productName' | 'specs' | 'skuId' | 'freightTemplateId' | 'salePrice'
export type StorageProducts = Pick<GoodItemType, ProductsPickItem> & { weight: string | number } & {
  distributionMode: DistributionKeyType[]
  productFeaturesValue: productAttribute[]
  sellType: string
  supplierId?: Long
}
type StorageShop = Pick<GoodListType, 'shopId' | 'shopLogo' | 'shopName' | 'mode' | 'address' | 'contractNumber'>
export type StoragePackage = {
  activityParam?: { activityId: string; extra: any; type: 'SPIKE' | 'TEAM' | 'BARGAIN' | 'PACKAGE' }
  products: StorageProducts[]
  distributionMode: DistributionKeyType
} & StorageShop
// export type ChoosedSpecType = Pick<
//     ApiGoodSkuType,
//     'id' | 'originalPrice' | 'price' | 'sale' | 'specs' | 'pic' | 'productId' | 'num' | 'productSpecNames' | 'productSku'
// >
