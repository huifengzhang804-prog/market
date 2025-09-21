package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.UserMatch;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 用户 角色权限 匹配器
 *
 * @author 张治保
 * date 2023/4/10
 */
public final class RolePermMatcher implements IUserRolePermMatcher {

    /**
     * 当前用户
     */
    private final SecureUser<?> user;

    /**
     * 匹配结果
     */
    private boolean success = true;

    /**
     * or 条件初始化兼容
     */
    private boolean orFirst = true;

    /**
     * 构造器
     *
     * @param authentication 认证信息
     */
    public RolePermMatcher(Authentication authentication) {
        // 如果上下文中没有用户信息，则从authentication中获取
        this(userFromAuthentication(authentication));
    }

    /**
     * 构造器
     *
     * @param user 用户信息
     * @param <T>  用户额外信息类型
     */
    public <T> RolePermMatcher(SecureUser<T> user) {
        this.user = user;
    }

    /**
     * 获取用户信息
     *
     * @param authentication 用户信息
     * @return {@link SecureUser} 用户信息
     */
    private static SecureUser<?> userFromAuthentication(Authentication authentication) {
        if (!(authentication instanceof SecureUserAuthentication secureUserAuthentication) || ISecurity.isAnonymous(authentication)) {
            return new SecureUser<>();
        }
        return secureUserAuthentication.user();
    }

    /**
     * 条件组装器 匿名用户
     *
     * @return {@link RolePermMatcher} 条件组装器
     */
    @Override
    public IUserRolePermMatcher anonymous() {
        if (success) {
            this.update(user.isAnonymous());
        }
        return this;
    }


    /**
     * 条件组装器 且  必须不包含
     *
     * @param property  权限/角色列
     * @param condition 权限条件
     * @param <T>       权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public <T> IUserRolePermMatcher neq(Function<SecureUser<?>, Set<T>> property, T condition) {
        if (success) {
            this.update(!property.apply(user).contains(condition));
        }
        return this;
    }

    /**
     * 条件组装器 且  必须包含匹配
     *
     * @param property  权限/角色列
     * @param condition 权限条件
     * @param <T>       权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public <T> IUserRolePermMatcher eq(Function<SecureUser<?>, Set<T>> property, T condition) {
        if (success) {
            this.update(property.apply(user).contains(condition));
        }
        return this;
    }

    /**
     * 条件组装器 且  任一条件包含匹配
     *
     * @param property   权限/角色列
     * @param conditions 权限条件
     * @param <T>        权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @SafeVarargs
    @Override
    public final <T> IUserRolePermMatcher any(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        if (success) {
            Set<T> roleOrPerms = property.apply(user);
            boolean anyMatch = false;
            for (T condition : conditions) {
                if (roleOrPerms.contains(condition)) {
                    anyMatch = true;
                    break;
                }
            }
            this.update(anyMatch);
        }
        return this;
    }

    /**
     * 条件组装器 且  所有条件都包含匹配
     *
     * @param property   权限/角色列
     * @param conditions 条件权限
     * @param <T>        权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    @SafeVarargs
    public final <T> IUserRolePermMatcher all(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        if (success) {
            Set<T> roleOrPerms = property.apply(user);
            this.update(roleOrPerms.containsAll(List.of(conditions)));
        }
        return this;
    }

    /**
     * 条件组装器 且
     *
     * @param and 条件组装器
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public IUserRolePermMatcher and(Consumer<IRolePermMatcher<?>> and) {
        if (success) {
            RolePermMatcher rolePermMatcher = new RolePermMatcher(user);
            and.accept(rolePermMatcher);
            this.update(rolePermMatcher.match());
        }
        return this;
    }

    /**
     * 条件组装器 或
     *
     * @param or 条件组装器
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public IUserRolePermMatcher or(Consumer<IRolePermMatcher<?>> or) {
        if (orFirst || !success) {
            RolePermMatcher rolePermMatcher = new RolePermMatcher(user);
            or.accept(rolePermMatcher);
            this.update(rolePermMatcher.match());
        }
        return this;
    }

    @Override
    public IUserRolePermMatcher custom(Function<IRolePermMatcher<?>, Boolean> custom) {
        Boolean isSuccess = custom.apply(this);
        if (isSuccess != null) {
            this.update(isSuccess);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SecureUser<T> getUser() {
        return (SecureUser<T>) user;
    }

    /**
     * 非黑名单用户 且 匹配成功
     *
     * @return boolean 是否匹配成功 true:匹配成功 false:匹配失败
     */
    @Override
    public boolean match() {
        return !user.getSubRoles().contains(Roles.BLACK_LIST) && success;
    }

    /**
     * 获取用户匹配结果 与用户信息
     *
     * @return {@link UserMatch} 用户匹配结果 与用户信息
     */
    @Override
    public UserMatch userMatch() {
        return UserMatch.builder()
                .secureUser(user)
                .success(match())
                .build();
    }


    private void update(boolean success) {
        this.success = success;
        this.orFirst = false;
    }


}
