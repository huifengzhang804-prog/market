export interface ApiSecondSKillGoods {
  productImage: string
  productId: string
  productName: string
  price: string
  productStock: string
  secKillId: string
  minPrice: string
  shopId: string
  stockStatus: SecondsKillGoodsJointType
  robbed: string
}
type SecondsKillGoodsJointType = keyof typeof SECONDS_KILL_GOODS_STATUS
/**
 * @description: 活动商品状态
 * @param NOT_STARTED 未开始
 * @param PROCESSING 进行中
 * @param OVER 已结束
 * @param BUY_NOW 立即抢购
 * @param OUT_OF_STOCK 已抢光
 */
export enum SECONDS_KILL_GOODS_STATUS {
  NOT_START = 'NOT_START',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED',
  OFF_SHELF = 'OFF_SHELF',
  VIOLATION_OFF_SHELF = 'VIOLATION_OFF_SHELF',
}

interface ProductItem {
  productId: string
  shopId: string
  pic: string
  productName: string
  salePrices: number[]
  prices: number[]
}

export interface ProductResponse {
  code: number
  data: {
    list: ProductItem[]
  }
  msg: string
}

interface Coupon {
  id: string
  rowNum: number
  shopId: string
  sourceDesc: string
}

interface MemberInfo {
  memberType: string
}

interface ProductLabel {
  backgroundColor: string
  fontColor: string
  name: string
  shopId: number
  shopType: string[]
}

interface ProductItemType {
  categoryFirstId: string
  categorySecondId: string
  categoryThirdId: string
  couponList: Coupon[]
  createTime: string
  distributionMode: string[]
  freightTemplateId: number
  id: string
  isDistributed: boolean
  memberInfo: MemberInfo
  pic: string
  platformCategoryFirstId: string
  platformCategorySecondId: string
  platformCategoryThirdId: string
  prices: number[]
  productId: string
  productLabel: ProductLabel
  productName: string
  saleDescribe: string
  salePrices: number[]
  salesVolume: number
  sellType: string
  shopId: string
  shopName: string
  shopType: string
  skuIds: string[]
  specs: string[]
  status: string
  stockTypes: string[]
  stocks: number[]
  widePic: string
}

interface Pagination {
  endRow: number
  firstPage: boolean
  hasNextPage: boolean
  hasPreviousPage: boolean
  lastPage: boolean
  list: ProductItemType[]
  navigateFirstPage: number
  navigateLastPage: number
  navigatePageNums: number[]
  navigatePages: number
  nextPage: number
  pageNum: number
  pageSize: number
  pages: number
  prePage: number
  size: number
  startRow: number
  total: number
}

export interface productListInterface {
  code: number
  data: Pagination
  msg: string
}
