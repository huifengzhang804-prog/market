package com.medusa.gruul.common.system.model;

import com.medusa.gruul.common.system.model.remote.SystemFeignRequestInterceptor;
import com.medusa.gruul.common.system.model.filter.SystemFilter;
import com.medusa.gruul.common.system.model.remote.SystemRestTemplateRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @author 张治保
 * date 2022/2/11
 */
@Import(
    {
        SystemFilter.class,
        SystemFeignRequestInterceptor.class,
        SystemRestTemplateRequestInterceptor.class
    }
)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
public class SystemAutoConfigure {
}
