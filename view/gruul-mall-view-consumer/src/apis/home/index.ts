import api from '@/libs/request'
import type { ApiCategoryData } from '@/pages/platform/types'
import type { Pagination } from '@/utils/types'

/**
 * 获取分类
 * @param {string} level
 * @param {number} parentId 父级分类id
 * @param {number} size
 * @param {number} current
 */
export const doGetCategory = (level: string | number, parentId: number | string, size: number, current: number) => {
  return api.get<Pagination<ApiCategoryData[]>>('gruul-mall-addon-platform/platform/category/list/level', { level, parentId, size, current })
}
