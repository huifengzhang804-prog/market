package com.medusa.gruul.addon.ic.modules.ic.controller;

import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopConfigDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.ShopConfigVO;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopConfigService;
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
 * 同城店铺配置控制器
 *
 * @author 张治保
 * @since 2024/8/13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ic/shop/config")
@PreAuthorize("@S.shopPerm('intra:city')")
public class ICShopConfigController {

    private final ICShopConfigService icShopConfigService;

    /**
     * 保存店铺同城配置
     *
     * @param config 店铺同城配置
     * @return void
     */
    @Idem
    @PostMapping
    public Result<Void> saveConfig(@RequestBody @Valid ICShopConfigDTO config) {
        config.validParam();
        icShopConfigService.saveConfig(ISecurity.userMust().getShopId(), config);
        return Result.ok();
    }

    /**
     * 查询店铺同城配置信息
     *
     * @return 查询结果
     */
    @PostMapping("/get")
    public Result<ShopConfigVO> getConfig() {
        return Result.ok(
                icShopConfigService.getConfig(ISecurity.userMust().getShopId())
        );
    }

}
