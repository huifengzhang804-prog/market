package com.medusa.gruul.service.uaa.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.po.ShopAdminRoleMap;
import com.medusa.gruul.service.uaa.service.model.po.UserRoleDetailsInfo;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.mapper.UserRoleMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    public Set<Role> selectRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public Long customerAdminRoleId(Roles role, Long userId) {
        return baseMapper.customerAdminRoleId(role, userId);
    }

    @Override
    public ShopAdminRoleMap shopAdminCurrent(Roles adminRole) {
        return baseMapper.shopAdminCurrent(adminRole);
    }

    @Override
    public UserRoleDetailsInfo getUserRoleDetails(Long userId, Integer value) {
        return baseMapper.queryUserRoleDetails(userId, value);
    }
}
