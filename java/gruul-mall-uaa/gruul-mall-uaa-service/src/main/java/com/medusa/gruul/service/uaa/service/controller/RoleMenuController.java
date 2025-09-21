package com.medusa.gruul.service.uaa.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.service.uaa.api.dto.MenuQueryDTO;
import com.medusa.gruul.service.uaa.service.model.dto.RoleMenuDTO;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.service.RoleMenuHandlerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色菜单控制器
 *
 * @author 张治保
 * date 2022/4/27
 */
@RestController
@RequestMapping("/uaa/role/menu")
@RequiredArgsConstructor
//@PreAuthorize("@S.anyPerm('perm','uaa:shop:admin')")
@PreAuthorize("@S.matcher().anyRole(@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)" +
        ".or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).perm('perm'))" +
        ".or(@S.consumer().role(@S.SHOP_CUSTOM_ADMIN).perm('uaa:shop:admin')" +
        ".or(@S.consumer().role(@S.R.SUPPLIER_CUSTOM_ADMIN).perm('uaa:supplier:admin'))).match()"

)
public class RoleMenuController {

    private final RoleMenuHandlerService roleMenuHandlerService;

    /**
     * 查询可分配自定义管理员的菜单列表
     *
     * @return 查询结果
     */
    @Log("查询可分配自定义管理员的菜单列表")
    @GetMapping("/menus")
    public Result<List<MenuVO>> menus() {
        return Result.ok(
                roleMenuHandlerService.menuTree()
        );
    }

    /**
     * 分页查询店铺橘色列表
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    @Log("分页查询店铺角色")
    @GetMapping
    public Result<IPage<Role>> shopRolePage(MenuQueryDTO page) {
        return Result.ok(
                roleMenuHandlerService.shopRolePage(page)
        );
    }

    /**
     * 新增角色与权限关联关系
     *
     * @param roleMenu 角色菜单关联数据
     */
    @Idem
    @Log("新增角色菜单")
    @PostMapping
    public Result<Void> newRoleMenu(@RequestBody @Valid RoleMenuDTO roleMenu) {
        roleMenuHandlerService.newRoleMenu(roleMenu);
        return Result.ok();
    }

    /**
     * 根据角色id获取角色对应的菜单id集合
     *
     * @param roleId 角色id
     * @return 角色对应的菜单id集合
     */
    @Log("根据角色id查询 对应的 菜单id列表")
    @GetMapping("/{roleId}")
    public Result<Set<Long>> getMenuIdsByRoleId(@PathVariable @Min(7) Long roleId) {
        return Result.ok(
                roleMenuHandlerService.getMenuIdsByRoleId(roleId)
        );

    }

    /**
     * 编辑角色菜单关联关系
     *
     * @param roleId   角色id
     * @param roleMenu 角色菜单关联数据
     */
    @Idem
    @Log("编辑角色菜单")
    @PutMapping("/{roleId}")
    public Result<Void> editRoleMenu(@PathVariable @Min(7) Long roleId, @RequestBody @Valid RoleMenuDTO roleMenu) {
        roleMenuHandlerService.editRoleMenu(roleId, roleMenu);
        return Result.ok();
    }

    /**
     * 删除角色菜单对应关系
     *
     * @param roleId 角色id
     */
    @Idem
    @Log("删除角色菜单")
    @DeleteMapping("/{roleId}")
    public Result<Void> deleteRoleMenu(@PathVariable @Min(7) Long roleId) {
        roleMenuHandlerService.deleteRoleMenu(roleId);
        return Result.ok();
    }

    @Idem
    @Log("更新角色的状态")
    @PostMapping("/{roleId}/{status}")
    public Result<Void> updateRoleStatus(@PathVariable("roleId") @Min(7) Long roleId,
                                         @PathVariable("status") Boolean status) {
        roleMenuHandlerService.updateRoleStatus(roleId,status);
        return Result.ok();
    }

    /**
     * 获取用户子角色集合
     *
     * @return Set<Roles>
     */
    @Log("获取用户副角色")
    @GetMapping("/role")
    @PreAuthorize(value = "@S.matcher().eq(@S.ROLES,@S.USER).match()")
    public Result<Set<Roles>> getRoleMenu() {
        Set<Roles> subRoles = ISecurity.userMust().getSubRoles();
        return Result.ok(subRoles);
    }

}
