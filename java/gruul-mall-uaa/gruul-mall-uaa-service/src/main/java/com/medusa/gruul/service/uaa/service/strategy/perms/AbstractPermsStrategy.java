package com.medusa.gruul.service.uaa.service.strategy.perms;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.base.UserKey;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.service.uaa.service.model.po.RolesAndPerms;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 抽象的角色权限处理器
 *
 * @author 张治保
 * @since 2024/4/19
 */
public abstract class AbstractPermsStrategy implements IStrategy<ClientType, UserKey, RolesAndPerms> {
    @Override
    public RolesAndPerms execute(ClientType type, UserKey param) {
        Long shopId = param.getShopId();
        Long userId = param.getUserId();
        Set<Role> roles = getRoles(shopId, userId);
        if (CollUtil.isEmpty(roles)) {
            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
        }
        LinkedList<Role> linkedRoles = new LinkedList<>(roles);
        linkedRoles.sort((o1, o2) -> Roles.mainSortComparator(getClientType()).compare(o1.getValue(), o2.getValue()));
        Role role = linkedRoles.pollFirst();
        if (role == null) {
            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
        }
        //  主角色
        Roles mainRole = role.getValue();
        if (getClientType() != mainRole.getClientType()) {
            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
        }
        Set<String> mergedPerms = new HashSet<>(mainRole.destinations(shopId, userId));
        if (mainRole.isPermNeed()) {
            mergedPerms.addAll(getPerms(role.getId()));
        }
        this.afterLoginSuccess(shopId, userId);
        return new RolesAndPerms()
                .setNickname(nickname(mainRole, shopId, userId))
                .setRoles(Set.of(role))
                .setSubRoles(Set.copyOf(linkedRoles))
                .setPerms(mergedPerms);
    }


    /**
     * 获取用户昵称
     *
     * @param mainRole 主角色
     * @param shopId   店铺id
     * @param userId   用户id
     * @return 用户昵称
     */
    public abstract String nickname(Roles mainRole, Long shopId, Long userId);

    /**
     * 获取角色权限数据具体实现
     *
     * @param shopId 商家id
     * @param userId 用户id
     * @return 用户角色集合
     */
    public abstract Set<Role> getRoles(Long shopId, Long userId);

    /**
     * 获取角色权限数据具体实现
     *
     * @param roleId 角色id
     * @return 用户角色集合
     */
    public abstract Set<String> getPerms(Long roleId);

    /**
     * 获取客户端类型
     *
     * @return 客户端类型
     */
    public abstract ClientType getClientType();


    /**
     * 登录成功后的操作
     *
     * @param shopId 商铺id
     * @param userId 用户id
     */
    public abstract void afterLoginSuccess(Long shopId, Long userId);
}
