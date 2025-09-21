package com.medusa.gruul.service.uaa.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.dto.AvailableUserPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminDTO;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.service.ShopAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * 店铺管理员控制器
 *
 * @author 张治保
 * date 2022/4/19
 */
@RestController
@RequiredArgsConstructor
//@PreAuthorize("@S.anyPerm('perm', 'uaa:shop:admin')")
@PreAuthorize("@S.matcher().anyRole(@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)" +
        ".or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).perm('perm'))" +
        ".or(@S.consumer().role(@S.SHOP_CUSTOM_ADMIN).perm('uaa:shop:admin')" +
        ".or(@S.consumer().role(@S.R.SUPPLIER_CUSTOM_ADMIN).perm('uaa:supplier:admin'))).match()"
)
@RequestMapping("/uaa/shop/admin")
public class ShopUserController {

    private final ShopAdminService shopAdminService;

    /**
     * 获取我的店铺资料
     *
     * @return 查询结果
     */
    @Log("获取我的店铺资料")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN, @S.PLATFORM_CUSTOM_ADMIN,@S.SHOP_ADMIN, @S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN).match()")
    @GetMapping("/mine")
    public Result<ShopUserData> mine() {
        return Result.ok(
                this.shopAdminService.myData()
        );
    }

    /**
     * 查询当前店铺可用作管理员的用户
     *
     * @param availableUserPage 查询条件
     * @return 查询结果
     */
    @Log("分页查询当前店铺可用作管理员的用户")
    @GetMapping("/available")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES, @S.PLATFORM_ADMIN, @S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'perm','shopList'))
            .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'uaa:shop:admin'))
            .match()
            """)
    public Result<IPage<UserWithDataVO>> getAvailableUser(AvailableUserPageDTO availableUserPage) {
        ISecurity.match()
                .ifAnyShopAdmin(
                        user -> availableUserPage.setExcludeShopId(user.getShopId()).setClientType(ClientType.SHOP_CONSOLE)
                );
        return Result.ok(
                this.shopAdminService.getAvailableUserForShopAdmin(availableUserPage)
        );
    }

    /**
     * 店铺用户(管理员)资料分页查询
     *
     * @param shopCustomAdminPage 分页参数
     * @return 查询结果
     */
    @GetMapping
    @Log("店铺管理员分页查询")
    public Result<IPage<ShopUserDataVO>> shopCustomAdminPage(ShopCustomAdminPageDTO shopCustomAdminPage) {
        return Result.ok(
                this.shopAdminService.shopUserDataPage(shopCustomAdminPage)
        );
    }

    /**
     * 新增店铺管理员
     *
     * @param customAdmin 管理员数据
     */
    @Idem
    @PostMapping
    @Log("新增店铺管理员")
    public Result<String> newShopCustomAdmin(@RequestBody @Valid ShopCustomAdminDTO customAdmin) {
        this.shopAdminService.newShopCustomAdmin(customAdmin);
        return Result.ok("用户创建成功,初始密码:123456");
    }

    /**
     * 根据用户id查询管理员注册资料
     *
     * @param userId 用户id
     * @return 查询出的用户资料
     */
    @GetMapping("/{userId}")
    @Log("根据用户id查询管理员注册资料")
    public Result<UserWithDataVO> getAdminRegisterDataById(@PathVariable Long userId) {
        return Result.ok(
                this.shopAdminService.getAdminRegisterDataById(userId)
        );
    }

    /**
     * 编辑店铺管理员
     *
     * @param dataId      资料id
     * @param customAdmin 管理员数据
     */
    @Idem
    @Log("编辑店铺管理员")
    @PutMapping("/{dataId}")
    public Result<Void> editShopCustomAdmin(@PathVariable Long dataId, @RequestBody @Valid ShopCustomAdminDTO customAdmin) {
        this.shopAdminService.editShopCustomAdmin(dataId, customAdmin);
        return Result.ok();
    }

    /**
     * 启用/禁用 店铺管理员
     *
     * @param dataId 资料id
     * @param enable 启用/禁用
     */
    @Log("启用/禁用用户与店铺管理绑定关系")
    @Idem(expire = 500)
    @PatchMapping("/{dataId}/{enable}")
    public Result<Void> enableDisableShopCustomAdmin(@PathVariable Long dataId, @PathVariable Boolean enable) {
        this.shopAdminService.enableDisableShopCustomAdmin(dataId, enable);
        return Result.ok();
    }

    /**
     * 移除用户和店铺管理员绑定关系
     *
     * @param dataId 管理员资料id
     */
    @Log("移除用户与店铺管理员绑定关系")
    @Idem(expire = 500)
    @DeleteMapping("/{dataId}")
    public Result<Void> deleteShopCustomAdmin(@PathVariable Long dataId) {
        this.shopAdminService.deleteShopCustomAdmin(dataId);
        return Result.ok();
    }

    /**
     * 分页查询可成为主播的用户
     *
     * @param availableUserPage 查询参数
     * @return 主播列表
     */
    @Log("分页查询可成为主播的用户")
    @GetMapping("/anchor/list")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES, @S.PLATFORM_ADMIN, @S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'perm','shopList'))
            .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'uaa:shop:admin'))
            .match()
            """)
    public Result<IPage<UserAnchorVO>> anchorList(AvailableUserPageDTO availableUserPage) {
        return Result.ok(
                this.shopAdminService.anchorList(availableUserPage)
        );
    }
}
