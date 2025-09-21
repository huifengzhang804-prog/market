package com.medusa.gruul.service.uaa.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.web.valid.annotation.Limit;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.service.uaa.service.model.dto.MenuDTO;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.model.vo.NavigateVO;
import com.medusa.gruul.service.uaa.service.service.SecureMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 插件控制器
 *
 * @author 张治保
 * date 2022/3/2
 */
@Validated
@RestController
@RequestMapping("/uaa/menu")
@PreAuthorize("@S.matcher().eq(@S.ROLES, @S.DEVELOPER).match()")
@RequiredArgsConstructor
public class MenuController {

    private final SecureMenuService secureMenuService;

    /**
     * 查询导航列表 树结构
     *
     * @return 查询结果
     */
    @Log("导航菜单查询")
    @GetMapping("/navigate")
    @PreAuthorize("@S.matcher()" +
            ".anyRole(@S.PLATFORM_ADMIN, @S.PLATFORM_CUSTOM_ADMIN," +
            " @S.SHOP_ADMIN, @S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN).match()")
    public Result<NavigateVO> menus() {
        Final<Roles> roles = new Final<>(Roles.CUSTOM_ADMIN);
        ISecurity.match()
                .ifSuperAdmin(secureUser -> roles.set(Roles.SUPER_ADMIN))
                .ifCustomSuperAdmin(secureUser -> roles.set(Roles.SUPER_CUSTOM_ADMIN))
                .ifAdmin(secureUser -> roles.set(Roles.ADMIN))
                .ifCustomAdmin(secureUser -> roles.set(Roles.CUSTOM_ADMIN))
                .when(secureUser -> roles.set(Roles.SUPPLIER_ADMIN),Roles.SUPPLIER_ADMIN)
                .when(secureUser -> roles.set(Roles.SUPPLIER_CUSTOM_ADMIN),Roles.SUPPLIER_CUSTOM_ADMIN)

        ;
        NavigateVO navigateVO = secureMenuService.navigate(roles.get())
                .setIsAdmin(ISecurity.anyRole(Roles.ADMIN, Roles.SUPER_ADMIN, Roles.SUPPLIER_ADMIN));
        return Result.ok(navigateVO);
    }

    /**
     * 开发者 查询导航列表 树结构
     */
    @GetMapping("/dev")
    @Log("开发-导航菜单查询")
    public Result<List<MenuVO>> menusForDev(@RequestParam @Limit(value = {"PLATFORM_CONSOLE", "SHOP_CONSOLE"}) ClientType clientType) {
        return Result.ok(
                secureMenuService.menusForDev(clientType)
        );
    }

    /**
     * 创建新菜单
     *
     * @param menu 菜单信息
     */
    @Log("新增导航菜单")
    @PostMapping
    public Result<Void> newMenu(@RequestBody @Valid MenuDTO menu) {
        secureMenuService.newMenu(menu);
        return Result.ok();
    }

    /**
     * 编辑菜单
     *
     * @param menuId 菜单id
     * @param menu   菜单信息
     */
    @Log("编辑导航菜单")
    @PutMapping("/{menuId}")
    public Result<Void> editMenu(@PathVariable Long menuId, @RequestBody @Valid MenuDTO menu) {
        secureMenuService.editMenu(menuId, menu);
        return Result.ok();
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     */
    @Log("删除菜单")
    @DeleteMapping("/{menuId}")
    public Result<Void> deleteMenu(@PathVariable Long menuId) {
        secureMenuService.deleteMenu(menuId);
        return Result.ok();
    }


}
