package com.medusa.gruul.service.uaa.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色菜单表 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 根据角色id 查询角色拥有的菜单列表
     * @param roleId 角色id
     * @return 菜单列表
     */
    List<Menu> getMenusByRoleId(@Param("roleId") Long roleId);

}
