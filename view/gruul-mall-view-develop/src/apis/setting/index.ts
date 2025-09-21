/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-04-06 17:32:50
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-04-13 09:43:33
 */
import { get, post, put, del } from "../http";

export const getAuth = (clientType: string) => {
  return get({ url: "/gruul-mall-uaa/uaa/menu/dev?clientType=" + clientType });
};
/**
 * @LastEditors: vikingShip
 * @description: 新增权限
 * @param {name:string,path:string,perm:string} data
 */
export const doNewPermissions = (data: any) => {
  return post({
    url: "/gruul-mall-uaa/uaa/menu",
    data,
  });
};
/**
 * @LastEditors: vikingShip
 * @description: 编辑权限
 * @param {menuId string}
 * @param {name:string,path:string,perm:string} data
 */
export const doEditPermissions = (menuId: string, data: any) => {
  return put({
    url: `/gruul-mall-uaa/uaa/menu/${menuId}`,
    data,
  });
};

/**
 * @LastEditors: vikingShip
 * @description: 删除权限
 * @param {string} menuId
 */
export const doDelPermissions = (menuId: string) => {
  return del({
    url: `/gruul-mall-uaa/uaa/menu/${menuId}`,
  });
};
