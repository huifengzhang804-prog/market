package com.medusa.gruul.service.uaa.service.model.dto;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.UserStatus;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.constant.RegexPools;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Set;


/**
 * @author 张治保
 * date 2022/4/19
 */
@Getter
@Setter
@ToString
public class ShopCustomAdminDTO {

    /**
     * 角色id
     */
    @NotNull
    @Min(7)
    private Long roleId;
    /**
     * 用户id
     */
    @Min(1)
    private Long userId;
    /**
     * 昵称
     */
    @NotBlank
    @Pattern(regexp = RegexPools.NICKNAME,message = "昵称 只允许中英文与数字")
    private String nickname;
    /**
     * 用户名
     */
//    @Pattern(regexp = RegexPools.NICKNAME,message = "用户名 只允许中英文与数字")
    private String username;
    /**
     * 密码
     */
//    @Pattern(regexp = RegexPools.PASSWORD,message = "密码包含数字、小写字母、大写字母 至少两种 6-20位")
    private String password;
    /**
     * 二次确认密码
     */
//    @Pattern(regexp = RegexPools.PASSWORD,message = "密码包含数字、小写字母、大写字母 至少两种 6-20位")
    private String confirmPassword;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
//    @Pattern(regexp = RegexPool.EMAIL)
    private String email;

    public void newShopCustomAdmin(
            IUserService userService,
            PasswordEncoder passwordEncoder,
            IUserRoleService userRoleService,
            IShopUserDataService shopUserDataService) {
        this.checkParam(true, userService, userRoleService);

        Long userId = getUserId(userService, passwordEncoder,"123456");
        boolean success;
        success = userRoleService.save(
                new UserRole()
                        .setUserId(userId)
                        .setRoleId(getRoleId())
                        .setEnable(Boolean.TRUE)
        );
        if (!success) {
            throw SystemCode.DATA_ADD_FAILED.exception();
        }
        success = shopUserDataService.save(
                new ShopUserData()
                        .setUserId(userId)
                        .setNickname(getNickname())
                        .setMobile(getMobile())
                        .setEmail(getEmail())
        );
        if (!success) {
            throw SystemCode.DATA_ADD_FAILED.exception();
        }
    }


    private void checkParam(boolean isNew, IUserService userService, IUserRoleService userRoleService) {
        Long userId = getUserId();
        if (userId != null) {
            User one = userService.lambdaQuery()
                    .eq(User::getId, userId)
                    .eq(User::getStatus, UserStatus.NORMAL)
                    .one();
            if (one == null) {
                throw SystemCode.DATA_ADD_FAILED.exception();
            }
            this.setMobile(one.getMobile());
            if (isNew) {
                Final<Roles> box = new Final<>();
                ISecurity.match()
                        .ifAnySuperAdmin(secureUser -> box.set(Roles.SUPER_CUSTOM_ADMIN))
                        .ifAnyShopAdmin(secureUser -> box.set(Roles.CUSTOM_ADMIN));
                Long otherRoleId = userRoleService.customerAdminRoleId(box.get(), userId);
                if (otherRoleId == null) {
                    return;
                }
                throw UaaError.USER_HAS_OTHER_ROLE.exception();
            }
            return;
        }

//        String username = getUsername();
//        String password = getPassword();
//        String confirmPassword = getConfirmPassword();
        String currentMobile = getMobile();
//        if (StrUtil.isBlank(username) || StrUtil.isBlank(password) || StrUtil.isBlank(confirmPassword) || StrUtil.isBlank(currentMobile)) {
//            throw SystemCode.PARAM_VALID_ERROR.exception();
//        }
        if (!currentMobile.matches(RegexPool.MOBILE)) {
            throw UaaError.MOBILE_INCORRECT_FORMAT.exception();
        }
//        if (!StrUtil.equals(password, confirmPassword)) {
//            throw UaaError.CONFIRM_PASSWORD_INCORRECT.exception();
//        }
//        if (userService.lambdaQuery().eq(User::getUsername, username).exists()) {
//            throw UaaError.USERNAME_HAS_BEEN_USED.exception();
//        }
        //校验是否存在相同手机号的用户
        if (userService.lambdaQuery().eq(User::getMobile, getMobile()).exists()) {
            throw UaaError.MOBILE_HAS_BEEN_USED.exception();
        }
    }


    public void editShopCustomAdmin(
            Long dataId,
            IUserService userService,
            PasswordEncoder passwordEncoder,

            IUserRoleService userRoleService,
            IShopUserDataService shopUserDataService
    ) {
        this.checkParam(false, userService, userRoleService);

        ShopUserDataVO shopUserData = shopUserDataService.shopUserDataById(dataId);
        if (shopUserData == null) {
            throw SystemCode.DATA_UPDATE_FAILED.exception();
        }
        Long preUserId = shopUserData.getUserRole().getUser().getId();
        Long userId = getUserId(userService, passwordEncoder,getPassword());
        Long roleId = getRoleId();
        /*
         * 是否是同一个用户 是否是同一个角色
         */
        boolean sameUser = userId.equals(preUserId);
        boolean sameRole = roleId.equals(shopUserData.getUserRole().getRole().getId());

        if (!sameRole || !sameUser) {
            boolean success = userRoleService.lambdaUpdate()
                    .eq(UserRole::getId, shopUserData.getUserRole().getId())
                    .set(!sameRole, UserRole::getRoleId, roleId)
                    .set(!sameUser, UserRole::getUserId, userId)
                    .update();
            if (!success) {
                throw SystemCode.DATA_UPDATE_FAILED.exception();
            }
        }
        boolean success = shopUserDataService.lambdaUpdate()
                .eq(ShopUserData::getId, shopUserData.getId())
                .set(!sameUser, ShopUserData::getUserId, userId)
                .set(ShopUserData::getNickname, getNickname())
                .set(ShopUserData::getMobile, getMobile())
                .set(ShopUserData::getEmail, getEmail())
                .update();
        if (!success) {
            throw SystemCode.DATA_UPDATE_FAILED.exception();
        }
        /* 让之前的用户下线
         */
        if (!sameUser) {
            ISecurity.kickUsers(ClientType.SHOP_CONSOLE, ISecurity.userMust().getShopId(), Set.of(preUserId), "更换管理员");
        }
        //刷新当前用户的权限数据
        ISecurity.refreshUsers(ClientType.SHOP_CONSOLE, ISecurity.userMust().getShopId(), Set.of(userId));
    }

    private Long getUserId(IUserService userService, PasswordEncoder passwordEncoder,String password) {
        boolean success;
        Long userId = getUserId();
        if (userId == null) {
            User user = new User()
//                    .setUsername(getUsername())
                    //默认密码
                    .setPassword(passwordEncoder.encode(password))
                    .setMobile(getMobile())
                    .setStatus(UserStatus.NORMAL);
            success = userService.save(user);

            userId = user.getId();
            this.setUserId(userId);
        }{
            if (Objects.nonNull(password)) {
                //更新老用户的密码为123456
                success = userService.lambdaUpdate()
                        .eq(User::getId, userId)
                        .set(User::getPassword, passwordEncoder.encode(password))
                        .update();
                if (!success) {
                    throw SystemCode.DATA_ADD_FAILED.exception();
                }
            }
        }

        return userId;
    }
}
