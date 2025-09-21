package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import org.junit.Test;

import java.util.Set;

/**
 * @author 张治保
 * @since 2023/12/25
 */
public class MatcherTest {

    @Test
    public void test() {
        SecureUser<Object> user = new SecureUser<>()
                .setRoles(Set.of(Roles.ADMIN, Roles.SUPER_CUSTOM_ADMIN))
                .setPerms(Set.of("save", "update", "delete"))
                .setId(10L);

        RolePermMatcher matcher = new RolePermMatcher(user);

        boolean success = matcher.role(Roles.SUPER_ADMIN)
                .or(match -> match.role(Roles.SUPER_CUSTOM_ADMIN).perm("1"))
                .or(match -> match.role(Roles.ADMIN).perm("1"))
                .match();
        System.out.println(success);
    }

    @Test
    public void test2() {
        SecureUser<Object> user = new SecureUser<>()
                .setRoles(Set.of(Roles.ADMIN, Roles.SUPER_CUSTOM_ADMIN))
                .setPerms(Set.of("save", "update", "delete"))
                .setId(10L);

        RolePermMatcher matcher = new RolePermMatcher(user);

        boolean success = matcher.role(Roles.SUPER_ADMIN)
                .or(new RolePermConsumer().role(Roles.SUPER_CUSTOM_ADMIN).perm("delete"))
                .or(new RolePermConsumer().role(Roles.ADMIN).perm("1"))
                .match();
        System.out.println(success);
    }
}
