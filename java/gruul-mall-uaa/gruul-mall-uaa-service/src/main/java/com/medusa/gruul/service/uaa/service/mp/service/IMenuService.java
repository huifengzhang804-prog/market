package com.medusa.gruul.service.uaa.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 查询菜单树
     *
     * @param clientType 客户端类型
     * @param parentId   父菜单id
     * @param unshared   是否管理员独占 为空查询所有
     * @return 查询的菜单树
     */
    List<MenuVO> queryMenusByParentId(ClientType clientType, Long parentId, Boolean unshared);

    /**
     * 根据menuIds 递归查询菜单树
     *
     * @param clientType 客户端类型
     * @param parentId   父菜单id
     * @param menuIds    拥有的菜单id列表
     * @return 查询的菜单结果
     */
    List<MenuVO> queryCustomAdminMenus(ClientType clientType, Long parentId, Set<Long> menuIds);


    /**
     * 递归树结构菜单列表
     *
     * @param clientType 客户端类型
     * @param parentId   父id
     * @param unshared   是否管路员独占 null查询所有
     * @return 查询结果
     */
    List<MenuVO> menuTree(ClientType clientType, Long parentId, Boolean unshared);


    /**
     * 递归树结构菜单列表 根据菜单id集合
     *
     * @param clientType 客户端类型
     * @param parentId   父id
     * @param menuIds    菜单id集合
     * @return 查询结果
     */
    List<MenuVO> menuTree(ClientType clientType, Long parentId, Set<Long> menuIds);

}
