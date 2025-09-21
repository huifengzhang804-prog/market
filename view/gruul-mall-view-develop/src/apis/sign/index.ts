/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-03-22 13:13:59
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:12:34
 */
import { post } from "../http";
import { encryptAccept } from "@/utils/rsa";
import { GrantType, GrantTypes } from "./index.type";

/**
 * @LastEditors: Liu_Fei
 * @description: login
 * @param {GrantType} grantType 认证方式类型
 * @param data 认证资料
 * @returns {*}
 */
export const signByUser = (grantType: GrantType, data: any) => {
  const grant = GrantTypes[grantType];
  //参数加密
  if (grant.encrypt) {
    encryptAccept((encrypt) => {
      grant.encryptFields?.forEach((field) => {
        data[field] = encrypt(data[field]);
      });
    });
  }
  return post({
    showLoading: true,
    url: `/gruul-mall-uaa/login?grant_type=${grant.value}`,
    data,
  });
};

/**
 * 退出登录
 */
export const doPostLogout = () => {
  return post({
    url: "gruul-mall-uaa/logout",
    showLoading: false,
  });
};
