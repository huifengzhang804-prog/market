/*
 * Copyright © ${project.inceptionYear} organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.medusa.gruul.common.sharding.jdbc;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.medusa.gruul.common.mp.model.DS;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.apache.shardingsphere.spring.boot.rule.LocalRulesCondition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


/**
 * 1. 建议 spring-boot 2.5.0 以下版本或者发现不加 `@Lazy`, `DataSource shardingSphereDataSource` 是 null 的情况都打开 `@Lazy`
 * 2. Compared with using SpringBoot Starter, if you encounter such problems,
 * you should directly use ShardingSphere's JDBC Driver to configure it as a JDBC data source, that is,
 * use `org.apache.shardingsphere:shardingsphere-jdbc-core:5.2.1` instead of `org.apache.shardingsphere:shardingsphere-jdbc-core-spring-boot-starter:5.2.1`.
 * For more information see <a href="https://shardingsphere.apache.org/document/5.2.1/en/user-manual/shardingsphere-jdbc/yaml-config/jdbc_driver/">JDBC Driver</a>
 * dynamic datasource 整合 shardingJdbc datasource
 *
 * @author 张治保
 * @see org.springframework.context.annotation.Lazy
 * @see org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource
 * @since 2024/05/14
 */
//@Import(ShardingSphereAutoConfiguration.class)
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class)
@Configuration
@Conditional(LocalRulesCondition.class)
@ConditionalOnClass(name = "com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider")
public class DynamicDataSourceShardingDataSourceAutoconfigure {
    private final DynamicDataSourceProperties properties;
    private final DefaultDataSourceCreator dataSourceCreator;
    private final DataSource shardingSphereDataSource;

    public DynamicDataSourceShardingDataSourceAutoconfigure(
            DynamicDataSourceProperties properties,
            DefaultDataSourceCreator dataSourceCreator, @Nullable @Qualifier("shardingSphereDataSource") DataSource shardingSphereDataSource) {
        this.properties = properties;
        this.dataSourceCreator = dataSourceCreator;
        this.shardingSphereDataSource = shardingSphereDataSource;
    }


    /**
     * 注入 sharding jdbc数据源
     *
     * @see ShardingSphereAutoConfiguration#shardingSphereDataSource(ObjectProvider, ObjectProvider)
     */
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new AbstractDataSourceProvider(dataSourceCreator) {
            @Override
            public Map<String, DataSource> loadDataSources() {
                if (shardingSphereDataSource == null) {
                    return Map.of();
                }
                return Map.of(DS.SHARDING_SPHERE_DS, shardingSphereDataSource);
            }
        };
    }

    /**
     * 将 dynamic-datasource 设置为首选的
     * 当 Spring 存在多个数据源时, 自动注入的是首选的对象
     * 设置为主要的数据源之后，就可以支持 shardingSphere 原生的配置方式了
     */
    @Primary
    @Bean
    public DataSource routingDataSource(List<DynamicDataSourceProvider> providers) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource(providers);
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }
}