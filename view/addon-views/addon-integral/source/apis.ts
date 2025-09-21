import { get, post, put, del, patch } from '@/apis/http'
import { IntegralOrderInfo } from './components/types/order'
const INTEGRAL = 'addon-integral/integral/'
const PRODUCT_BASE_URL = INTEGRAL + 'product/'
const ORDER_BASE_URL = INTEGRAL + 'order/'

/**
 * 积分商品发布
 */
export const doPostAddIntegralProduct = (data: any) => {
  return post({
    url: PRODUCT_BASE_URL + 'issue',
    data,
  })
}
/**
 * 积分商品列表
 */
export const doPutIntegralProductList = (params: any): Promise<any> => {
  return get({
    url: PRODUCT_BASE_URL + 'list',
    params,
  })
}
/**
 * 获取积分商品信息
 */
export const doGetIntegralProductInfo = (id: any) => {
  return get({
    url: PRODUCT_BASE_URL + 'info',
    params: {
      id,
    },
  })
}
/**
 * 积分商品修改
 */
export const doPutIntegralProductInfoUpdate = (data: any) => {
  return put({
    url: PRODUCT_BASE_URL + 'update',
    data,
  })
}
/**
 * 积分商品上下架
 */
export const doPutIntegralProductInfoUpdateStatus = (status: any, ids: string[]) => {
  return put({
    url: PRODUCT_BASE_URL + `updateStatus/${status}`,
    data: ids,
  })
}
/**
 * 积分商品删除
 */
export const doDelIntegralProductDelete = (ids: string[]) => {
  return del({
    url: PRODUCT_BASE_URL + `delete/${ids}`,
  })
}
/**
 * 积分订单信息
 */
export const doGetIntegralOrderList = (params: any): Promise<any> => {
  return get({
    url: ORDER_BASE_URL + `list`,
    params,
  })
}
/**
 * 积分规则获取
 */
export const doGetIntegralRulesInfo = (): Promise<any> => {
  return get({
    url: INTEGRAL + `rules/info`,
  })
}
/**
 * 积分规则保存
 */
export const doPostIntegralRulesInfo = (data: any) => {
  return post({
    url: INTEGRAL + `rules/save`,
    data,
  })
}
/**
 * 积分规则修改
 */
export const doPostIntegralRulesInfoUpdate = (data: any) => {
  return post({
    url: INTEGRAL + `rules/update`,
    data,
  })
}
/**
 * 查看待发货商品信息
 */
export const doGetIntegralOrderDeliverSingle = (orderNo: any): Promise<any> => {
  return get({
    url: ORDER_BASE_URL + `deliver/single/undeliver/${orderNo}`,
  })
}
/**
 * 积分订单详情 by 订单号
 */
export const doGetIntegralOrderInfoByNo = (integralOrderNo: string) => {
  return get<IntegralOrderInfo>({
    url: ORDER_BASE_URL + `get/${integralOrderNo}`,
  })
}

/**
 * 订单发货 批量发货和单独发货
 */
export const doPutIntegralOrderDeliver = (
  data: {
    integralOrderNo: string
    integralOrderDeliverType: 'WITHOUT' | 'EXPRESS' | string
    expressCompanyName?: string
    expressNo?: string
  }[],
) => {
  return put({
    url: ORDER_BASE_URL + `deliver`,
    data,
  })
}
/**
 * 订单发货 批量发货和单独发货
 */
export const doPutIntegralOrderDeliverComplete = (integralOrderNo: string) => {
  return put({
    url: ORDER_BASE_URL + `deliver/complete/${integralOrderNo}`,
  })
}
/**
 * 查看所有待发货商品 批量发货
 */
export const doGetIntegralOrderUndeliver = () => {
  return get<any>({
    url: ORDER_BASE_URL + `deliver/batch/undeliver`,
  })
}
/**
 * 会员积分调整(充值/扣除)
 */
export const doPostIntegralChange = (userId: string, integral: number, changeType: 'INCREASE' | 'REDUCE') => {
  return post({
    url: '/gruul-mall-user/user/integral/system/change',
    data: { userId, integral, changeType },
  })
}
/**
 * 获取用户积分
 */
export const doGetIntegralTotal = (userId: string) => {
  return get({
    url: 'gruul-mall-user/user/integral/system/total',
    params: { userId },
  })
}
/**
 * 获取用户积分详情列表
 */
export const doGetIntegralDetailInfo = (params: any) => {
  return get({
    url: 'gruul-mall-user/user/integral/detail/info',
    params,
  })
}
/**
 * 积分商品列表
 */
export const doGetIntegralProductList = (params: any): Promise<any> => {
  return get({ url: `${PRODUCT_BASE_URL}list`, params })
}
/**
 * 查看积分明细
 */
export const doGetUserIntegralDetailInfo = (params: any): Promise<any> => {
  return get({ url: `gruul-mall-user/user/integral/detail/info`, params })
}
/**
 * 查看剩余积分
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetUserIntegralSystemtotal = (): Promise<any> => {
  return get({ url: `gruul-mall-user/user/integral/system/total` })
}
/**
 * 积分行为天数
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetIntegralBehaviorDays = (params?: string): Promise<any> => {
  return get({ url: `${INTEGRAL}behavior/days?ruleType=${params}` })
}
/**
 * 积分行为触发
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetIntegralBehaviorSave = (ruleType: string): Promise<any> => {
  return get({ url: `${INTEGRAL}behavior/save?ruleType=${ruleType}` })
}
/*
 * 确认收货
 */
export const doPutCompleteIntegralOrder = (on: string) => put({ url: `addon-integral/integral/order/deliver/complete/${on}` })
/* 
取消订单
*/
export const doDelIntegralOrder = (on: string) => post({ url: `addon-integral/integral/order/close/${on}` })
/**
 * 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 */
export const doGetLogisticsTrajectoryByWaybillNo = (
  companyCode: string,
  waybillNo: string,
  shopId: string,
  recipientsPhone?: string,
): Promise<any> => {
  return get({
    url: `gruul-mall-freight/logistics/node?companyCode=${companyCode}&waybillNo=${waybillNo}&recipientsPhone=${recipientsPhone}&${
      shopId ? { 'Shop-Id': shopId } : {}
    }`,
  })
}
/**
 * 积分订单信息 详情
 */
export const doGetIntegralOrderDetail = (on: string) => {
  return get({ url: `addon-integral/integral/order/get/${on}` })
}

/**
 * 获取物流公司列表
 */
export const doGetLogisticsCompany = (): Promise<any> => {
  return get({
    url: 'gruul-mall-freight/fright/list',
    params: {
      current: 1,
      size: 1000,
    },
  })
}
// 获取警告订单数
export const doPetIntegralOrderWarning = (): Promise<any> => {
  return put({ url: `addon-integral/integral/order/warning` })
}
