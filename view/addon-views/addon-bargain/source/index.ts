/**
 * 添加砍价活动
 * @param shopId 店铺id
 * @param shopName 店铺名称
 * @param name 砍价活动名称
 * @param startTime 活动开始日期
 * @param endTime 活动结束日期
 * @param bargainingPeople 砍价人数
 * @param bargainValidityPeriod 砍价有效期
 * @param isSelfBargain 是否自我砍价
 * @param userType 用户类型 UNLIMITED 不限 NEW_USER 新用户
 * @param activityPreheat 活动预热时间
 * @param stackable 是否可优惠叠加（会员，优惠券，满减） coupon vip full
 * @param status  NOT_STARTED = '未开始',PROCESSING = '进行中', OVER = '已结束',ILLEGAL_SELL_OFF = '违规下架',
 * @param helpCutAmount RANDOM_BARGAIN = '随机砍价',FIX_BARGAIN = '固定砍价',
 * @param bargainProducts  砍价活动商品
 * @param productNum 砍价活动数量
 */
export interface DoPostAddBargainQuery {
  shopId: string
  shopName: string
  name: string
  startTime: string
  endTime: string
  bargainingPeople: number
  bargainValidityPeriod: number
  isSelfBargain: boolean
  // userType: keyof typeof UserType
  // activityPreheat: number
  stackable: {
    coupon: boolean
    vip: boolean
    full: boolean
  }
  status: keyof typeof AddBargainStatus
  helpCutAmount: keyof typeof HelpCutAmount
  bargainProducts: {
    activityId: string
    productId: string
    productPic: string
    productName: string
    skuId: string
    skuStock: number
    stock: number
    skuName: string
    floorPrice: number | string
    skuPrice: number | string
    stockType: 'UNLIMITED' | 'LIMITED' | 'PRODUCT_LIMITED' | 'SKU_LIMITED'
  }[]
  productNum: number
}
export interface DoGetAddBargainQuery {
  status: DoPostAddBargainQuery['status']
  keyword: string
  current: number
  shopId: string
  total: number
  size: number
}
// 砍价活动列表
export interface DoGetBargainListResponse {
  endTime: string
  id: string
  name: string
  payOrder: number
  peopleNum: number
  productNum: number
  shopId: string
  amountReceivable: string
  startTime: string
  status: keyof typeof AddBargainStatus
  shopName: string
}
enum UserType {
  UNLIMITED = '不限',
  NEW_USER = '新用户',
}
export enum AddBargainStatus {
  NOT_STARTED = '未开始',
  PROCESSING = '进行中',
  OVER = '已结束',
  ILLEGAL_SELL_OFF = '违规下架',
  SHOP_SELL_OFF = '已下架',
}

enum HelpCutAmount {
  RANDOM_BARGAIN = '随机砍价',
  FIX_BARGAIN = '固定砍价',
}
/**
 * @param BARGAINING: 砍价中
 * @param FAILED_TO_BARGAIN: 砍价失败
 * @param SUCCESSFUL_BARGAIN: 砍价成功
 */
enum BARGAIN_SPONSOR_SKU_STATUS {
  BARGAINING = 'BARGAINING',
  FAILED_TO_BARGAIN = 'FAILED_TO_BARGAIN',
  SUCCESSFUL_BARGAIN = 'SUCCESSFUL_BARGAIN',
}
export interface FlatGood {
  floorPrice: number
  isJoin: boolean
  productId: string
  productName: string
  productPic: string
  rowTag: number
  stock: number
  skuItem: SkuItem
}
export interface SkuItem {
  productId: string
  skuId: string
  skuName: string
  skuPrice: string
  skuStock: number
  stockType: 'UNLIMITED' | 'LIMITED' | 'PRODUCT_LIMITED' | 'SKU_LIMITED'
}
export interface BargainOrderList {
  activityId: string
  bargainStatus: keyof typeof BARGAIN_SPONSOR_SKU_STATUS
  activityName: string
  bargainingPeople: number
  createTime: string
  endTime: string
  floorPrice: string
  id: string
  productId: string
  productName: string
  productPic: string
  publishBargainingTime: string
  salePrice: string
  shopId: string
  skuId: string
  sponsorId: string
  updateTime: string
  userHeadPortrait: string
  userNickname: string
  orderNo?: string
}
export interface BargainHelpPeopleList {
  activityId: string
  helpCutAmount: string
  helpCutTime: string
  shopId: string
  userHeadPortrait: string
  userId: string
  userNickName: string
}

export interface ApiGroupProducts {
  productId: string
  skus: ApiGroupProductSku[]
}
export interface ApiGroupProductSku {
  skuId: string
  stock: number
  prices: number[] | string[]
}

export type ApiGroupMode = keyof typeof GROUP_MODE
export enum GROUP_MODE {
  COMMON,
  STAIRS,
}

/**
 * 拼团商品table类型
 */
export interface FlatGoodRetrieve {
  skuItem: SkuItem
  rowTag: number
  stock: number
  prices: number[] | string[]
  isJoin: boolean
  productId: string
  productName: string
  productPic: string
}
