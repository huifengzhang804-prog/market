package com.medusa.gruul.service.uaa.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.po.ShopAdminRoleMap;
import com.medusa.gruul.service.uaa.service.model.po.UserRoleDetailsInfo;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;

import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface IUserRoleService extends IService<UserRole> {

    /**
     * 根据用户id 获取用户角色列表
     *
     * @param userId 用户id
     * @return 用户拥有的角色列表
     */
    Set<Role> selectRolesByUserId(Long userId);

    /**
     * 根据 角色类型与用户id 查询自定义管理员Id
     *
     * @param role   角色类型
     * @param userId 用户id
     * @return 用户拥有的自定义管理员id
     */
    Long customerAdminRoleId(Roles role, Long userId);

    /**
     * 根据 店铺id查询店铺当前管理员 用户角色对应关系
     *
     * @param adminRole 目标管理员角色
     * @return 店铺管理员用户id
     */
    ShopAdminRoleMap shopAdminCurrent(Roles adminRole);

    /**
     * 获取用户角色详情
     *
     * @param id    用户id
     * @param value 角色值
     * @return
     */
    UserRoleDetailsInfo getUserRoleDetails(Long id, Integer value);
}
