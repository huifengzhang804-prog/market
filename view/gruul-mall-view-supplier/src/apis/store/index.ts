import { get, post, put, del, patch } from '../http'

/**
 * @description: 获取分类
 * @param {string} level
 * @param {number} parentId 父级分类id
 * @param {number} size
 * @param {number} current
 * @returns {*}
 */
export const doGetCategoryLevelByParentId = (level: 'LEVEL_2' | 'LEVEL_1' | 'LEVEL_3', parentId: number | string, size: number, current: number) => {
    return get({ url: 'gruul-mall-addon-platform/platform/category/list/level', params: { level, parentId, size, current } })
}
/**
 * @description 获取店铺关联的签约类目
 * @param { string } params.shopId 店铺id号
 * @returns {*}
 */
export const doGetCurrentShopRelationCategory = ({ shopId }: { shopId: string }): Promise<any> => {
    return get({ url: 'gruul-mall-addon-platform/platform/shop/signing/category/listWithParent', params: { shopId } })
}
/**
 * @description 保存店铺的签约类目
 * @param { string[] } param0.currentCategoryId 选定的二级id号
 * @returns {*}
 */
export const doPostSaveShopRelationCategory = (currentCategoryId: string[]) => {
    return post({ url: 'gruul-mall-addon-platform/platform/shop/signing/category/add', data: currentCategoryId })
}
/**
 * @description 移出店铺的签约类目
 * @param { string[] } deleteCategoryId 删除的二级类目号
 * @returns { * }
 */
export const doDelShopRelationCategory = (deleteCategoryId: string[]) => {
    return post({ url: 'gruul-mall-addon-platform/platform/shop/signing/category/del', data: deleteCategoryId })
}
