package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import io.vavr.control.Option;

import java.util.function.Consumer;

/**
 * 角色匹配任务
 *
 * @author 张治保
 * date 2022/8/11
 */
public final class RoleTask {


    private final IUserRolePermMatcher matcher;


    public RoleTask() {
        matcher = ISecurity.matcher();

    }

    /**
     * 当拥有roles中的任意一个角色时执行 consumer 消费任务
     *
     * @param consumer 消费任务
     * @param roles    角色列表
     * @return RoleTask
     */
    public <T> RoleTask when(Consumer<SecureUser<T>> consumer, Roles... roles) {
        matcher.or(
                rp -> rp.any(SecureUser::getRoles, roles)
                        .and(rp1 -> consumer.accept(matcher.getUser()))
        );
        return this;
    }

    /**
     * 当是匿名用户访问是的任务
     *
     * @param anonymousTask 匿名任务
     * @return RoleTask
     */
    public RoleTask anonymous(Runnable anonymousTask) {
        matcher.or(
                rp -> rp.anonymous()
                        .and(rp1 -> anonymousTask.run())
        );
        return this;
    }

    /**
     * 当是超级管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifSuperAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPER_ADMIN);
    }

    /**
     * 当是自定义超级管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifCustomSuperAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPER_CUSTOM_ADMIN);
    }

    /**
     * 当是超级管理员或自定义超级管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifAnySuperAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN);
    }

    /**
     * 当是管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.ADMIN);
    }

    /**
     * 当是自定义管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifCustomAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.CUSTOM_ADMIN);
    }

    /**
     * 当是管理员或自定义管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifAnyShopAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.ADMIN, Roles.CUSTOM_ADMIN);
    }


    public <T> RoleTask ifAnySupplierAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
    }

    /**
     * 当是用户时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifUser(Consumer<SecureUser<T>> task) {
        return when(task, Roles.USER);
    }

    /**
     * 当用户没有匹配到任何任务时 执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask other(Consumer<SecureUser<T>> task) {
        matcher.or(rp -> task.accept(matcher.getUser()));
        return this;
    }


    /**
     * 执行任务 并返回可能为空的用户信息
     * 当用户匿名访问为空 需要使用方自行判断
     *
     * @return SecureUser
     * @deprecated 匿名用户也不会为空, 请使用 {@link #getUser()}}
     * 使用{@link SecureUser#isAnonymous()}判断是否是匿名用户
     */
    @Deprecated
    public <T> Option<SecureUser<T>> run() {
        SecureUser<T> user = getUser();
        if (user.isAnonymous()) {
            return Option.none();
        }
        return Option.of(user);
    }

    /**
     * @return SecureUser 用户信息 请使用 {@link SecureUser#isAnonymous()}判断是否是匿名用户
     */
    public <T> SecureUser<T> getUser() {
        return matcher.getUser();
    }


}
