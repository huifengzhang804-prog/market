import { L } from '@/utils/types'
import { get, post, put, del, patch } from '../http'
import { ImageDetail } from './types'

export const doPostMaterialList = (data: any) => {
    return post<L<ImageDetail>>({
        url: '/gruul-mall-search/search/material',
        data,
    })
}

export const doGetMaterialCategoryList = <T>(params: any) => {
    return get<T>({
        url: '/gruul-mall-search/search/material/category/children',
        params,
        errorImmediately: false,
    })
}

export const doPostMaterialInfo = (data: any) => {
    return post({
        url: '/gruul-mall-search/search/material/category',
        params: data,
    })
}

export const doPutMaterialInfo = (data: any) => {
    return put({
        url: `/gruul-mall-search/search/material/category/${data?.id}`,
        data: {
            name: data?.name,
        },
    })
}
// 删除分类
export const doDelMaterialInfo = (categoryId: string) => {
    return del({
        url: `/gruul-mall-search/search/material/category/${categoryId}`,
    })
}

/* *批量删除素材 */
export const doDelMaterial = (data: any) => {
    return del<Obj>({
        url: '/gruul-mall-search/search/material',
        data,
    })
}
// 修改素材名称
export const doPutMaterialName = (materialId: string, name: string) => {
    return put({
        url: `/gruul-mall-search/search/material/${materialId}`,
        params: {
            name,
        },
    })
}
/* *更换素材分类 移动素材
 * categoryId 素材分类 id
 * materialIds 素材 id ==> 数组
 */
export const doPutMaterialTo = (categoryId?: Long, materialIds?: string[]) => {
    return put({
        url: 'gruul-mall-search/search/material/move/to/category',
        data: { categoryId, materialIds },
    })
}

// 获取素材推荐检索建议
export const doPostMaterialSuggest = (categoryId?: string) => {
    return post<Obj>({
        url: '/gruul-mall-search/search/material/suggest',
        params: { categoryId },
    })
}
