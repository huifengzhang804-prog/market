package com.medusa.gruul.order.service.modules.printer.feie.api;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@NoArgsConstructor
@AllArgsConstructor
public class FeieApi implements IFeieApi {

    protected FeieConfig config;


    @Override
    public FeieConfig config() {
        return config;
    }
}
