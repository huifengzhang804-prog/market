package com.medusa.gruul.service.uaa.service.service.impl;

import com.google.common.collect.Lists;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.dto.MenuDTO;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.model.vo.NavigateVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import com.medusa.gruul.service.uaa.service.service.SecureMenuService;
import com.medusa.gruul.service.uaa.service.strategy.navigate.MenuStrategyFactory;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author 张治保
 * date 2022/3/2
 */
@Service
@RequiredArgsConstructor
public class SecureMenuServiceImpl implements SecureMenuService {


    private final IMenuService menuService;
    private final MenuStrategyFactory menuStrategyFactory;
    private final ShopRpcService shopRpcService;

    @Override
    public NavigateVO navigate(Roles role) {
        List<MenuVO> menuVOList = menuStrategyFactory.execute(role, null);

        if (ClientType.SHOP_CONSOLE.equals(ISecurity.userMust().getClientType())) {
            Long shopId = ISecurity.userMust().getShopId();
            ShopInfoVO shopInfoVO = shopRpcService.getShopInfoByShopId(shopId);
            if (ShopMode.O2O.equals(shopInfoVO.getShopMode())) {
                for (MenuVO menuVO : menuVOList) {
                    if (menuVO.getName().equals("营销应用")) {
                        List<MenuVO> children = menuVO.getChildren();
                        menuVO.setChildren(children.stream().filter(menuVO1 ->
                                !Lists.newArrayList("秒杀","砍价","拼团","套餐")
                                        .contains(menuVO1.getName())).toList());
                    }
                }
            }

        }

        return new NavigateVO()
                .setMenus(menuVOList);
    }

    @Override
    public List<MenuVO> menusForDev(ClientType clientType) {
        if (ClientType.SHOP_CONSOLE != clientType && ClientType.PLATFORM_CONSOLE != clientType) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        return menuService.menuTree(clientType, 0L, (Boolean) null);
    }

    @Override
    public void newMenu(MenuDTO menu) {
        /*
         * 检查入参
         */
        menu.checkParam(menuService, null);

        boolean success = menuService.save(
                new Menu()
                        .setName(menu.getName())
                        .setAbbreviationName(menu.getAbbreviationName())
                        .setType(menu.getType())
                        .setUnshared(Boolean.FALSE)
                        .setParentId(menu.getParentId())
                        .setClientType(menu.getClientType())
                        .setOrder(menu.getOrder())
                        .setPath(menu.getPath())
                        .setPerms(menu.getPerm())
                        .setUnshared(menu.getUnshared())
                        .setIcon(menu.getIcon())
        );
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }

    @Override
    public void editMenu(Long menuId, MenuDTO menu) {
        /*
         * 检查入参
         */
        menu.checkParam(menuService, menuId);
        Menu oldMenu = menuService.getById(menuId);
        if (oldMenu == null) {
            throw SystemCode.DATA_ADD_FAILED.exception();
        }
        boolean success = menuService.updateById(
                oldMenu.setName(menu.getName())
                        .setAbbreviationName(menu.getAbbreviationName())
                        .setType(menu.getType())
                        .setParentId(menu.getParentId())
                        .setOrder(menu.getOrder())
                        .setPath(menu.getPath())
                        .setPerms(menu.getPerm())
                        .setUnshared(menu.getUnshared())
                        .setIcon(menu.getIcon())
        );
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }


    @Override
    public void deleteMenu(Long menuId) {
        /*
         * 判断是否有子菜单 有子菜单则不可删除
         * */
        Long count = menuService.lambdaQuery().eq(Menu::getParentId, menuId).count();
        if (count > 0) {
            throw UaaError.MENU_HAS_CHILDREN.exception();
        }
        menuService.removeById(menuId);
    }
}
