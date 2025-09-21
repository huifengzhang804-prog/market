import { ShopFormType } from '@/views/shops/types'
import { get, post, put, del, patch } from '../http'
import { L } from '../http.type'
import { DoShopAuditRequest } from './types/request'
import { ShopListVO, UserListResponse } from '@/apis/shops/types/response'

/**
 * 获取商铺列表
 */
export const doGetShopList = (params: any) => {
    return get<L<ShopListVO>>({
        url: '/gruul-mall-shop/shop/list',
        params,
    })
}
/**
 * 获取店铺
 */
export const doGetAllStoresList = (params: any) => {
    return get<L<ShopListVO>>({
        url: '/gruul-mall-shop/shop/info/getShopInfo',
        params,
    })
}
/**
 * 获取商铺详情
 */
export const doGetShopDetail = (id: any) => {
    return get<ShopFormType>({
        url: `/gruul-mall-shop/shop/${id}/detail`,
    })
}
/**
 * 获取商铺状态数据
 */
export const storeStatusList = (data: any): Promise<any> => {
    return post({
        url: `/gruul-mall-shop/shop/status/count`,
        data,
    })
}
/**
 * 店铺重新审核
 */
export const doReAudit = ({ shopId }: { shopId: string }): Promise<any> => {
    return put({
        url: `/gruul-mall-shop/shop/review/audit/${shopId}`,
    })
}
/**
 * 新增商户
 */
export const doAddShops = (data: any) => {
    return post({
        url: '/gruul-mall-shop/shop',
        data,
    })
}

export const doDelShop = (params: string[]) => {
    const toStr = params.join()
    return del({
        url: `/gruul-mall-shop/shop`,
        params: {
            shopIds: toStr,
        },
    })
}
export const doEditShop = (data: any) => {
    return put({
        url: `/gruul-mall-shop/shop/${data.id}`,
        data,
    })
}

export const doChangeStatus = (params: string[], isEnable: boolean) => {
    const toStr = params.join()
    return patch({
        url: `/gruul-mall-shop/shop/${isEnable}`,
        params: {
            shopIds: toStr,
        },
    })
}

/**
 * 审核店铺
 * @param shopId 店铺id
 * @param isPass 是否通过
 */
export const doShopAudit = (data?: DoShopAuditRequest) => {
    return post({
        url: `/gruul-mall-shop/shop/audit`,
        data,
    })
}

/**
 * 获取类目列表
 */
export const doGetCategory = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-addon-platform/platform/category/list',
        params,
    })
}
export const doNewCategory = (data: any) => {
    return post({
        url: 'gruul-mall-addon-platform/platform/category/save',
        data,
    })
}

/**
 * 获取分类
 * @param {number} parentId 父级分类id
 */
export const doGetCategoryLevelByParentId = (
    level: 'LEVEL_2' | 'LEVEL_1' | 'LEVEL_3',
    parentId: number | string,
    size: number,
    current: number,
): Promise<any> => {
    return get({ url: 'gruul-mall-addon-platform/platform/category/list/level', params: { level, parentId, size, current } })
}
/**
 * 类目排序
 */
export const doSortCategory = (ids: string[], parentId: string) => {
    return put({
        url: 'gruul-mall-addon-platform/platform/category/order',
        data: {
            sortedIds: ids,
            parentId,
        },
        showLoading: false,
    })
}
/**
 * 删除类目
 */
export const doDelCategory = (id: string) => {
    return del({
        url: `gruul-mall-addon-platform/platform/category/delete/${id}`,
    })
}
/**
 * 更新类目
 */
export const doUpdateCategory = (id: string, data: any) => {
    return put({
        url: `gruul-mall-addon-platform/platform/category/update/${id}`,
        data,
    })
}
/**
 * 获取用户列表
 */
export const doGetUserList = (current: number, size: number, keywords?: string) => {
    return get<UserListResponse>({
        url: '/gruul-mall-uaa/uaa/shop/admin/available',
        params: {
            current,
            size,
            keywords,
        },
        showLoading: false,
    })
}
/**
 * 获取单个用户信息
 * @param {string} userId 用户id
 */

export const doGetSingleUser = (userId: string) => {
    return get({
        url: `/gruul-mall-uaa/uaa/shop/admin/${userId}`,
        params: {},
    })
}

/**
 * 获取平台端单个商铺关联的签约类目
 * @param { string | number } params.shopId 店铺id
 */
export const doGetShopSigningCategoryList = (params: any = {}): Promise<any> => {
    return get({
        url: `/gruul-mall-addon-platform/platform/shop/signing/category/list`,
        params,
    })
}
/**
 * 获取平台端单个商铺拒绝原因
 * @param { string | number } params.shopId 店铺id
 */
export const doGetShopRejectReason = (id: any = {}): Promise<any> => {
    return get({
        url: `gruul-mall-shop/shop/${id}/rejectReason`,
    })
}

/**
 * 获取店铺优惠券
 * @param { string | number } params.shopId 店铺id
 */
export const doGetShopCoupon = (id: string) => {
    return get({
        url: `addon-coupon/coupon/decoration/${id}`,
    })
}
