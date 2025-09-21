import { get, post } from '@/apis/http'
import { Long, OffShelfDTO, SeckillActivityDTO, SeckillDetailParam, SeckillQuery } from './index'

const BASE_URL = 'addon-seckill/seckill/activity'
/**
 * 店铺批量删除/单个删除秒杀活动
 */
export const doDelShopSeckill = (data: Long[]) => {
  return post({
    url: BASE_URL + '/delete',
    data,
  })
}
/**
 * 秒杀活动列表
 */
export const doGetsecKillList = (data: SeckillQuery): Promise<any> => {
  return post({
    url: `${BASE_URL}/page`,
    data,
  })
}
/**
 * 商家秒杀活动下架
 */
export const doPutsecKillOff = (data: OffShelfDTO): Promise<any> => {
  return post({
    url: `${BASE_URL}/off/shelf`,
    data,
  })
}

/**
 * 查询可用的秒杀场次
 */
export const doGetSeckillRound = (data: { date: string }) => {
  return post({
    url: BASE_URL + '/round',
    data: data,
  })
}

/**
 * 添加秒杀活动
 * @param {any} data update true 编辑 false 添加
 */
export const doPostAddonSeckill = (data: SeckillActivityDTO) => {
  return post({
    url: BASE_URL,
    data: data,
  })
}
/**
 * 查看秒杀活动
 */
export const doGetSeckillInfo = (data: SeckillDetailParam) => {
  return post({
    url: BASE_URL + '/detail',
    data,
  })
}

/**
 * 查询秒杀场次(装修)
 */
export const doGetSecKillSessions = (shopId: string) => {
  return get({
    url: `addon-seckill/seckillPromotion/notStartedAndProcessing?size=1000&shopId=${shopId}`,
  })
}
/**
 * 根据时间查询秒杀商品
 */
export const doGetGoodsBySessions = (params: any) => {
  return get({
    url: `addon-seckill/seckillPromotion/secKillProduct`,
    params,
  })
}

/**
 * 查询秒杀场次 查店铺传递shopid
 */
export const doGetSecondsKill = (params?: any) => {
  return get({ url: `${BASE_URL}consumer/sessions`, params })
}
/**
 * 分页查询秒杀场次信息
 * @param data 分页查询条件
 */
export const doGetSeckillRounds = (data: any): Promise<any> => {
  return post({ url: 'addon-seckill/seckill/activity/round/page', data })
}
/**
 * 根据场次开始时间 分页查询商品列表
 */
export const doGetSeckillProductsOfRound = (data: any): Promise<any> => {
  return post({
    url: 'addon-seckill/seckill/activity/round/product/page',
    data,
  })
}
/**
 * 根据秒杀场次查询秒杀场次的商品 查店铺传递shopid
 */
export const doGetSeckillSessionsProduct = (params?: any) => {
  return get({ url: `${BASE_URL}consumer/seckillSessionsProduct`, params })
}
/**
 * 秒杀活动设置
 */
export const doPostActivitySetUp = (data?: any) => {
  return post({
    url: `addon-seckill/common/config/seckill`,
    data: { duration: data },
  })
}

/**
 * 秒杀活动
 */
export const doGetActivity = (data?: any) => {
  return get<any>({
    url: `addon-seckill/common/config/seckill`,
  })
}
