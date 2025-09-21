import { Stock } from '@/components/q-choose-goods-popup/types'
import { doGetFreightCalculation } from './apis'
import { ElMessage } from 'element-plus'
import { Long } from '@/apis/good'

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}
export interface SkuItem {
  limitType?: keyof typeof Limit
  productId: string
  skuId: string
  skuName?: string
  skuPrice: string
  skuStock: number | string
  stockType: 'UNLIMITED' | 'PRODUCT_LIMITED' | 'SKU_LIMITED' | 'LIMITED'
}
enum GROUP_ORDER_STATUS {
  ING,
  SUCCESS,
  FAIL,
}
enum GROUP_MODE {
  COMMON,
  STAIRS,
}
enum GROUP_STATUS {
  OPENING,
  PREHEAT,
  OPEN,
  FINISHED,
  VIOLATION,
}
export type ApiGroupOrderStatus = keyof typeof GROUP_ORDER_STATUS
export type ApiGroupMode = keyof typeof GROUP_MODE
export type ApiGroupStatus = keyof typeof GROUP_STATUS
/**
 * 分页查询拼团活动
 */
export interface ApiGroupList {
  id: string
  status: ApiGroupStatus
  name: string
  startTime: string
  endTime: string
  productNum: number
  users?: number
  orders: number
  amount?: string
  shopId: Long
  rejectReason?: boolean
}
export interface ApiSearchParam {
  current: number
  size: number
  endDate?: string
  startDate?: string
  status?: string
}
/**
 * 拼团订单接口
 * @param activityId 活动id
 * @param teamNo 团号
 * @param orderNo 订单号
 * @param commanderId 团长用户id
 * @param commanderAvatar 团长头像
 * @param commanderNickname 团长用户昵称
 * @param productName 商品名称
 * @param productImage 商品图片
 * @param openTime 开团时间
 * @param currentNum 当前参团人数
 * @param totalNum 成团总人数
 * @param amount 订单总金额
 */
export interface ApiGroupOrderItem {
  activityId: string
  teamNo: string
  orderNo: string
  commanderId: string
  commanderAvatar: string
  commanderNickname: string
  productName: string
  productImage: string
  openTime: string
  currentNum: number
  totalNum: number
  amount: string
  status: ApiGroupOrderStatus
}
/**
 * 拼团表单
 * @param name 拼团活动名称
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
 * @param stackable  优惠叠加
 * @param products  拼团绑定的商品列表，最少1个最多五个商品
 */
