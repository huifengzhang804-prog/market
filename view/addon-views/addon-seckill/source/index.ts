import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'
import Decimal from 'decimal.js'

/**
 * 活动状态
 * @param NOT_STARTED 未开始
 * @param PROCESSING 进行中
 * @param OVER 已结束
 * @param ILLEGAL_SELL_OFF 违规下架
 */
enum SECONDS_KILL_STATUS {
  NOT_START = 'NOT_START',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED',
  OFF_SHELF = 'OFF_SHELF',
  VIOLATION_OFF_SHELF = 'VIOLATION_OFF_SHELF',
}
export type SecondsKillJointType = keyof typeof SECONDS_KILL_STATUS

export type Long = string | number
export type Stock = 'UNLIMITED' | 'LIMITED'

/**
 * 分页参数
 */
export interface PageParams {
  //当前页码
  current: number
  //每页显示条数
  size: number
  //总数据条数
  total: number
}

//秒杀活动状态
export enum SeckillQueryStatus {
  //未开始
  NOT_START = 'NOT_START',
  // 进行中
  IN_PROGRESS = 'IN_PROGRESS',
  //已完成 已结束
  FINISHED = 'FINISHED',
  //已下架
  OFF_SHELF = 'OFF_SHELF',
  //违规下架
  VIOLATION_OFF_SHELF = 'VIOLATION_OFF_SHELF',
  ALL = '',
}

export const SeckillStatus: Record<SeckillQueryStatus, string> = {
  [SeckillQueryStatus.ALL]: '全部状态',
  [SeckillQueryStatus.NOT_START]: '未开始',
  [SeckillQueryStatus.IN_PROGRESS]: '进行中',
  [SeckillQueryStatus.FINISHED]: '已结束',
  [SeckillQueryStatus.OFF_SHELF]: '已下架',
  [SeckillQueryStatus.VIOLATION_OFF_SHELF]: '违规下架',
}

//秒杀检索条件
export interface SearchParam {
  //活动状态
  status: null | SeckillQueryStatus
  //活动名称
  name: string | string
}

//秒杀查询数据
export interface SeckillQuery extends SearchParam, PageParams {}

/**
 * 秒杀活动 列表信息
 */
export interface ApiSecondSkillItem {
  //店铺 id （平台查询时有值）
  shopId?: Long
  //店铺名称（平台查询时有值）
  shopName?: string
  //秒杀活动 id
  activityId: Long
  //活动名称时间
  name: string
  //活动开始时间
  startTime: string
  //活动结束时间
  endTime: string
  //活动状态
  status: SeckillQueryStatus
  //参与人数
  user: Long
  //支付订单数
  payOrder: Long
  //商品数
  product: Long
  //违规原因
  violation?: string
}

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}

/**
 * @param highestPrice: 最高价
 * @param lowestPrice: 最低价
 * @param productId: 商品ID
 * @param productName: 商品名称
 * @param productPic: 商品图
 * @param shopId: 店铺ID
 * @param skus: SkuItem[]
 */
export interface ApiGoodsRetrieve {
  productId: Long
  productName: string
  productPic: string
  shopId?: Long
  skus: SkuItem[]
}

export interface SkuItem {
  limitType?: keyof typeof Limit
  productId?: string
  skuId: Long
  skuName?: string[]
  skuPrice?: Long
  skuStock?: Long
  stockType?: Stock | string
  seckillLimit?: number
}

/**
 * 选择商品 sku 参数
 */
export interface SkuQuery extends SkuItem {
  seckillPrice: Long
  seckillStock: Long
  actualPaidPrice?: Long
}

export interface GoodsListItem extends Omit<ApiGoodsRetrieve, 'skus'> {
  sku: SkuQuery
}

export const secondsKillStatus = {
  NOT_START: '未开始',
  IN_PROGRESS: '进行中',
  FINISHED: '已结束',
  OFF_SHELF: '已下架',
  VIOLATION_OFF_SHELF: '违规下架',
}
/**
 * 秒杀商品
 */
