package com.medusa.gruul.common.module.app.addon;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Component
@RequiredArgsConstructor
public class CommonConfigStrategyFactory extends AbstractStrategyFactory<String, Void, CommonConfigService<?>> {

    @Nullable
    private final List<CommonConfigService<?>> addonConfigServices;

    @Override
    public Map<String, Supplier<? extends IStrategy<String, Void, CommonConfigService<?>>>> getStrategyMap() {
        if (CollUtil.isEmpty(addonConfigServices)) {
            return Map.of();
        }
        return addonConfigServices.stream()
                .collect(
                        Collectors.toMap(
                                CommonConfigService::type,
                                addonConfigService -> () -> (type, param) -> addonConfigService
                        )
                );
    }
}
