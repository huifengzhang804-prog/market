import api from '@/libs/request'
import type { EnablePageByType } from './types'

/**
 * 分类页查询分类
 * @param {string} ids
 * @param {string} categoryLevel
 */
export const doGetCategoryLevel = (ids: string[], categoryLevel: string, shopId: Long) => {
  return api.get('gruul-mall-goods/goods/product/category/by/ids', { ids: ids.join(','), categoryLevel }, { 'Shop-Id': shopId })
}
type PageConfig = {
  current: number
  pages: number
  size: number
}
/**
 * 根据二级分类id查询商品(商家)
 */
export const doGetCommodityBySecCateId = (ids: string, categoryLevel: string, pageConfig: PageConfig) => {
  const { current, pages, size } = pageConfig
  return api.get('gruul-mall-goods/api/product/by/categoryId', {
    ids,
    categoryLevel,
    current,
    pages,
    size,
  })
}

/**
 * 根据页面类型获取已启用的模板的对应页面
 * @param {string} type
 */
export const doGetEnablePageByType = (shopID: Long, endpointType: string, pageType: string) => {
  return api.get<EnablePageByType>(`gruul-mall-shop/decoration-pages/opening-up/${shopID}/${endpointType}/${pageType}`)
}
/**
 * 获取店铺装修页面详情
 */
export const doGetShopPagesDetail = (id: string, shopId?: Long) => {
  return api.get(
    `gruul-mall-shop/decoration-pages/${id}`,
    {},
    {
      'Shop-Id': shopId || '0',
    },
  )
}
