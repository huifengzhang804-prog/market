import type { SecondsKillJointType } from '@/pluginPackage/seckill/components/types'
/**
 * @description: 活动商品状态
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
type SecondsKillGoodsJointType = keyof typeof SECONDS_KILL_GOODS_STATUS
export interface ApiSecondSKill {
  secKillStatus: SecondsKillJointType
  startTime: string
}
export interface ApiSecondSKillGoods {
  productId: string
  productName: string
  productPic: string
  productStock: string
  secKillId: string
  secKillPrice: string
  shopId: string
  stockStatus: SecondsKillGoodsJointType
  robbed: string
}
