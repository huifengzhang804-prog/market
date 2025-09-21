package com.medusa.gruul.service.uaa.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.po.ShopAdminRoleMap;
import com.medusa.gruul.service.uaa.service.model.po.UserRoleDetailsInfo;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户id 获取用户角色列表
     *
     * @param userId 用户id
     * @return 查询结果 角色列表
     */
    Set<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id 查询自定义管理员Id
     *
     * @param role   角色类型
     * @param userId 自定义管理员角色
     * @return 返回自定义管理员角色id
     */
    Long customerAdminRoleId(@Param("role") Roles role, @Param("userId") Long userId);

    /**
     * 根据 店铺id查询店铺当前管理员 用户角色对应关系
     *
     * @param adminRole 目标管理员角色
     * @return 查询结果
     */
    ShopAdminRoleMap shopAdminCurrent(@Param("adminRole") Roles adminRole);

    /**
     * 获取用户角色详情
     *
     * @param userId 用户id
     * @param value  角色值
     * @return 用户角色详情信息
     */
    UserRoleDetailsInfo queryUserRoleDetails(@Param("userId") Long userId, @Param("value") Integer value);
}
