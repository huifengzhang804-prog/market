export type Nullable<T> = T | null | ''
export type Long = number | string

export interface PageParams {
  current: number
  size: number
  total: number
}

export interface SearchParam {
  name: Nullable<string>
  status: Nullable<Status>
}

// 活动状态
export enum Status {
  /**
   * 正常状态
   */
  NOT_STARTED = 'NOT_STARTED',

  /**
   * 进行中
   */
  IN_PROGRESS = 'IN_PROGRESS',

  /**
   * 活动结束
   */
  FINISHED = 'FINISHED',

  /**
   * 违规下架
   */
  OFF_SHELF = 'OFF_SHELF',

  /**
   * 店铺下架
   */
  VIOLATION_OFF_SHELF = 'VIOLATION_OFF_SHELF',
}

interface StatusMap {
  value: Status
  label: string
  style: string
}

export const AllStatus: Record<Status, StatusMap> = {
  [Status.NOT_STARTED]: {
    value: Status.NOT_STARTED,
    label: '未开始',
    style: 'nots',
  },
  [Status.IN_PROGRESS]: {
    value: Status.IN_PROGRESS,
    label: '进行中',
    style: 'ongoing',
  },
  [Status.FINISHED]: {
    value: Status.FINISHED,
    label: '已结束',
    style: 'hasEnded',
  },
  [Status.OFF_SHELF]: {
    value: Status.OFF_SHELF,
    label: '已下架',
    style: 'off',
  },
  [Status.VIOLATION_OFF_SHELF]: {
    value: Status.VIOLATION_OFF_SHELF,
    label: '违规下架',
    style: 'off',
  },
}

///满减下架请求参数
export interface ViolationParam {
  shopId?: Nullable<Long>
  id: Long
  violation?: Nullable<string>
}

export interface PromotionFullReduction {
  //店铺 id
  shopId: Long
  shopName: string
  //活动 id
  id: Long
  //活动状态
  status: Status
  //活动名称
  name: string
  //活动开始时间
  startTime: string
  //活动结束时间
  endTime: string
  //商品类型
  productType: ProductType
  //商品数量
  product: number
  // 支付订单数
  order: Long
  // 违规原因
  violation?: string
  firstRuleDesc: string
  firstRuleType: keyof typeof FULL_REDUCTION_RULE
}

/**
 * 分页查询满减活动参数
 */
export interface DoGetApplyDiscountListParams extends PageParams, SearchParam {}

export interface RangeTime<T> {
  start: T
  end: T
}

/**
 * 编辑满减活动所需参数
 * @param {*} fullReductionId: 满减id
 * @param {*} fullReductionName: 满减活动名称
 * @param {*} fullReductionStatus: 未开始 进行中 已结束 违规下架
 * @param {*} fullReductionStartTime: 开始时间
 * @param {*} fullReductionEndTime: 结束时间
 * @param {*} fullReductionRule: 0:满减 1:满折
 * @param {*} shopId: 店铺id
 * @param {*} shopName: 店铺名称
 * @param {*} productType: 0:全部商品 1:指定商品 2:指定商品不参与
 * @param {*} conditionAmount: 满减金额/满折金额
 * @param {*} discountAmount: 优惠金额
 * @param {*} discountRatio: 折扣比 0.1-9.9
 * @param {*} productIds: 商品ids
 * @param {*} isUpdate: 是否更新
 */
export interface AddDiscountActiveParams {
  id: Nullable<string>
  name: string
  time: RangeTime<string>
  rules: FullReductionRules[]
  productType: ProductType
  productIds?: string[]
}

export interface FullReductionRules {
  type: Nullable<FullReductionRule>
  conditionAmount: number
  discountAmount?: number
  discountRatio?: number
}

export type FullReductionRule = keyof typeof FULL_REDUCTION_RULE

export type ProductType = keyof typeof PRODUCT_TYPE

export const fullReductionRuleCn = [
  {
    label: '满X元减',
    value: 'FULL_REDUCTION',
  },
  {
    label: '满X元折',
    value: 'FULL_DISCOUNT',
  },
]

export enum FULL_REDUCTION_RULE {
  '' = '未选择满减条件',
  FULL_REDUCTION = '满减',
  FULL_DISCOUNT = '满折',
}

export enum PRODUCT_TYPE {
  ALL_PRODUCT = '全部商品',
  SPECIFIED_PRODUCT_PARTICIPATE = '指定商品',
  SPECIFIED_PRODUCT_NOT_PARTICIPATE = '指定商品不参与',
}

export interface SearchType {
  fullReductionStatus: string
  keyword: string
}
