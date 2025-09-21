package com.medusa.gruul.common.security.resource.helper;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RolePermConsumer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2022/2/25
 */
@Component("S")
public final class Security {

    /**
     * 角色过度器
     */
    public static final Roles R = Roles.USER;

    /**
     * 开发者 角色
     */
    public static final Roles DEVELOPER = Roles.DEVELOPER;

    /**
     * 平台管理员
     */
    public static final Roles PLATFORM_ADMIN = Roles.SUPER_ADMIN;

    /**
     * 平台自定义管理员
     */
    public static final Roles PLATFORM_CUSTOM_ADMIN = Roles.SUPER_CUSTOM_ADMIN;

    /**
     * 商家管理员
     */
    public static final Roles SHOP_ADMIN = Roles.ADMIN;

    /**
     * 商家自定义管理员
     */
    public static final Roles SHOP_CUSTOM_ADMIN = Roles.CUSTOM_ADMIN;

    /**
     * 消费者
     */
    public static final Roles USER = Roles.USER;


    /**
     * 获取用户角色集合 func
     */
    public static final Function<SecureUser<?>, Set<Roles>> ROLES = SecureUser::getRoles;

    /**
     * 获取用户副角色集合 func
     */
    public static final Function<SecureUser<?>, Set<Roles>> SUB_ROLES = SecureUser::getSubRoles;

    /**
     * 获取用户权限集合 func
     */
    public static final Function<SecureUser<?>, Set<String>> PERMS = SecureUser::getPerms;


    /**
     * 用户角色权限匹配器
     *
     * @return IRolePermMatcher 角色权限匹配器
     */
    public IUserRolePermMatcher matcher() {
        return ISecurity.matcher();
    }

    /**
     * IRolePermMatcher 消费器 用于给 springEl表达式使用
     *
     * @return RolePermConsumer 消费器
     */
    public RolePermConsumer consumer() {
        return new RolePermConsumer();
    }

    /**
     * 是否已认证
     */
    public boolean isAuthenticated() {
        return ISecurity.isAuthenticated();
    }


    /**
     * 是否有平台权限 管理员直接放行  自定义管理员有权限放行
     *
     * @param perms 权限列表
     * @return 是否有权限
     */
    public boolean platformPerm(String... perms) {
        return this.hasPerm(true, perms);
    }

    /**
     * 是否有商家端 权限
     * 管理员直接放行
     * 自定义管理员 有权限放行
     */
    public boolean shopPerm(String... perms) {
        return this.hasPerm(false, perms);
    }

    /**
     * 任意权限
     *
     * @param platformPerms 平台权限
     * @param shopPerms     商家权限
     */
    public boolean anyPerm(String[] platformPerms, String[] shopPerms) {
        return this.matcher()
                .anyRole(Roles.SUPER_ADMIN, Roles.ADMIN)
                .or(matcher -> matcher.role(Roles.SUPER_CUSTOM_ADMIN).anyPerm(platformPerms))
                .or(matcher -> matcher.role(Roles.CUSTOM_ADMIN).anyPerm(shopPerms))
                .match();
    }

    /**
     * 管理员直接放行  自定义管理员有权限放行
     *
     * @param platform 是否是平台 true 平台管理员  false 商家管理员
     * @param perms    权限列表
     * @return 是否有权限
     */
    private boolean hasPerm(boolean platform, String... perms) {
        return ISecurity.matcher()
                .role(platform ? Roles.SUPER_ADMIN : Roles.ADMIN)
                .or(matcher -> matcher.role(platform ? Roles.SUPER_CUSTOM_ADMIN : Roles.CUSTOM_ADMIN).anyPerm(perms))
                .match();
    }


}
