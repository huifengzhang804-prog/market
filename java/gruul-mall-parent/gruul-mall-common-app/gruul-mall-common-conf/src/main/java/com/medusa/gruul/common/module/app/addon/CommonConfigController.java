package com.medusa.gruul.common.module.app.addon;

import com.medusa.gruul.common.model.resp.Result;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/common/config/{type}")
public class CommonConfigController {

    private final CommonConfigStrategyFactory strategyFactory;

    @GetMapping
    @PermitAll
    public Result<Object> getConfig(@PathVariable String type) {
        return Result.ok(
                strategyFactory.execute(type).get()
        );
    }

    @PostMapping
    public Result<Void> setConfig(@PathVariable String type, @Config @Validated Object config) {
        strategyFactory.execute(type).setConf(config);
        return Result.ok();
    }

}
