package com.medusa.gruul.service.uaa.service.strategy.perms;

import com.medusa.gruul.common.mp.model.TenantShop;
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
public class ShopStorePermsStrategy extends AbstractPermsStrategy {
    private final IUserRoleService userRoleService;

    @Override
    public String nickname(Roles mainRole, Long shopId, Long userId) {
        return "店员";
    }

    @Override
    public Set<Role> getRoles(Long shopId, Long userId) {
        Optional<Role> anyStore = TenantShop.disable(() -> userRoleService.selectRolesByUserId(userId))
                .stream()
                .filter(role -> role.getValue().getClientType() == ClientType.STORE)
                .findAny();
        return anyStore.map(Set::of).orElseGet(Set::of);
    }

    @Override
    public Set<String> getPerms(Long roleId) {
        return Set.of();
    }

    @Override
    public ClientType getClientType() {
        return ClientType.STORE;
    }

    @Override
    public void afterLoginSuccess(Long shopId, Long userId) {

    }
}
