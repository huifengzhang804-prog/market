package com.medusa.gruul.common.mp;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.config.TenantHandlerConfig;
import com.medusa.gruul.common.mp.config.TransactionalConfig;
import com.medusa.gruul.common.mp.handler.IMetaObjectHandler;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * MybatisPlus 自动装配
 *
 * @author 张治保
 * date 2022/2/10
 */
@Import(
        {
                TenantHandlerConfig.class,
                MybatisPlusConfig.class,
                TransactionalConfig.class
        }
)
@EnableConfigurationProperties(TenantConfigProperties.class)
public class MybatisPlusAutoconfigure {

    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new IMetaObjectHandler();
    }

}
