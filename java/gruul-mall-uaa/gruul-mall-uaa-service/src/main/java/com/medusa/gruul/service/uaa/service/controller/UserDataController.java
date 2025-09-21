package com.medusa.gruul.service.uaa.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.service.uaa.api.dto.UserAuthorityDTO;
import com.medusa.gruul.service.uaa.service.model.dto.UserDataDTO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAccountVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserDataVO;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 个人资料控制器
 *
 * @author 张治保
 * date 2022/5/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/uaa/user/data")
public class UserDataController {

    private final UserDataHandlerService userDataHandlerService;


    /**
     * 查询我的账号资料
     *
     * @return 账户资料
     */
    @Log("查询我的账号资料")
    @GetMapping("/account")
    @PreAuthorize("@S.authenticated")
    public Result<UserAccountVO> myAccount() {
        return Result.ok(
                this.userDataHandlerService.getMyAccount()
        );
    }


    /**
     * 查询个人资料
     *
     * @return 查询结果
     */
    @GetMapping
    @Log("查询个人资料")
    @PreAuthorize("@S.authenticated")
    public Result<UserDataVO> myData() {
        UserDataVO userDataVo = this.userDataHandlerService.getUserDataById(ISecurity.userMust().getId());
        userDataVo.setPhone(ISecurity.userMust().getMobile());
        return Result.ok(userDataVo);
    }

    /**
     * 根据用户id查询用户资料
     *
     * @param userId 用户id
     * @return 查询结果
     */
    @GetMapping("/{userId}")
    @Log("根据用户id查询用户资料")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN).match()")
    public Result<UserDataVO> getUserDataByUserId(@PathVariable Long userId) {
        return Result.ok(
                this.userDataHandlerService.getUserDataById(userId)
        );
    }

    /**
     * 修改个人资料
     *
     * @param userData 个人资料数据
     */
    @PostMapping
    @Log("修改个人资料")
    @PreAuthorize("@S.authenticated")
    public Result<Void> saveData(@RequestBody @Valid UserDataDTO userData) {
        this.userDataHandlerService.saveUserData(userData);
        return Result.ok();
    }

    /**
     * 修改用户权限
     *
     * @param authority 用户权限
     */
    @Log("修改用户权限")
    @PostMapping("/update/authority")
    @PreAuthorize("@S.platformPerm('vip:blacklist','vip:base')")
    public Result<Void> setUserAuthority(@RequestBody @Valid UserAuthorityDTO authority) {
        this.userDataHandlerService.setUserAuthority(authority);
        return Result.ok();
    }

    /**
     * 批量导入用户
     *
     * @param file 文件
     * @return 导入结果
     */
    @Log("批量导入用户")
    @PostMapping("/import")
    @PreAuthorize("@S.platformPerm('vip:blacklist','vip:base')")
    public Result<Void> importUser(@RequestParam("file") MultipartFile file) {
        userDataHandlerService.importUsers(file);
        return Result.ok();
    }


}
