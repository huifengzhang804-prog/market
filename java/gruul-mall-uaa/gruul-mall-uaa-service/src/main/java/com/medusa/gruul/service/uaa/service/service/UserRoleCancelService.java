package com.medusa.gruul.service.uaa.service.service;

import com.medusa.gruul.common.security.model.enums.Roles;

/**
 * 用户角色取消服务层
 *
 * @author xiaoq
 * @Description 用户角色取消服务层
 * @date 2023-03-14 19:53
 */
public interface UserRoleCancelService {
    /**
     * 用户角色删除 by 用户手机号
     *
     * @param mobile 用户手机号
     * @param roles 用户角色
     * @return 是否成功
     */
    boolean delUserRoleByMobiles(String mobile, Roles roles);
}
