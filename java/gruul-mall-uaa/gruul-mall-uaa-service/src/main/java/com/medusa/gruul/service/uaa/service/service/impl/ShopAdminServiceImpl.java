package com.medusa.gruul.service.uaa.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.UserStatus;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.service.model.dto.AvailableUserPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminDTO;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminPageDTO;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.model.po.ShopAdminRoleMap;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.ShopAdminService;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import com.medusa.gruul.shop.api.model.dto.ShopAdminMapDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/4/19
 */
@Service
@RequiredArgsConstructor
public class ShopAdminServiceImpl implements ShopAdminService {

    private final IUserService userService;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final IUserRoleService userRoleService;
    private final IShopUserDataService shopUserDataService;
    private final UserDataHandlerService userDataHandlerService;

    @Override
    @Log("店铺管理员修改")
    @Redisson(value = UaaConstant.UAA_SHOP_LOCK, key = "#shopAdminMap.shopId")
    @Transactional(rollbackFor = Exception.class)
    public void shopAdminChange(ShopAdminMapDTO shopAdminMap) {
        Long userId = shopAdminMap.getUserId();
        User admin = userService.getById(userId);
        if (admin == null || UserStatus.NORMAL != admin.getStatus()) {
            throw UaaError.USER_STATUS_ERROR.exception();
        }
        Long shopId = shopAdminMap.getShopId();
        Roles adminRole = getAdminRole(shopAdminMap.getShopMode());
        ShopAdminRoleMap shopAdminRoleMap = ISystem.shopId(shopId, () -> userRoleService.shopAdminCurrent(adminRole));
        if (shopAdminRoleMap == null) {
            newShopAdmin(adminRole, shopId, shopAdminMap);
            return;
        }
        if (shopAdminRoleMap.getUserId().equals(userId)) {
            return;
        }
        //角色绑定到新用户
        Boolean success = ISystem.shopId(
                shopId,
                () -> userRoleService.lambdaUpdate()
                        .set(UserRole::getUserId, userId)
                        .eq(UserRole::getId, shopAdminRoleMap.getUserRoleId())
                        .eq(UserRole::getRoleId, shopAdminRoleMap.getRoleId())
                        .eq(UserRole::getUserId, shopAdminRoleMap.getUserId())
                        .update()
        );
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        ISecurity.kickUsers(adminRole.getClientType(), shopId, Set.of(shopAdminRoleMap.getUserId()), "管理员变更");
    }

    private Roles getAdminRole(ShopMode shopMode) {
        return ShopMode.SUPPLIER == shopMode ? Roles.SUPPLIER_ADMIN : Roles.ADMIN;
    }

    private void newShopAdmin(Roles adminRole, Long shopId, ShopAdminMapDTO shopAdminMap) {
        Role role = ISystem.shopId(
                shopId, () -> {
                    Role inRole = new Role()
                            .setName(adminRole.getDesc())
                            .setValue(adminRole)
                            .setEnabled(Boolean.TRUE)
                            .setClientType(adminRole.getClientType());
                    SystemCode.DATA_ADD_FAILED.falseThrow(roleService.save(inRole));
                    return inRole;
                }
        );
        /* 保存 用户与角色关系
         */
        boolean success = ISystem.shopId(
                shopId,
                () -> userRoleService.save(
                        new UserRole()
                                .setRoleId(role.getId())
                                .setUserId(shopAdminMap.getUserId())
                                .setEnable(BooleanUtil.isTrue(shopAdminMap.getEnable()))
                )
        );
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }

    @Override
    @Log("店铺管理员禁用启用")
    @Redisson(value = UaaConstant.UAA_SHOP_LOCK, batchParamName = "#shopsEnableDisable.shopIds", key = "#item")
    public void shopEnableDisable(ShopsEnableDisableDTO shopsEnableDisable) {
        Boolean enable = BooleanUtil.isTrue(shopsEnableDisable.getEnable());
        Set<Long> shopIds = shopsEnableDisable.getShopIds();
        //更新设置可用状态
        TenantShop.disable(() -> {
            boolean exists = userRoleService.lambdaQuery()
                    .eq(UserRole::getEnable, !enable)
                    .in(UserRole::getShopId, shopIds)
                    .exists();
            if (!exists) {
                return;
            }
            boolean success = userRoleService.lambdaUpdate()
                    .set(UserRole::getEnable, enable)
                    .eq(UserRole::getEnable, !enable)
                    .in(UserRole::getShopId, shopIds)
                    .update();
            SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        });
        if (enable) {
            return;
        }
        ISecurity.offlineAllUsers(ClientType.SHOP_CONSOLE, shopIds);
        ISecurity.offlineAllUsers(ClientType.SUPPLIER_CONSOLE, shopIds);
    }

