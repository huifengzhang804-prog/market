package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.model.vo.UserPersonVo;
import com.medusa.gruul.user.service.model.dto.UserBlacklistQueryDTO;
import com.medusa.gruul.user.service.model.dto.UserGrowthValueDTO;
import com.medusa.gruul.user.service.model.dto.UserQueryDTO;
import com.medusa.gruul.user.service.model.vo.UserListVO;
import com.medusa.gruul.user.service.service.UserManageService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 会员前端控制器
 *
 * @author WuDi
 * @since 2022-09-13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    private final UserManageService userManageService;

    /**
     * 分页查询会员信息
     *
     * @param userQuery 会员查询参数
     * @return 会员列表
     */
    @GetMapping("list")
    @Log("分页查询会员列表")
    @PreAuthorize("@S.platformPerm('vip:base')")
    public Result<IPage<UserListVO>> getUserList(UserQueryDTO userQuery) {
        return Result.ok(userManageService.getUserList(userQuery));
    }

    /**
     * 获取用户余额
     *
     * @return 用户余额
     */
    @Log("获取用户余额")
    @GetMapping("balance")
    @PreAuthorize("@S.authenticated")
    public Result<Long> getUserBalance() {
        return Result.ok(userManageService.getUserBalance());
    }

    /**
     * 会员余额调整(充值/扣除)
     *
     * @param balanceChangeMessage 余额变化消息体
     * @return Void
     */
    @Idem(500)
    @Log("会员余额调整")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.USER)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'vip:base'))
            .match()
            """)
    @PostMapping("balance/change")
    public Result<Void> userBalanceChange(@RequestBody @Valid DataChangeMessage balanceChangeMessage) {

        ISecurity.match()
                .ifUser(secureUser -> balanceChangeMessage.setUserId(secureUser.getId()));
        balanceChangeMessage.validParam();
        userManageService.userBalanceChange(balanceChangeMessage);
        return Result.ok();
    }

    /**
     * 获取今日新增访客数(授权认证)
     *
     * @return 今日新增访客数量
     */
    @GetMapping("uv")
    @Log("获取今日新增访客数(授权认证)")
    @PreAuthorize("@S.platformPerm('overview')")
    public Result<Long> getTodayUvNum() {
        return Result.ok(userManageService.getTodayUvNum());
    }


    /**
     * 用户详情
     *
     * @param userId 用户id
     * @return 用户详情
     */
    @GetMapping("info")
    @Log("用户详情信息")
    @PreAuthorize("@S.platformPerm('vip:base')")
    public Result<UserListVO> userParticulars(Long userId) {
        return Result.ok(userManageService.getUserParticulars(userId));
    }


    /**
     * 用户黑名单列表查询
     *
     * @param userBlacklistQuery 用户黑名单查询dto
     * @return IPage<UserListVO>
     */
    @GetMapping("blacklist")
    @Log("用户黑名单列表信息")
    @PreAuthorize("@S.platformPerm('vip:base','vip:blacklist')")
    public Result<IPage<UserListVO>> getUserBlacklist(UserBlacklistQueryDTO userBlacklistQuery) {
        return Result.ok(userManageService.getUserBlacklist(userBlacklistQuery));
    }

    @Log("用户个人中心数据")
    @GetMapping("/person")
    @PreAuthorize("@S.authenticated")
    public Result<UserPersonVo> userPerson() {
        Long userId = ISecurity.userMust().getId();
        return Result.ok(userManageService.userPerson(userId));
    }

    /**
     * 会员成长值调整
     *
     * @param userGrowthValue 用户成长值调整
     */
    @Log("会员成长值调整")
    @PostMapping("growthValue/change")
    @PreAuthorize("@S.platformPerm('vip:base')")
    public Result<Void> growthValueBalanceChange(@RequestBody @Valid UserGrowthValueDTO userGrowthValue) {
        userManageService.growthValueBalanceChange(userGrowthValue);
        return Result.ok();
    }


    /**
     * 批量导出用户为excel
     *
     * @param response  响应
     * @param userQuery 用户查询参数
     */
    @Log("批量导出用户为excel")
    @PostMapping("/export")
    @PreAuthorize("@S.platformPerm('vip:base')")
    public void exportUser(HttpServletResponse response,
                           @RequestBody UserQueryDTO userQuery
    ) {
        userManageService.exportUser(response, userQuery);
    }
    @Log("取登录用户的【昵称、手机号】")
    @GetMapping("/getUserInfo")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN)
            .match()
            """)
    public Result<UserDataVO> queryUserData() {
        SecureUser<Object> user = ISecurity.userMust();
        return Result.ok(
                new UserDataVO()
                        .setNickname(user.getUsername())
                        .setMobile(user.getMobile())
        );
    }

}