export const strategyPatternHandler = {
  NOT_START: {
    color: '#333333',
    text: '即将开始',
    soldOut: false,
  },
  IN_PROGRESS: {
    color: '#e31436',
    text: '立即抢购',
    soldOut: false,
  },
  OFF_SHELF: {
    color: '#999999',
    text: '已结束',
    soldOut: false,
  },
  BUY_NOW: {
    color: '#e31436',
    text: '立即抢购',
    soldOut: false,
  },
  OUT_OF_STOCK: { color: '#999999', text: '已抢光', soldOut: true },
}

/**
 * 倒计时
 */
export const countdownStrategyPatternHandler = {
  NOT_START: {
    text: '距开始',
  },
  IN_PROGRESS: {
    text: '距结束',
  },
  FINISHED: {
    text: '距结束',
    soldOut: false,
  },
  OFF_SHELF: {
    text: '已下架',
  },
  VIOLATION_OFF_SHELF: {
    text: '违规下架',
  },
}

export interface ApiSecondSKill {
  status: SecondsKillJointType
  time: { end: string; start: string }
}

export interface ApiSecondSKillGoods {
  productId: string
  productName: string
  productImage: string
  productStock: string
  secKillId: string
  minPrice: string
  shopId: string
  stockStatus: SecondsKillGoodsJointType
  robbed: string
}

type SecondsKillGoodsJointType = keyof typeof SECONDS_KILL_GOODS_STATUS

/**
 * 活动商品状态
 * @param NOT_STARTED 未开始
 * @param PROCESSING 进行中
 * @param OVER 已结束
 * @param BUY_NOW 立即抢购
 * @param OUT_OF_STOCK 已抢光
 */
enum SECONDS_KILL_GOODS_STATUS {
  NOT_STARTED = 'NOT_STARTED',
  PROCESSING = 'PROCESSING',
  OVER = 'OVER',
  BUY_NOW = 'BUY_NOW',
  OUT_OF_STOCK = 'OUT_OF_STOCK',
}

type DecimalType = Decimal
export function useConversionPrice(price: string | number | DecimalType = 0): DecimalType {
  if (!price) price = 0
  return new Decimal(price).div(10000)
}

type UnionDate = number | Date
export default class DateUtil {
  ms: Date

  constructor(ms = new Date()) {
    this.ms = ms
  }

  /**
   * 获取年
   */
  getY(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    return GMT.getFullYear()
  }

  /**
   * 获取月
   */
  getM(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const m = GMT.getMonth() + 1
    return this.formatLength(m)
  }

  /**
   * 获取日
   */
  getD(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const d = GMT.getDate()
    return this.formatLength(d)
  }

  /**
   * 获取时
   */
  getH(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const H = GMT.getHours()
    return this.formatLength(H)
  }

  /**
   * 获取分
   */
  getMin(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const M = GMT.getMinutes()
    return this.formatLength(M)
  }

  /**
   * 获取秒
   */
  getS(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const S = GMT.getSeconds()
    return this.formatLength(S)
  }

  /**
   * 获取年/月/日
   */
  getYMD(ms: Date | number = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const y = GMT.getFullYear()
    const m = GMT.getMonth() + 1
    const d = GMT.getDate()
    return [y, m, d].map(this.formatLength).join('/')
  }

  /**
   * 获取年-月-日
   */
  getYMDs(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const y = GMT.getFullYear()
    const m = GMT.getMonth() + 1
    const d = GMT.getDate()
    return [y, m, d].map(this.formatLength).join('-')
  }

  /**
   * 获取年-月
   */
  getYMs(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const y = GMT.getFullYear()
    const m = GMT.getMonth() + 1
    return [y, m].map(this.formatLength).join('-')
  }

  /**
   * 获取月-日
   */
  getMDs(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const m = GMT.getMonth() + 1
    const d = GMT.getDate()
    return [m, d].map(this.formatLength).join('-')
  }