    @Override
    public IPage<ShopUserDataVO> shopUserDataPage(ShopCustomAdminPageDTO shopCustomAdminPage) {
        ISecurity.match()
                .ifAnySuperAdmin(secureUser -> {
                    shopCustomAdminPage.setRoles(Roles.SUPER_CUSTOM_ADMIN);
                })
                .ifAnySupplierAdmin(secureUser -> {
                    shopCustomAdminPage.setRoles(Roles.SUPPLIER_CUSTOM_ADMIN);
                })
                .ifAnyShopAdmin(secureUser -> {
                    shopCustomAdminPage.setRoles(Roles.CUSTOM_ADMIN);
                });
        shopCustomAdminPage.setCurrentUserId(ISecurity.userMust().getId());
        return shopUserDataService.shopUserDataPage(shopCustomAdminPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = UaaConstant.USER_LOGIN_REG_LOCK, key = "#customAdmin.mobile")
    public void newShopCustomAdmin(ShopCustomAdminDTO customAdmin) {
        boolean isNew = customAdmin.getUserId() == null;
        customAdmin.newShopCustomAdmin(userService, passwordEncoder, userRoleService, shopUserDataService);
        if (isNew) {
            userDataHandlerService.generateUserDataAndSave(customAdmin.getUserId(), customAdmin.getMobile());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editShopCustomAdmin(Long dataId, ShopCustomAdminDTO customAdmin) {
        customAdmin.editShopCustomAdmin(dataId, userService, passwordEncoder, userRoleService, shopUserDataService);
    }

    @Override
    public void enableDisableShopCustomAdmin(Long dataId, Boolean enable) {
        ShopUserDataVO userData = shopUserDataService.shopUserDataById(dataId);
        if (userData == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        boolean success = userRoleService.lambdaUpdate()
                .eq(UserRole::getId, userData.getUserRole().getId())
                .set(UserRole::getEnable, enable)
                .update();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        /* 用户下线
         */
        ISecurity.offlineUsers(ClientType.SHOP_CONSOLE, ISecurity.userMust().getShopId(), userData.getUserRole().getUser().getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShopCustomAdmin(Long dataId) {
        Final<Tuple2<Long, ClientType>> finalBox = new Final<>();
        ISecurity.match()
                .ifAnySuperAdmin((secureUser) -> finalBox.set(Tuple.of(secureUser.getShopId(), ClientType.PLATFORM_CONSOLE)))
                .ifAnyShopAdmin((secureUser) -> finalBox.set(Tuple.of(secureUser.getShopId(), ClientType.SHOP_CONSOLE)));
        ShopUserDataVO userData = shopUserDataService.shopUserDataById(dataId);
        if (userData == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (userData.getUserRole().getEnable()) {
            throw UaaError.CAN_NOT_DELETE_ENABLE_ADMIN.exception();
        }
        boolean success = userRoleService.removeById(userData.getUserRole().getId());
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        success = shopUserDataService.removeById(userData.getId());
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        /* 让该用户下线
         */
        ISecurity.offlineUsers(finalBox.get()._2(), finalBox.get()._1(), userData.getUserRole().getUser().getId());
    }

    @Override
    public IPage<UserWithDataVO> getAvailableUserForShopAdmin(AvailableUserPageDTO availableUserPage) {
        return TenantShop.disable(() -> userService.getAvailableUserForShopAdmin(availableUserPage));
    }

    @Override
    public UserWithDataVO getAdminRegisterDataById(Long userId) {
        List<UserWithDataVO> userDataBatchByUserId = userService.getUserDataBatchByUserId(Set.of(userId),
                ISecurity.matcher()
                        .anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN)
                        .match()
        );
        return CollUtil.isEmpty(userDataBatchByUserId) ? null : userDataBatchByUserId.get(CommonPool.NUMBER_ZERO);
    }

    @Override
    public ShopUserData myData() {
        SecureUser<?> secureUser = ISecurity.userMust();
        return new ShopUserData()
                .setNickname(secureUser.getNickname())
                .setUserId(secureUser.getId())
                .setMobile(secureUser.getMobile());
    }

    /**
     * 分页查询可成为主播的用户
     *
     * @param availableUserPage 查询参数
     * @return 主播列表
     */
    @Override
    public IPage<UserAnchorVO> anchorList(AvailableUserPageDTO availableUserPage) {
        return TenantShop.disable(() -> userService.anchorList(availableUserPage));
    }
}
