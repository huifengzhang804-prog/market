package com.medusa.gruul.common.mp.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: MybatisPlusConfig.java
 *
 * @author alan
 * date: 2019/7/13 19:21
 */
@ConditionalOnBean(DataSource.class)
@MapperScan("com.medusa.gruul.**.mapper")
public class MybatisPlusConfig {

    public static final IdentifierGenerator IDENTIFIER_GENERATOR = entity -> {
        int classNameHash = Math.abs(entity.getClass().getCanonicalName().hashCode());
        int space = classNameHash % 32;
        return IdUtil.getSnowflake(space, space).nextId();
    };

    /**
     * 分页插件
     *
     * @return PaginationInterceptor
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor paginationInterceptor(@Nullable List<TenantLineHandler> tenantLineHandlers) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //多租户插件
        if (tenantLineHandlers != null && !tenantLineHandlers.isEmpty()) {
            interceptor.setInterceptors(
                    tenantLineHandlers.stream()
                            .map(TenantLineInnerInterceptor::new)
                            .collect(Collectors.toList())
            );
        }
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 自定义id生成器
     */
    @Bean
    @Primary
    public IdentifierGenerator idGenerator() {
        return MybatisPlusConfig.IDENTIFIER_GENERATOR;
    }
}
