import type { Ref, ComputedRef } from 'vue'
import type { Fn } from '@/utils/types'
import type { ActivityDispatcherType } from '@plugin/types'

enum GROUP_MODE {
  COMMON,
  STAIRS,
}
export enum GROUP_STATUS {
  NO = '',
  OPENING = '即将开始',
  FINISHED = '已结束',
  OPEN = '参团',
}
enum GROUP_ACTIVITY_STATUS {
  ING,
  SUCCESS,
  FAIL,
}
type ApiGroupMode = keyof typeof GROUP_MODE
export type ApiGroupStatus = keyof typeof GROUP_STATUS
/**
 * 拼团表单
 * @param activityId 拼团活动id
 * @param startDate 开始时间 2023-03-10
 * @param endDate 结束时间
 * @param effectTime 拼团有效时间
 * @param mode 拼团有效时间
 * @param users 参团人数
 * @param payTimeout 支付超时时间
 * @param simulate  是否开启模拟成团
 * @param huddle  是否开启凑团模式
 * @param preheat  是否开启预热
 * @param preheatHours  预热开启时间，单位小时，24小时内
 * @param stackable  叠加优惠
 * @param products  拼团绑定的商品列表，最少1个最多五个商品
 */
export interface ApiGroupInfo {
  activityId: string
  startTime: string
  endTime: string
  effectTimeout: number
  mode: ApiGroupMode
  // payTimeout: number
  // simulate: boolean
  huddle: boolean
  preheat: boolean
  preheatHours: number
  stackable: {
    payTimeout: number
    vip: boolean
    coupon: boolean
    full: boolean
  }
  teamStatus: ApiGroupStatus
  skus: ApiGroupProductSku[]
  users: number[]
  myTeams: ApiGroupUserTeam[]
}
interface ApiGroupUserTeam {
  activityId: string
  amount: string
  commanderAvatar: string
  commanderId: string
  commanderNickname: string
  currentNum: number
  openTime: string
  orderNo: string
  productImage: string
  productName: string
  status: keyof typeof GROUP_ACTIVITY_STATUS
  teamNo: string
  totalNum: number
}
interface ApiGroupProductSku {
  skuId: Long
  stock: string
  prices: string[]
}
/**
 * 拼团列表类型
 * @param teamNo 团号
 * @param commanderId 团长用户id
 * @param commanderAvatar 团长头像
 * @param commanderNickname 发起人用户昵称
 * @param openTime 开团时间
 * @param effectTimeout 拼团有效时间(分钟)
 * @param currentNum 当前参团人数
 * @param totalNum 成团总人数
 */
export interface ApiGroupListType {
  teamNo: string
  commanderId: string
  commanderAvatar: string
  commanderNickname: string
  openTime: string
  effectTimeout: number
  currentNum: number
  totalNum: number
  canJoin: boolean
}
/**
 * 拼团人员信息
 */
export interface ApiGroupUser {
  amount: string
  avatar: string
  commander: boolean
  createTime: string
  nickname: string
  orderNo: string
  price: Long
  userId: string
}

export interface ApiGroupActivity {
  name: string
  commanderId: string
  commanderAvatar: string
  commanderNickname: string
  openTime: string
  effectTimeout: number
  currentNum: number
  totalNum: number
  productId: Long
  productName: string
  buyNum: number
  orderNo: string
  shopId: Long
  status: keyof typeof GROUP_ACTIVITY_STATUS
}
export type RequestGroupListParams = {
  shopId: Long
  goodId: Long
  current?: number
  size?: number
}
type RequestGroupListReturnVal = {
  current: number
  size: number
  pages: number
  records: ApiGroupListType[]
}
/**
 * 二级拼团列表
 */
export interface ApiGroupAndComItem {
  activityId: string
  activityName: string
  endTime: string
  minPrice: string
  price: Long
  productId: Long
  productImage: string
  productName: string
  shopId: Long
  startTime: string
  status: ApiGroupStatus
  stock: string
  userNum: number
}

export interface GroupDispatcherType extends ActivityDispatcherType {
  groupInfo: Ref<ApiGroupInfo>
  groupList: Ref<ApiGroupListType[]>
  existGroup: ComputedRef<boolean>
  isJoinForGroup: ComputedRef<boolean>
  groupIndex: Ref<number>
  doRequest: Fn
  requestGroupInfo: (shopId: Long, goodId: Long) => Promise<any>
  requestGroupList: (params: RequestGroupListParams) => Promise<RequestGroupListReturnVal>
}
