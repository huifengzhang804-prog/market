package com.medusa.gruul.addon.platform.controller;

import com.medusa.gruul.addon.platform.model.dto.ClientConfigDTO;
import com.medusa.gruul.addon.platform.model.enums.ClientConfigType;
import com.medusa.gruul.addon.platform.mp.entity.PlatformClientConfig;
import com.medusa.gruul.addon.platform.service.ClientConfigService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 客户端配置控制器
 *
 * @author 张治保
 * date 2022/4/25
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.superAdmin")
@RequestMapping("/platform/client/config")
public class ClientConfigController {

    private final ClientConfigService clientConfigService;

    /**
     * 根据客户端查询配置信息
     *
     * @param type 配置类型
     * @return 查询结果
     */
    @Log("根据配置类型查询配置")
    @GetMapping("/{type}")
    public Result<PlatformClientConfig> clientConfig(@PathVariable ClientConfigType type) {
        return Result.ok(
                clientConfigService.clientConfig(type)
        );
    }

    /**
     * 新增客户端配置
     *
     * @param clientConfig 配置数据
     */
    @PostMapping
    public Result<Void> newClientConfig(@RequestBody @Valid ClientConfigDTO clientConfig) {
        clientConfigService.newClientConfig(clientConfig);
        return Result.ok();
    }

    /**
     * 编辑客户端配置
     *
     * @param configId     配置id
     * @param clientConfig 配置详情
     */
    @PutMapping("/{configId}")
    public Result<Void> editClientConfig(@PathVariable Long configId, @RequestBody ClientConfigDTO clientConfig) {
        clientConfigService.editClientConfig(configId, clientConfig);
        return Result.ok();
    }

}