export interface ApiGroupForm {
  name: string
  startTime: string
  endTime: string
  effectTimeout: number
  mode: ApiGroupMode
  users: number[]
  payTimeout: number
  simulate: boolean
  huddle: boolean
  stackable: {
    vip: boolean
    coupon: boolean
    full: boolean
  }
  products: ApiGroupProducts[]
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
/**
 * 拼团活动查询参数
 */
export interface ApiGroupOrderParams {
  status: ApiGroupOrderStatus
  commander: string
  productName: string
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
export interface ApiGroupActivity {
  name: string
  commanderId: string
  commanderAvatar: string
  commanderNickname: string
  openTime: string
  effectTimeout: number
  currentNum: number
  totalNum: number
  productId: string
  productName: string
  buyNum: number
  orderNo: string
  shopId: string
  productImage: string
  status: ApiGroupOrderStatus
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
  price: string
  userId: string
}

/**
 * 商品sku规格table
 * @param initSalesVolume 初始销量
 */
export interface ApiGoodSkus {
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
  specs: string[]
  salesVolume: string
  secKillPrice?: string // 秒杀价格
}

export const packageStatusHandle: Record<keyof typeof PACKAGESTATUS, any> = {
  WAITING_FOR_DELIVER: { desp: '待发货', steps: 2 },
  WAITING_FOR_RECEIVE: { desp: '等待收货', color: '#71B247', track: true, Confirmreceipt: true, steps: 3 },
  BUYER_WAITING_FOR_COMMENT: { desp: '交易成功', color: '#ccc', track: true, evaluate: true, steps: 4 },
  SYSTEM_WAITING_FOR_COMMENT: { desp: '交易成功', color: '#ccc', track: true, evaluate: true, steps: 4 },
  BUYER_COMMENTED_COMPLETED: { desp: '交易成功', color: '#ccc', track: true, steps: 4 },
  SYSTEM_COMMENTED_COMPLETED: { desp: '交易成功', color: '#ccc', track: true, steps: 4 },
}
/**
 * 包裹状态
 * @param  WAITING_FOR_DELIVER 待发货
 * @param WAITING_FOR_RECEIVE  待收货
 * @param BUYER_WAITING_FOR_COMMENT 买家确认收货 待评价
 * @param SYSTEM_WAITING_FOR_COMMENT 系统确认收货 待评价
 * @param BUYER_COMMENTED_COMPLETED 买家已评论 已完成
 * @param SYSTEM_COMMENTED_COMPLETED 系统自动好评 已完成
 */
export enum PACKAGESTATUS {
  WAITING_FOR_DELIVER,
  WAITING_FOR_RECEIVE,
  BUYER_WAITING_FOR_COMMENT,
  SYSTEM_WAITING_FOR_COMMENT,
  BUYER_COMMENTED_COMPLETED,
  SYSTEM_COMMENTED_COMPLETED,
}
/**
 * 整理运费所需参数
 */
export function formatGetFreightParams(receiverAreaCode: string[], shopId: string, weight: number, skuId: string, price: string) {
  return {
    receiverAreaCode,
    shopFreights: [{ shopId, freights: [{ skuInfos: [{ skuId, weight, price, num: 1 }] }] }],
  }
}
/**
 * 获取运费
 */
export async function GetFreightCalculation(params: {
  receiverAreaCode: string[]
  shopFreights: { shopId: string; freights: { skuInfos: { skuId: string; weight: number; price: string; num: number }[] }[] }[]
}) {
  let freight = ''
  const { data: res, code, msg } = await doGetFreightCalculation(params)
  if (code !== 200) {
    ElMessage.error(msg || '运费获取失败')
    return freight
  }
  for (const key in res) {
    freight = res[key]
  }
  return freight
}
export interface ApiGoodType {
  albumPics: string | string[]
  categoryId: string
  createTime: string
  platformCategoryId: string
  detail: string
  name: string
  id: string
  pic: string
  saleDescribe: string
  openSpecs: boolean
  score: number
  status: string
  weight: number
  sale: number
  providerId: string
  videoUrl: string
  serviceIds: ServicesType[]
  productAttributes: ApiGoodAttr[]
  productCategory: ApiGoodCategory
  shopId: string
  salePrices?: string[]
  remainingStock: string
  freightTemplateId: string
}
enum Services {
  /**
   * 全场包邮
   */
  NO_FREIGHT,
  /**
   * 7天退换
   */
  SEVEN_END_BACK,
  TWO_DAY_SEND,
  THREE_DAY_SEND,
  SEVEN_DAY_SEND,
  FIFTEEN_DAY_SEND,
  /**
   * 假一赔十
   */
  FAKE_COMPENSATE,
  /**
   * 正品保证
   */
  ALL_ENSURE,
}
export type ServicesType = keyof typeof Services
type ApiGoodAttr = Record<'id' | 'name' | 'productId' | 'content', string>
export interface ApiProductSpecs {
  id: string
  name: string
  children: ApiProductSpecs[]
}
/**
 * 商品分类图片
 * @param {string} level LEVEL_3 LEVEL_2 LEVEL_1
 */
interface ApiGoodCategory {
  categoryImg: string
  id: string
  level: string
  name: string
  parentId: string
}
export interface ApiGoodSkuCombination {
  skus: ApiGoodSkus[]
  specGroups: ApiProductSpecs[]
}
