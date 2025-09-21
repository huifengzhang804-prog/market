package com.medusa.gruul.service.uaa.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.entity.RoleMenu;
import com.medusa.gruul.service.uaa.service.mp.mapper.RoleMenuMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleMenuService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色菜单表 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Override
    public List<Menu> getMenusByRoleId(@NonNull Long roleId) {
        return baseMapper.getMenusByRoleId(roleId);
    }
}
