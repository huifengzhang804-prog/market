package com.medusa.gruul.common.module.app.addon;

import org.springframework.context.annotation.Import;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Import({
        CommonConfigStrategyFactory.class,
        CommonConfigController.class,
        CommonConfigMvcConfigure.class
})
public class CommonAddonAutoconfigure {


}
