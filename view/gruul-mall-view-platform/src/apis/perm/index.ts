import { get, post, put, del, patch } from '../http'
import { R } from '../http.type'
import { MenuType } from './types'

/**
 * 分页查询当前店铺通知
 */
export const getAdminPage = (params: any): Promise<any> => {
    return get({
        showLoading: false,
        url: '/gruul-mall-uaa/uaa/shop/admin',
        params,
    })
}

/**
 * 分页查询店铺角色
 */
export const getRolePage = (params: any): Promise<any> => {
    return get({
        showLoading: false,
        url: '/gruul-mall-uaa/uaa/role/menu',
        params,
    })
}
/**
 * 保存角色
 */
export const saveRole = (data: any) => {
    return post({
        url: '/gruul-mall-uaa/uaa/role/menu',
        data,
    })
}
/**
 * 根据角色id查询对应菜单id列表
 */
export const getMenuIdsByRoleId = (roleId: string): Promise<any> => {
    return get({
        url: `/gruul-mall-uaa/uaa/role/menu/${roleId}`,
    })
}

/**
 * 编辑角色
 */
export const editRole = (roleId: Long, { roleName, menuIds }: { roleName: string | null; menuIds: Long[] }) => {
    return put({
        url: `/gruul-mall-uaa/uaa/role/menu/${roleId}`,
        data: {
            roleName,
            menuIds,
        },
    })
}
/**
 * 获取所有可分配的菜单
 */
export const getAllMenus = () => {
    return get<R<MenuType[]>>({
        showLoading: false,
        url: '/gruul-mall-uaa/uaa/role/menu/menus',
    })
}
/**
 * 删除角色
 */
export const deleteRole = (roleId: Long) => {
    return del({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/role/menu/${roleId}`,
    })
}
/**
 * 新增店铺自定义管理员
 */
export const newShopCustomAdmin = (data: any) => {
    return post({
        showLoading: false,
        url: '/gruul-mall-uaa/uaa/shop/admin',
        data,
    })
}
/**
 * 编辑店铺自定义管理员
 */
export const updateShopCustomAdmin = (dataId: Long, data: any) => {
    return put({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/shop/admin/${dataId}`,
        data,
    })
}
/**
 * 查询当前店铺可用作管理员的用户
 */
export const getAvailableUser = (keywords: string): Promise<any> => {
    return get({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/shop/admin/available`,
        params: {
            keywords,
            clientType: import.meta.env.VITE_CLIENT_TYPE,
            excludeShopId: 0,
            current: 1,
            size: 10,
        },
    })
}
/**
 * 根据用户id查询管理员注册资料
 */
export const getAdminRegisterDataById = (userId: string): Promise<any> => {
    return get({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/shop/admin/${userId}`,
    })
}
/**
 * 启用/禁用 店铺管理员
 * @param dataId
 * @param status
 */
export const enableDisableShopCustomAdmin = (dataId: Long, status: number) => {
    return patch({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/shop/admin/${dataId}/${status}`,
    })
}

/**
 * 移除店铺管理员
 */
export const deleteShopAdmin = (dataId: Long) => {
    return del({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/shop/admin/${dataId}`,
    })
}
/**
 * 更改角色状态
 */
export const updateRoleStatus = (roleId: Long, enabled: boolean) => {
    return post({
        url: `/gruul-mall-uaa/uaa/role/menu/${roleId}/${enabled}`,
    })
}
