package com.medusa.gruul.mall.common.security;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import org.springframework.stereotype.Component;

/**
 * 简化商城权限校验逻辑工具
 *
 * @author 张治保
 * @since 2024/6/13
 */
@Component("SS")
public class MallSecurity {

    private static final ThreadLocal<IUserRolePermMatcher> MATCHER = new TransmittableThreadLocal<>();

    private static IUserRolePermMatcher getMatcher() {
        IUserRolePermMatcher mather = MATCHER.get();
        if (mather != null) {
            return mather;
        }
        mather = ISecurity.matcher();
        MATCHER.set(mather);
        return mather;
    }

    /**
     * 匹配任意平台权限
     * 超级管理员 或 有任意权限的管理员
     *
     * @return this
     */
    public MallSecurity platform(String... perms) {
        getMatcher()
                .or(mather ->
                        mather.role(Roles.SUPER_ADMIN)
                                .or(m -> m.role(Roles.SUPER_CUSTOM_ADMIN).anyPerm(perms))

                );
        return this;
    }

    /**
     * 匹配任意商家权限
     * 商家管理员 或 有任意权限的商家管理员
     *
     * @param perms 权限
     * @return this
     */
    public MallSecurity shop(String... perms) {
        getMatcher()
                .or(mather ->
                        mather.role(Roles.ADMIN)
                                .or(m -> m.role(Roles.CUSTOM_ADMIN).anyPerm(perms))

                );
        return this;
    }

    /**
     * 是否是消费者
     *
     * @return this
     */
    public MallSecurity otherRoles(Roles... roles) {
        getMatcher().or(
                mather -> mather.anyRole(roles)
        );
        return this;
    }

    /**
     * 权限是否匹配成功
     *
     * @return this
     */
    public boolean match() {
        try {
            return getMatcher().match();
        } finally {
            MATCHER.remove();
        }
    }


}
