import { get, post, put, del, patch } from '../../http'
interface SaveOrUpdateData {
    COPYRIGHT_INFO: string // 版权信息
    COPYRIGHT_URL: string // 版权链接地址
    LOGIN_LOGO: string // 登录页logo
    PLATFORM_LOGIN_PAGE_BG: string // 平台登录页背景
    PLATFORM_LOGO: string // 平台logo
    PLATFORM_NAME: string // 平台名称
    PLATFORM_WEB_SIT_NAME: string // 平台网站名称
    RECORDER_INFO: string // 备案信息
    RECORDER_URL: string // 备案链接地址
    SHOP_LOGIN_PAGE_BG: string // 商家登录页背景图
    SHOP_WEB_SIT_NAME: string // 店铺网站名称
    SUPPLIER_LOGIN_PAGE_BG: string // 供应商登录页背景图
    SUPPLIER_WEB_SIT_NAME: string // 供应商网站名称
    WEB_SIT_ICON: string // 网站图标
}
/**
 * 商家设置信息获取
 * @param data
 */
export const getsettings = <T>(data: any): Promise<any> => {
    return get<T>({
        url: '/gruul-mall-shop/shop/info',
        params: data,
    })
}
/**
 * 店铺交易信息获取
 * @param data
 */
export const gettrade = <T>(data: any) => {
    return get<T>({
        url: '/gruul-mall-order/order/config/form',
        params: data,
    })
}
/**
 * 商家设置信息修改
 * @param data
 */
export const postsettings = (data: any) => {
    return post({
        url: '/gruul-mall-shop/shop/info/update',
        data,
    })
}
/**
 * 商铺交易信息编辑
 * @param data
 */
export const posttrade = (data: any) => {
    return post({
        url: '/gruul-mall-order/order/config/form',
        data,
    })
}
/**
 * 获取店铺关联的签约类目
 * @param { string } params.shopId 店铺id号
 */
export const doGetCurrentShopRelationCategory = ({ shopId }: { shopId: any }): Promise<any> => {
    return get({ url: 'gruul-mall-addon-platform/platform/shop/signing/category/listWithParent', params: { shopId } })
}
/**
 * 保存店铺的签约类目
 * @param { string[] } param0.currentCategoryId 选定的二级id号
 */
export const doPostSaveShopRelationCategory = (currentCategoryId: string[]) => {
    return post({ url: 'gruul-mall-addon-platform/platform/shop/signing/category/add', data: currentCategoryId })
}
/**
 * 移出店铺的签约类目
 * @param { string[] } deleteCategoryId 删除的二级类目号
 * @returns { * }
 */
export const doDelShopRelationCategory = (deleteCategoryId: string[]) => {
    return post({ url: 'gruul-mall-addon-platform/platform/shop/signing/category/del', data: deleteCategoryId })
}
/**
 * 获取分类
 * @param {string} level
 * @param {number} parentId 父级分类id
 * @param {number} size
 * @param {number} current
 */
export const doGetCategoryLevelByParentId = (level: 'LEVEL_2' | 'LEVEL_1' | 'LEVEL_3', parentId: number | string, size: number, current: number) => {
    return get({
        url: 'gruul-mall-addon-platform/platform/category/list/level',
        params: { level, parentId, size, current },
    })
}
/**
 * 根据模块类id获取配置信息
 * @param {string} params
 */
export const queryConfigByModule = (params: string) => {
    return get<SaveOrUpdateData>({ url: `gruul-mall-addon-platform/platform/config/query-config-by-module?modules=${params}` })
}