  /**
   * 获取时: 分: 秒
   */
  getHMS(ms: UnionDate = this.ms) {
    const GMT = this.unitReturnDate(ms)
    const h = GMT.getHours()
    const m = GMT.getMinutes()
    const s = GMT.getSeconds()
    return [h, m, s].map(this.formatLength).join(':')
  }

  /**
   * 获取年/月/日 时: 分: 秒
   */
  getYMDHMS(ms: UnionDate = this.ms) {
    ms = this.unitReturnDate(ms)
    return this.getYMD(ms) + ' ' + this.getHMS(ms)
  }

  /**
   * 获取年-月-日 时: 分: 秒
   */
  getYMDHMSs(ms: UnionDate = this.ms) {
    return this.getYMDs(ms) + ' ' + this.getHMS(ms)
  }

  /**
   * 获取上个月 格式年-月-日 时: 分: 秒
   * @param {number} day 天数
   */

  getLastMonth(ms: UnionDate = this.ms, day = 30) {
    let GMT = this.getTime(this.unitReturnDate(ms))
    GMT = GMT - 3600 * 1000 * 24 * day
    return this.getYMDs(GMT)
  }

  /**
   * 获取上个季度 格式年-月-日 // 时: 分: 秒
   * @param {number} day 天数
   */
  getLastThreeMonth(ms: Date = this.ms, day = 90) {
    let GMT = this.getTime(ms)
    GMT = GMT - 3600 * 1000 * 24 * day
    return this.getYMDs(GMT)
  }

  /**
   * 年月日加天数
   * @param {number} day 天数
   */
  getAddDays(ms: Date | number = this.ms, day = 0) {
    let GMT = this.getTime(this.unitReturnDate(ms))
    GMT = GMT + day * 24 * 60 * 60 * 1000
    const Y = this.getY(GMT)
    const M = this.getM(GMT)
    const D = this.getD(GMT)
    return [Y, M, D].map(this.formatLength).join('-')
  }

  getSubtracteDays(ms: Date | number = this.ms, day = 0) {
    let GMT = this.getTime(this.unitReturnDate(ms))
    GMT = GMT - day * 24 * 60 * 60 * 1000
    const Y = this.getY(GMT)
    const M = this.getM(GMT)
    const D = this.getD(GMT)
    return [Y, M, D].map(this.formatLength).join('-')
  }

  /**
   * 获取毫秒数
   */
  getTime(ms: Date = this.ms) {
    return ms.getTime()
  }

  getObj(ms = this.ms) {
    const GMT = ms
    const Y = GMT.getFullYear()
    const M = GMT.getMonth() + 1
    const D = GMT.getDate()
    const h = GMT.getHours()
    const m = GMT.getMinutes()
    const s = GMT.getSeconds()
    return [Y, M, D, h, m, s].map(this.formatLength)
  }

  /**
   * 格式化补零
   */
  formatLength(ms: number | string) {
    return String(ms)[1] ? String(ms) : '0' + ms
  }

  /**
   * 统一返回日期类型
   */
  unitReturnDate(ms: Date | number) {
    if (!(ms instanceof Date)) {
      return new Date(ms)
    }
    return ms
  }

  /**
   * 闰年判断
   */
  isLeapYear(year: string | number) {
    const d = new Date(Number(year), 1, 29)
    return d.getDate() === 29
  }

  /**
   * 判断当月对应天数
   */
  MonthToDay(year: string | number, month: string | number) {
    const d = new Date(Number(year), Number(month), 1, 0, 0, 0)
    const lastDay = new Date(d.getTime() - 1000)
    return lastDay.getDate()
  }
}
/**
 * 59 分 59 秒 用于计算 秒杀结束时间
 */
export const FIXED_POINT_TIME = 60 * 60 * 1000 - 1000

/**
 * 时间格式化工具（倒计时转化）
 */
