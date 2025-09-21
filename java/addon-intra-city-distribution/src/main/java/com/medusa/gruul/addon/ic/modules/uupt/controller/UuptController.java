package com.medusa.gruul.addon.ic.modules.uupt.controller;

import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptConfigDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptShopAuthDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptShopSmsDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptShopRechargeVO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptShopSmsVO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptStatusVO;
import com.medusa.gruul.addon.ic.modules.uupt.service.UuptService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UU 跑腿控制器
 *
 * @author 张治保
 * @since 2024/8/13
 */
@RestController
@RequestMapping("/ic/uupt")
@RequiredArgsConstructor
public class UuptController {

    private final UuptService uuptService;

    /**
     * 设置 uupt 开放平台配置
     *
     * @param param 配置参数
     * @return void
     */
    @PostMapping("/open/config")
    @PreAuthorize("@SS.platform('generalSet').match()")
    public Result<Void> saveOpenConfig(@RequestBody @Valid UuptConfigDTO param) {
        uuptService.saveOpenConfig(param);
        return Result.ok();
    }

    /**
     * 获取 uupt 开放平台配置
     *
     * @return uupt 开放平台配置
     */
    @PreAuthorize("@SS.platform('generalSet').match()")
    @PostMapping("/open/config/get")
    public Result<UuptConfig> getOpenConfig() {
        return Result.ok(
                uuptService.getOpenConfig()
        );
    }


    /**
     * 查询店铺 uupt 账号激活状态
     *
     * @return 店铺uupt账号激活状态
     */
    @PostMapping("/shop/status")
    @PreAuthorize("@SS.shop('intra:city').match()")
    public Result<UuptStatusVO> status() {
        return Result.ok(
                uuptService.status(ISecurity.userMust().getShopId())
        );
    }

    /**
     * 商家发送短信验证码进行授权
     *
     * @param param 发送短信验证码参数
     * @return 发送结果
     */
    @Idem
    @PostMapping("/shop/sms")
    @PreAuthorize("@SS.shop('intra:city').match()")
    public Result<UuptShopSmsVO> sendSms(@RequestBody @Valid UuptShopSmsDTO param) {
        return Result.ok(
                uuptService.sendSms(param)
        );
    }

    /**
     * 商家用户授权
     *
     * @param param 授权参数
     * @return void
     */
    @Idem
    @PostMapping("/shop/auth")
    @PreAuthorize("@SS.shop('intra:city').match()")
    public Result<Void> auth(@RequestBody @Valid UuptShopAuthDTO param) {
        uuptService.auth(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }


    /**
     * 获取 uu跑腿 充值二维码
     *
     * @return 充值二维码
     */
    @PostMapping("/shop/recharge")
    @PreAuthorize("@SS.shop('intra:city').match()")
    public Result<UuptShopRechargeVO> recharge() {
        return Result.ok(
                uuptService.recharge(ISecurity.userMust().getShopId())
        );
    }

}
