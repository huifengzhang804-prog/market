package com.medusa.gruul.addon.ic.modules.opens.judanke.core;

import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeConfig;
import com.medusa.gruul.addon.ic.modules.opens.judanke.api.*;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@RequiredArgsConstructor
public class JudankeApiFactory implements IJudankeApiFactory {


    private final JudankeConfig config;

    @Override
    public IOrderApi order() {
        return () -> config;
    }

    @Override
    public IShopApi shop() {
        return () -> config;
    }

    @Override
    public IMerchantApi merchant() {
        return () -> config;
    }

    @Override
    public IPrinterApi printer() {
        return () -> config;
    }

    @Override
    public IFinanceApi finance() {
        return () -> config;
    }

    @Override
    public JudankeConfig getConfig() {
        return config;
    }
}
