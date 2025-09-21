package com.medusa.gruul.addon.ic.modules.opens.uupt.core;

import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@RequiredArgsConstructor
public class UuptApiFactory implements IUuptApiFactory {

    protected final UuptConfig config;

    @Override
    public final UuptConfig config() {
        return config;
    }

}
