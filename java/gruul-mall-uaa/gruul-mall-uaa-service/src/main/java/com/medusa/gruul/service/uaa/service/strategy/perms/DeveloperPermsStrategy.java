package com.medusa.gruul.service.uaa.service.strategy.perms;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/4/19
 */
@RequiredArgsConstructor
public class DeveloperPermsStrategy extends AbstractPermsStrategy {

    private final IUserRoleService userRoleService;

    @Override
    public String nickname(Roles mainRole, Long shopId, Long userId) {
        return Roles.DEVELOPER.getDesc();
    }

    @Override
    public Set<Role> getRoles(Long shopId, Long userId) {
        Set<Role> rolesSet = userRoleService.selectRolesByUserId(userId);
        if (CollUtil.isEmpty(rolesSet)) {
            return Set.of();
        }
        Optional<Role> any = rolesSet.stream()
                .filter(role -> Roles.DEVELOPER == role.getValue())
                .findAny();
        return any.map(Set::of).orElseGet(Set::of);
    }

    @Override
    public Set<String> getPerms(Long roleId) {
        return Set.of();
    }

    @Override
    public ClientType getClientType() {
        return ClientType.DEVELOPER_CONSOLE;
    }

    @Override
    public void afterLoginSuccess(Long shopId, Long userId) {

    }
}
