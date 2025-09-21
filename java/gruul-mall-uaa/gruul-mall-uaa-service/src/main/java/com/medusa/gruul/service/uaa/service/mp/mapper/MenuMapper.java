package com.medusa.gruul.service.uaa.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询菜单树
     * @param clientType 客户端类型值
     * @param parentId 父菜单id
     * @param unshared 是否管理员独占 空查询所有
     * @return 查询的菜单树
     */
    List<MenuVO> queryMenusByParentId(@Param("clientType") ClientType clientType, @Param("parentId") Long parentId,@Param("unshared") Boolean unshared);

    /**
     * 根据menuIds 递归查询菜单树
     * @param clientType 客户端类型值
     * @param parentId 父菜单id
     * @param menuIds  拥有的菜单id列表
     * @return 查询结果
     */
    List<MenuVO> queryCustomAdminMenus(@Param("clientType") ClientType clientType, @Param("parentId") Long parentId, @Param("menuIds") Set<Long> menuIds);
}
