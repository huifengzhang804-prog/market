import type { ComputedRef, Ref } from 'vue'
import type { ActivityDispatcherType } from '@plugin/types'
import type { Nullable, RageTime } from '@/constant/global'
import type { PageParam } from '@/utils/types'

/**
 * 商品详情页秒杀活动商品信息
 * @param startTime
 * @param endTime
 * @param productId
 * @param secKillId
 */
export interface ApiSeckILLGoodsDetails {
  startTime: string
  endTime: string
  productId: Long
  secKillId: Long
}

export interface SecDispatcherType extends ActivityDispatcherType {
  initGetSeckillConsumerByShopId: (_: any, goodsInfo: any) => Promise<void>
  isJoinSecKill: ComputedRef<boolean>
  secKillGoodsInfo: Ref<ApiSeckILLGoodsDetails>
  whetherAMemberDiscount: ComputedRef<boolean>
}

/**
 * @param  goodList 商品列表id
 * @param  display 展示方式 1为横向滚动 2详细列表
 * @param  goodStyle 商品样式 1无边描底 2卡片投影 3 描边白底
 * @param  border 图片倒角
 * @param  tagStyle 1 新品 2热卖 3抢购
 * @param  sessionId 场次ID
 * @param  seckillTime 秒杀时间
 * @param  textStyle 1常规体 2加粗体
 */
export interface SeckillDataType {
  goodList: seckillGoodType[]
  display: number
  padding: number
  marginBottom: number
  goodStyle: number
  border: boolean
  showtag: boolean
  tagStyle: number
  sessionId: string
  seckillTime: string
  seckillEndTime: string
  textStyle: number
}

type seckillGoodType = {
  maxPrice: string
  minPrice: string
  productId: Long
  productName: string
  productPic: string
  shopId: Long
}

/**
 * 秒杀状态
 */
export enum SeckillQueryStatus {
  //未开始
  NOT_START = 'NOT_START',
  //进行中
  IN_PROGRESS = 'IN_PROGRESS',
  //已结束
  FINISHED = 'FINISHED',
  //已下架
  OFF_SHELF = 'OFF_SHELF',
  //违规下架
  VIOLATION_OFF_SHELF = 'VIOLATION_OFF_SHELF',
}

export const SeckillStatus: Record<
  SeckillQueryStatus,
  {
    //状态描述
    desc: string
    //倒计时器描述
    timerDesc: string
    //计时器计算的时间生成器
    timePick: (round: SeckillRoundVO) => string
    //购买按钮描述
    buyButtonText: string
    //购买按钮样式类
    buyButtonClass: string
  }
> = {
  [SeckillQueryStatus.NOT_START]: {
    desc: '未开始',
    timerDesc: '距本场开始',
    timePick: (round) => round.time.start,
    buyButtonText: '即将开抢',
    buyButtonClass: 'snap-up',
  },
  [SeckillQueryStatus.IN_PROGRESS]: {
    desc: '进行中',
    timerDesc: '本场还剩',
    timePick: (round) => round.time.end,
    buyButtonText: '马上抢',
    buyButtonClass: 'snapped-up-immediately',
  },
  [SeckillQueryStatus.FINISHED]: {
    desc: '已结束',
    timerDesc: '',
    timePick: () => '',
    buyButtonText: '已结束',
    buyButtonClass: '',
  },
  [SeckillQueryStatus.OFF_SHELF]: {
    desc: '已下架',
    timerDesc: '',
    timePick: () => '',
    buyButtonText: '已下架',
    buyButtonClass: '',
  },
  [SeckillQueryStatus.VIOLATION_OFF_SHELF]: {
    desc: '违规下架',
    timerDesc: '',
    timePick: () => '',
    buyButtonText: '已下架',
    buyButtonClass: '',
  },
}

export interface SeckillRoundPageDTO extends PageParam {
  //店铺 id 可选是否指定店铺 id查询
  shopId?: Nullable<Long>
}

/**
 * 活动场次信息
 */
export interface SeckillRoundVO {
  //场次时间范围
  time: RageTime<string>
  //该场次状态
  status: SeckillQueryStatus
}

/**
 * 场次商品分页查询条件
 */
export interface SeckillRoundProductPageDTO extends PageParam {
  //店铺 id 可选是否指定店铺 id查询
  shopId?: Nullable<Long>
  //场次开始时间
  start: string
}

/**
 * 场次商品数据类型
 */
export interface SeckillRoundProductVO {
  //店铺 id
  shopId: Long
  //活动 id
  activityId: Long
  //商品 id
  productId: Long
  //商品名称
  productName: string
  //商品主图
  productImage: string
  //秒杀最低价
  minPrice: Long
  //活动库存
  activityStock: Long
}
