import { get, put, post, del } from '../../http'
import { SaveOrUpdateData } from './types'

/**
 * 网站设置
 * @param {SaveOrUpdateData} data
 */
export const doUpdateSaveOr = (data: SaveOrUpdateData) => {
    return post({ url: 'gruul-mall-addon-platform/platform/config/saveOrUpdate', data })
}

/**
 * 根据模块类id获取配置信息
 * @param {string} params
 */
export const queryConfigByModule = (params: string) => {
    return get<SaveOrUpdateData>({ url: `gruul-mall-addon-platform/platform/config/query-config-by-module?modules=${params}` })
}
