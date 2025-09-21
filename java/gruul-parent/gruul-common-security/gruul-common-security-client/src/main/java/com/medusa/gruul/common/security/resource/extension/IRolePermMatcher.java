package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2023/4/13
 */
public interface IRolePermMatcher<S extends IRolePermMatcher<S>> {

    /**
     * 开启允许匿名访问
     *
     * @return IRolePermMatcher 条件组装器
     */
    S anonymous();

    /**
     * 不包含角色匹配
     *
     * @param role 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S neqRole(Roles role) {
        return neq(SecureUser::getRoles, role);
    }

    /**
     * 不包含权限匹配
     *
     * @param perm 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S neqPerm(String perm) {
        return neq(SecureUser::getPerms, perm);
    }

    /**
     * 不包含子角色匹配
     *
     * @param role 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S neqSubRole(Roles role) {
        return neq(SecureUser::getSubRoles, role);
    }

    /**
     * 不包含资源（权限/角色）匹配
     *
     * @param condition 权限条件
     * @param property  权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    <T> S neq(Function<SecureUser<?>, Set<T>> property, T condition);


    /**
     * 匹配该角色
     *
     * @param condition 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S role(Roles condition) {
        return eq(SecureUser::getRoles, condition);
    }

    /**
     * 匹配该权限
     *
     * @param perm 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S perm(String perm) {
        return eq(SecureUser::getPerms, perm);
    }

    /**
     * 匹配该子角色
     *
     * @param condition 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S subRole(Roles condition) {
        return eq(SecureUser::getSubRoles, condition);
    }

    /**
     * 资源匹配（权限/角色）
     *
     * @param condition 权限条件
     * @param property  权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    <T> S eq(Function<SecureUser<?>, Set<T>> property, T condition);

    /**
     * 任意角色匹配
     *
     * @param roles 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S anyRole(Roles... roles) {
        return any(SecureUser::getRoles, roles);
    }

    /**
     * 任意权限匹配
     *
     * @param perms 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S anyPerm(String... perms) {
        return any(SecureUser::getPerms, perms);
    }

    /**
     * 任意子角色匹配
     *
     * @param roles 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S anySubRole(Roles... roles) {
        return any(SecureUser::getSubRoles, roles);
    }

    /**
     * 是否包含任意权限/角色
     *
     * @param conditions 权限条件
     * @param property   权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    @SuppressWarnings("unchecked")
    <T> S any(Function<SecureUser<?>, Set<T>> property, T... conditions);


    /**
     * 匹配所有角色
     *
     * @param roles 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S roles(Roles... roles) {
        return all(SecureUser::getRoles, roles);
    }

    /**
     * 匹配所有权限
     *
     * @param perms 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S perms(String... perms) {
        return all(SecureUser::getPerms, perms);
    }

    /**
     * 匹配所有子角色
     *
     * @param condition 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S subRoles(Roles... condition) {
        return all(SecureUser::getSubRoles, condition);
    }

    /**
     * 匹配所有权限/角色
     *
     * @param conditions 条件权限
     * @param property   权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    @SuppressWarnings("unchecked")
    <T> S all(Function<SecureUser<?>, Set<T>> property, T... conditions);


    /**
     * and 条件
     *
     * @param and 条件组装器
     * @return IRolePermMatcher 条件组装器
     */
    S and(Consumer<IRolePermMatcher<?>> and);

    /**
     * or 条件
     *
     * @param or 条件组装器
     * @return IRolePermMatcher 条件组装器
     */
    S or(Consumer<IRolePermMatcher<?>> or);

    /**
     * 自定义条件匹配
     *
     * @param custom 自定义条件
     *               true 匹配成功
     *               false 匹配失败
     *               null  不参与匹配
     * @return IRolePermMatcher 条件组装器
     */
    S custom(Function<IRolePermMatcher<?>, Boolean> custom);
}
