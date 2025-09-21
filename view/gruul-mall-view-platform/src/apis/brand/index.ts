import { get, post, put, del } from '../http'

/**
 * 添加品牌
 */
export const doAddBrand = (data: any) => {
    return post({
        url: 'gruul-mall-search/search/brand',
        data,
    })
}
/**
 *获取品牌列表
 */
export const getBrandList = (data: any): Promise<any> => {
    return get({
        url: 'gruul-mall-search/search/brand',
        params: data,
    })
}
/**
 *获取品牌详情
 */
export const getBrandDetail = (brandId: string): Promise<any> => {
    return get({
        url: `gruul-mall-search/search/brand/${brandId}`,
    })
}
/**
 *删除品牌
 */
export const doDelBrand = (brandId: string) => {
    return del({
        url: `gruul-mall-search/search/brand/${brandId}`,
    })
}
/**
 *品牌上下架
 */
export const doPutBrand = (brandId: string, status: string) => {
    return put({
        url: `gruul-mall-search/search/brand/updateStatus/${brandId}/${status}`,
    })
}
/**
 *编辑品牌
 */
export const doEditeBrand = (data: any) => {
    return put({
        url: `gruul-mall-search/search/brand`,
        data,
    })
}
