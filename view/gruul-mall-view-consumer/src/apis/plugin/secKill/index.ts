import api from '@/libs/request'
import type { SeckillRoundPageDTO, SeckillRoundProductPageDTO, SeckillRoundProductVO, SeckillRoundVO } from './model'
import type { Pagination, PageParam } from '@/utils/types'

/**
 * 分页查询秒杀场次信息
 * @param data 分页查询条件
 */
export const doGetSeckillRounds = (data: SeckillRoundPageDTO) => {
  return api.post<Pagination<SeckillRoundVO[]>, PageParam>('addon-seckill/seckill/activity/round/page', data)
}

/**
 * 根据场次开始时间 分页查询商品列表
 */
export const doGetSeckillProductsOfRound = (data: SeckillRoundProductPageDTO) => {
  return api.post<Pagination<SeckillRoundProductVO[]>, SeckillRoundProductPageDTO>('addon-seckill/seckill/activity/round/product/page', data)
}
