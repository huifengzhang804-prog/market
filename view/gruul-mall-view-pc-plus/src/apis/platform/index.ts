import { get, post, put, del, patch } from '../http'
/**
 * @description: 分页查询商品分类
 * @param {} params
 */
export const doGetCategory = (params?: any) => {
  return get({ url: 'gruul-mall-addon-platform/platform/category/list', params })
}
/**
 * 查询PC装修页面数据
 */

export const doGetOpeningUp = (pageType = 'RECOMMENDED_MALL_HOME_PAGE') => {
  return get({
    url: `gruul-mall-addon-platform/pages/opening-up/PC_MALL/${pageType}`,
  })
}
/**
 * 根据模块类id获取配置信息
 */
export const queryConfigByModule = (params: string): Promise<any> => {
  return get({ url: `gruul-mall-addon-platform/platform/config/query-config-by-module?modules=${params}` })
}
/**
 * @description:获取平台装修页面详情
 */
export const doGetPlatformPagesDetail = (id: string): Promise<any> => {
  return get({
    url: `/gruul-mall-addon-platform/pages/${id}`,
  })
}

/**
 * @: 查询PC 商家端 装修页面数据
 */
export const doGetShopOpeningUp = (shopId: string, pageType = 'SHOP_HOME_PAGE'): Promise<any> => {
  return get({
    url: `gruul-mall-shop/decoration-pages/opening-up/${shopId}/PC_MALL/${pageType}`,
  })
}

/**
 * @description:获取店铺装修页面详情
 */
export const doGetShopPagesDetail = (id: string, shopId: string): Promise<any> => {
  return get({
    url: `gruul-mall-shop/decoration-pages/${id}`,
    headers: { 'Shop-Id': shopId },
  })
}
