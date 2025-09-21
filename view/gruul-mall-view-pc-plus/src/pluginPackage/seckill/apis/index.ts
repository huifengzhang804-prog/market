import { get, post, put, del, patch } from '@/apis/http'
const BASE_URL = 'addon-seckill/seckillPromotion/consumer/'
/**
 * @description: 查询秒杀场次 查店铺传递shopid
 * @param {any} params
 * @returns {*}
 */
export const doGetSecondsKill = (params?: any) => {
  return get({ url: `${BASE_URL}sessions`, params })
}
/**
 * 分页查询秒杀场次信息
 * @param data 分页查询条件
 */
export const doGetSeckillRounds = (data: any) => {
  return post({ url: 'addon-seckill/seckill/activity/round/page', data })
}
/**
 * 根据场次开始时间 分页查询商品列表
 */
export const doGetSeckillProductsOfRound = (data: any): Promise<any> => {
  return post({ url: 'addon-seckill/seckill/activity/round/product/page', data })
}
/**
 * @description: 根据秒杀场次查询秒杀场次的商品 查店铺传递shopid
 * @param {any} params
 * @returns {*}
 */
export const doGetSeckillSessionsProduct = (params?: any) => {
  return get({ url: `${BASE_URL}seckillSessionsProduct`, params })
}
/**
 * @description: 判断当前商品是否参加秒杀活动
 * @param {string} shopId
 * @param {string} productId
 * @returns {*}
 */
export const doGetSeckillConsumerByShopId = (shopId: string, productId: string) => {
  return get({ url: `${BASE_URL}${shopId}/${productId}` })
}
/**
 * @description: 获取商品详情页秒杀活动商品信息
 * @param {string} secKillId
 * @param {string} productId
 * @returns {*}
 */
export const doGetSeckillProductDetailBySecKillIdd = (secKillId: string, productId: string, shopId: string) => {
  return get({ url: `${BASE_URL}secKillProductDetail/${secKillId}/${productId}`, headers: { 'Shop-Id': shopId } })
}
/**
 * @description: 获取对应场次的商品(装修)
 * @returns {*}
 */
export const doGetSeckillGoods = (data: any) => {
  return post({ url: 'addon-seckill/seckillPromotion/consumer/showPlatformSecKillSessions', data })
}
