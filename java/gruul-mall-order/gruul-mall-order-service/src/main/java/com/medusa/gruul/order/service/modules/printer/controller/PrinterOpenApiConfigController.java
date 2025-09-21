package com.medusa.gruul.order.service.modules.printer.controller;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieConfig;
import com.medusa.gruul.order.service.modules.printer.model.dto.FeieConfigDTO;
import com.medusa.gruul.order.service.modules.printer.service.PrinterOpenApiConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打印机 开放平台 api 配置
 *
 * @author 张治保
 * @since 2024/8/22
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@SS.platform('generalSet').match()")
@RequestMapping("/order/printer/open/api")
public class PrinterOpenApiConfigController {

    private final PrinterOpenApiConfigService openApiConfigService;

    /**
     * 配置飞鹅打印机开放平台配置
     *
     * @param config 配置信息
     * @return void
     */
    @PostMapping("/feie")
    public Result<Void> feie(@RequestBody @Valid FeieConfigDTO config) {
        openApiConfigService.feie(config);
        return Result.ok();
    }

    /**
     * 获取飞鹅打印机开放平台配置
     *
     * @return 飞鹅打印机开放平台配置
     */
    @PostMapping("/feie/get")
    public Result<FeieConfig> feieConfig() {
        return Result.ok(
                openApiConfigService.feieConfig()
        );
    }
}
