package com.medusa.gruul.common.web;

import cn.hutool.extra.spring.SpringUtil;
import com.medusa.gruul.common.web.config.RestExceptionConfig;
import com.medusa.gruul.common.web.config.WebAppConfig;
import org.springframework.context.annotation.Import;

/**
 * web插件自动装配
 *
 * @author 张治保
 * date 2022/2/10
 */

@Import({
        WebAppConfig.class,
        RestExceptionConfig.class,
        SpringUtil.class
})
public class WebAutoconfigure {


}
