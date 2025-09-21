import type { ActivityDispatcherType } from '@plugin/types'
import type { ComputedRef, Ref } from 'vue'
import type { ActivityDetailVO, ProductResponse } from '@/apis/good/model'

/**
 * @param SUCCESSFUL_BARGAIN: 已成功
 * @param HELPED: 已帮砍
 * @param SOLD_OUT: 已售罄
 * @param END: 已结束
 * @param FAILED_TO_BARGAIN: 已失败
 * @param IN_PROGRESS: 进行中
 */
enum BARGAIN_SPONSOR_SKU_STATUS {
  SUCCESSFUL_BARGAIN = 'SUCCESSFUL_BARGAIN',
  HELPED = 'HELPED',
  SOLD_OUT = 'SOLD_OUT',
  END = 'END',
  FAILED_TO_BARGAIN = 'FAILED_TO_BARGAIN',
  IN_PROGRESS = 'IN_PROGRESS',
}
/**
 * 获取砍价好友列表 query参数
 * @param sponsorId 发起人id
 * @param shopId 店铺id
 * @param productId 商品id
 * @param activityId 活动id
 */
interface BargainHelpPeopleListQuery {
  sponsorId?: string
  shopId?: Long
  productId?: Long
  activityId?: Long
  size?: number
  current?: number
  pages?: number
}

interface BargainSponsorRes {
  activityId: BargainHelpPeopleListQuery['activityId']
  productId: BargainHelpPeopleListQuery['productId']
  shopId: BargainHelpPeopleListQuery['shopId']
  skuId: BargainSponsortQuery['skuId']
  userId: string
  bargainOrderId: string
}
/**
 * 发起砍价 query参数
 * @param shopId: 店铺id
 * @param productId: 商品id
 * @param skuId: 商品skuId
 * @param skuImage: 商品sku图片
 * @param activityId: 活动id
 * @param userHeadPortrait: 用户头像
 */
interface BargainSponsortQuery {
  shopId: BargainHelpPeopleListQuery['shopId']
  productId: BargainHelpPeopleListQuery['productId']
  skuId: Long
  skuImage: string
  activityId: BargainHelpPeopleListQuery['activityId']
  userHeadPortrait: string
  userNickname: string
}
/**
 * 帮好友砍价 query参数
 * @param sponsorId: 发起人id
 * @param shopId: 店铺id
 * @param productId: 商品id
 * @param activityId: 活动id
 * @param userHeadPortrait: 用户头像
 * @param userNickName: 用户昵称
 */
interface BargainHelpBargainQuery {
  sponsorId: BargainHelpPeopleListQuery['sponsorId']
  shopId: BargainHelpPeopleListQuery['shopId']
  productId: BargainHelpPeopleListQuery['productId']
  activityId: BargainHelpPeopleListQuery['activityId']
  userHeadPortrait: BargainSponsortQuery['userHeadPortrait']
  userNickName: string
  bargainOrderId: string
}
interface BargainSponsorProductSkuQuery extends Omit<BargainSponsortQuery, 'skuImage' | 'userHeadPortrait' | 'userNickname'> {
  sponsorId: BargainHelpBargainQuery['sponsorId']
  bargainOrderId: string
}

interface BargainHelpPeopleListRes {
  sponsorProductSku?: Obj
  ActivityDetailVO?: ActivityDetailVO
}
interface BargainPagePathQuery {
  activityId: string
  productId: Long
  shopId: Long
  skuId: Long
  bargainOrderId: string
  userId: string
}
interface BargainDispatcherType extends Omit<ActivityDispatcherType, 'getSkuIds'> {
  isBargaining: ComputedRef<boolean>
  isJoinForBargain: ComputedRef<boolean>
  bargainProductInfo: Ref<BargainHelpPeopleListRes>
  handlerBargainSponsor: (productName: string, currentSpec: any) => Promise<void>
  navigateToBargain: () => void
  goodsInfoRef: Ref<ProductResponse>
}

interface BargainSponsorProductSkuRes {
  activityId?: string
  amountCut?: string
  endTime?: string
  floorPrice?: string
  productId?: Long
  shopId?: Long
  skuId?: Long
  skuImage?: string
  skuName?: string
  skuPrice?: string
  userHeadPortrait?: string
  userId?: string
  userNickName?: string
  isSelfBargain?: boolean
  bargainOrderId?: string
  productAttributes?: any[]
  productName: string
  stackable?: {
    coupon: boolean
    full: boolean
    vip: boolean
  }
  bargainSponsorSkuStatus: keyof typeof BARGAIN_SPONSOR_SKU_STATUS
}
// 帮砍好友列表item
interface BargainHelpPeopleItemRes
  extends Pick<BargainSponsorProductSkuRes, 'activityId' | 'shopId' | 'userHeadPortrait' | 'userId' | 'userNickName'> {
  helpCutAmount: string
  helpCutTime: string
}
export {
  type BargainHelpPeopleListQuery,
  type BargainSponsortQuery,
  type BargainHelpBargainQuery,
  type BargainSponsorProductSkuQuery,
  type BargainDispatcherType,
  type BargainHelpPeopleListRes,
  type BargainSponsorRes,
  BARGAIN_SPONSOR_SKU_STATUS,
  type BargainSponsorProductSkuRes,
  type BargainHelpPeopleItemRes,
  type BargainPagePathQuery,
}