export function toHHmmss(data: number) {
  let time
  let hours = parseInt(((data % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toString())
  let minutes = parseInt(((data % (1000 * 60 * 60)) / (1000 * 60)).toString())
  let seconds = parseInt(((data % (1000 * 60)) / 1000).toString())
  time = (hours < 10 ? '0' + hours : hours) + ':' + (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds)
  return time
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
  stockType: Stock
  totalStock: string
  weight: number
  specs: string[]
  salesVolume: string
  secKillPrice?: string // 秒杀价格
}

/**
 * 秒杀活动信息
 */
export interface SeckillActivityDTO {
  //秒杀活动名称
  name: string
  //活动日期 大于等于当日
  date: string
  //活动轮次 1-23 从轮次接口里渲染
  round: number | null
  //支付超时时间 单位 分 3-360 单位分
  payTimeout: number
  //叠加优惠信息
  stackable: StackableDiscount
  //活动商品列表
  products: SeckillActivityProductDTO[]

  //开始时间 不带日期 不需要提交的数据
  startTime: string
  //结束时间 不带日期 不需提交的数据
  endTime: string
}

/**
 * 活动商品信息
 */
export interface SeckillActivityProductDTO {
  //商品 id
  id: Long
  //sku 信息
  skus: SeckillActivitySkuDTO[]
}

/**
 * 活动商品 sku 信息
 */
export interface SeckillActivitySkuDTO {
  //sku Id
  id: Long
  //活动库存
  stock: Long
  //sku 规格
  specs: string
  //活动价格
  price: unknown | Long
}

/**
 * 叠加优惠信息
 */
export interface StackableDiscount {
  //是否可叠加会员优惠
  vip: boolean
  //是否可叠加优惠券优惠
  coupon: boolean
  //是否可叠加满减优惠
  full: boolean
}

/**
 * 活动场次信息
 */
export interface RoundVO {
  //活动场次值
  round: number
  //该场次开始时间
  startTime: string
  //该场次结束时间
  endTime: string
}

/**
 * 活动下架表单
 */
export interface OffShelfDTO {
  //店铺 id  违规下架时不为空
  shopId?: Long
  //秒杀活动 id
  activityId: Long
  //违规下架原因
  reason?: string
}

/**
 * 秒杀详情商品回显信息
 */
export interface SeckillProductVO {
  //商品 id
  id: Long
  //商品名称
  name: string
  //商品图片
  image: string
  //sku 信息
  skus: SeckillSkuVO[]
}

/**
 * 秒杀详情 商品 sku 信息
 */
export type SeckillSkuVO = SeckillActivitySkuDTO

/**
 * 秒杀活动详情查询条件
 */
export interface SeckillDetailParam {
  //店铺 id 非店铺内查询时不为空
  shopId?: Long
  //秒杀活动 id
  activityId: Long
}

/**
 * 秒杀活动详情响应数据
 */
export interface ApiEditSecondSKill {
  name: string
  date: string
  startTime: string
  endTime: string
  payTimeout: number
  stackable: StackableDiscount
  products: SeckillProductVO[]
}

interface SpanMethodProps {
  row: GoodsListItem
  column: TableColumnCtx<GoodsListItem>
  rowIndex: number
  columnIndex: number
}

let spanArr: number[] = []
let pos = 0
export const getSpanId = (data: GoodsListItem[]) => {
  spanArr = []
  pos = 0
  // data就是我们从后台拿到的数据
  for (let i = 0; i < data.length; i++) {
    if (i === 0) {
      //spanArr 用于存放没一行记录的合并数
      spanArr.push(1)
      //pos是spanArr的索引
      pos = 0
    } else if (data[i].productId === data[i - 1].productId) {
      spanArr[pos] += 1
      spanArr.push(0)
    } else {
      spanArr.push(1)
      pos = i
    }
  }
}
export const objectSpanMethod = ({ row, column, rowIndex, columnIndex }: SpanMethodProps) => {
  if (columnIndex === 0) {
    const _row = spanArr[rowIndex]
    //如果行号大于0 合并
    const _col = _row > 0 ? 1 : 0
    return {
      // [0,0] 表示这一行不显示，
      rowspan: _row,
      colspan: _col,
    }
  }
}
