package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeConfig;

/**
 * @author 张治保
 * @since 2024/8/6
 */
public interface IJudankeApiFactory {


    IOrderApi order();

    IShopApi shop();

    IMerchantApi merchant();


    IFinanceApi finance();

    IPrinterApi printer();

    JudankeConfig getConfig();


}
