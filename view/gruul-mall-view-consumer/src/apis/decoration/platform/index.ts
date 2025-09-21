import api from '@/libs/request'
import type { PageInfo } from './model'

/**
 * 分类页查询分类
 * @param {string} ids
 * @param {string} categoryLevel(平台端)
 */
export const doGetCategoryLevelFromPlatform = (ids: string[], categoryLevel: string) => {
  return api.get('gruul-mall-addon-platform/platform/category/by/ids', { ids: ids.join(','), categoryLevel })
}
type PageConfig = {
  current: number
  pages: number
  size: number
}

/**
 * 根据页面类型获取已启用的模板的对应页面
 * @param {string} type
 */
export const doGetEnablePageByType = (endpointType: string, type: string) => {
  return api.get(`gruul-mall-addon-platform/pages/opening-up/${endpointType}/${type}`)
}
/**
 * 根据页面id获取平台装修页面详情（主要为自定义页面）
 */
export const doGetPlatformPagesDetail = (id: string) => {
  return api.get(`gruul-mall-addon-platform/pages/${id}`)
}
