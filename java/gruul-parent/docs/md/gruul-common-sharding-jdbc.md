# com.medusa.gruul.common.sharding.jdbc.DynamicDataSourceShardingDataSourceAutoconfigure

**文件路径**: `sharding\jdbc\DynamicDataSourceShardingDataSourceAutoconfigure.java`

## 代码文档
///
1. 建议 spring-boot 2.5.0 以下版本或者发现不加 `@Lazy`, `DataSource shardingSphereDataSource` 是 null 的情况都打开 `@Lazy`
2. Compared with using SpringBoot Starter, if you encounter such problems,
you should directly use ShardingSphere's JDBC Driver to configure it as a JDBC data source, that is,
use `org.apache.shardingsphere:shardingsphere-jdbc-core:5.2.1` instead of `org.apache.shardingsphere:shardingsphere-jdbc-core-spring-boot-starter:5.2.1`.
For more information see <a href="https://shardingsphere.apache.org/document/5.2.1/en/user-manual/shardingsphere-jdbc/yaml-config/jdbc_driver/">JDBC Driver</a>
dynamic datasource 整合 shardingJdbc datasource

@author 张治保
@see org.springframework.context.annotation.Lazy
@see org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource
@since 2024/05/14
 
///

///
注入 sharding jdbc数据源

@see ShardingSphereAutoConfiguration#shardingSphereDataSource(ObjectProvider, ObjectProvider)
     
///

///
将 dynamic-datasource 设置为首选的
当 Spring 存在多个数据源时, 自动注入的是首选的对象
设置为主要的数据源之后，就可以支持 shardingSphere 原生的配置方式了
     
///


## 文件结构
```
项目根目录
└── sharding\jdbc
    └── DynamicDataSourceShardingDataSourceAutoconfigure.java
```

## 完整代码
```java
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
```

# com.medusa.gruul.common.sharding.jdbc.ShardingHelper

**文件路径**: `sharding\jdbc\ShardingHelper.java`

## 代码文档
///
@author 张治保
@since 2024/5/9
 
///

///
获取分库分表工具

@param db    数据库数量 如 4
@param table 表数量 如 8
@return DbTableNum
     
///


## 文件结构
```
项目根目录
└── sharding\jdbc
    └── ShardingHelper.java
```

## 完整代码
```java
package com.medusa.gruul.common.sharding.jdbc;

/**
 * @author 张治保
 * @since 2024/5/9
 */
public interface ShardingHelper {

    /**
     * 获取分库分表工具
     *
     * @param db    数据库数量 如 4
     * @param table 表数量 如 8
     * @return DbTableNum
     */
    static DbTableNum dbTableNum(int db, int table) {
        return new DbTableNum(db, table);
    }

    record DbTableNum(int db, int table) {

        public int db(long shardingId) {
            return (int) shardingId % db;
        }

        public int table(long shardingId) {
            return (int) shardingId / db % table;
        }


    }


}

```

# com.medusa.gruul.common.sharding.jdbc.nacos.NacosClusterPersistRepository

**文件路径**: `jdbc\nacos\NacosClusterPersistRepository.java`

## 代码文档
///
todo 暂未实现, 需要请自行实现

@author 张治保
@since 2024/5/13
 
///


## 文件结构
```
项目根目录
└── jdbc\nacos
    └── NacosClusterPersistRepository.java
```

## 完整代码
```java
package com.medusa.gruul.common.sharding.jdbc.nacos;

import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepository;
import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepositoryConfiguration;
import org.apache.shardingsphere.mode.repository.cluster.LeaderExecutionCallback;
import org.apache.shardingsphere.mode.repository.cluster.listener.DataChangedEventListener;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * todo 暂未实现, 需要请自行实现
 *
 * @author 张治保
 * @since 2024/5/13
 */
public class NacosClusterPersistRepository implements ClusterPersistRepository {
    @Override
    public void init(ClusterPersistRepositoryConfiguration clusterPersistRepositoryConfiguration) {

    }

    @Override
    public long getRegistryCenterTime(String s) {
        return 0;
    }

    @Override
    public Object getRawClient() {
        return null;
    }

    @Override
    public int getNumChildren(String s) {
        return 0;
    }

    @Override
    public void addCacheData(String s) {

    }

    @Override
    public void evictCacheData(String s) {

    }

    @Override
    public Object getRawCache(String s) {
        return null;
    }

    @Override
    public void executeInLeader(String s, LeaderExecutionCallback leaderExecutionCallback) {

    }

    @Override
    public void updateInTransaction(String s, String s1) {

    }

    @Override
    public void persistEphemeral(String s, String s1) {

    }

    @Override
    public void persistExclusiveEphemeral(String s, String s1) {

    }

    @Override
    public boolean tryLock(String s, long l) {
        return false;
    }

    @Override
    public void unlock(String s) {

    }

    @Override
    public void watch(String s, DataChangedEventListener dataChangedEventListener, Executor executor) {

    }

    @Override
    public String get(String s) {
        return null;
    }

    @Override
    public String getDirectly(String s) {
        return null;
    }

    @Override
    public List<String> getChildrenKeys(String s) {
        return null;
    }

    @Override
    public boolean isExisted(String s) {
        return false;
    }

    @Override
    public void persist(String s, String s1) {

    }

    @Override
    public void update(String s, String s1) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void close() {

    }
}

```

# com.medusa.gruul.common.sharding.jdbc.nacos.NacosConfigiServiceUtils

**文件路径**: `jdbc\nacos\NacosConfigiServiceUtils.java`

## 代码文档
///
@author 张治保
@since 2024/5/9
 
///


## 文件结构
```
项目根目录
└── jdbc\nacos
    └── NacosConfigiServiceUtils.java
```

## 完整代码
```java
package com.medusa.gruul.common.sharding.jdbc.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.Getter;

/**
 * @author 张治保
 * @since 2024/5/9
 */
public class NacosConfigiServiceUtils {

    @Getter
    private static NacosConfigManager configManager;
    @Getter
    private static ConfigService configService;

    public static void init(NacosConfigManager nacosConfigManager) {
        configManager = nacosConfigManager;
        configService = nacosConfigManager.getConfigService();

    }

}

```

# NacosShardingSphereDriverURLProvider

**文件路径**: `jdbc\nacos\NacosShardingSphereDriverURLProvider.java`

## 代码文档
///

// * @author 张治保
// * @since 2024/5/9
// 
///


## 文件结构
```
项目根目录
└── jdbc\nacos
    └── NacosShardingSphereDriverURLProvider.java
```

## 完整代码
```java
//package com.medusa.gruul.common.sharding.jdbc.nacos;
//
//import com.alibaba.cloud.nacos.NacosConfigProperties;
//import com.alibaba.nacos.shaded.com.google.common.base.Strings;
//import lombok.SneakyThrows;
//import org.apache.shardingsphere.driver.jdbc.core.driver.ShardingSphereDriverURLProvider;
//
//import java.nio.charset.StandardCharsets;
//
///**
// * @author 张治保
// * @since 2024/5/9
// */
//public class NacosShardingSphereDriverURLProvider implements ShardingSphereDriverURLProvider {
//    private static final String CLASSPATH_TYPE = "nacos:";
//
//    private static final String URL_PREFIX = "jdbc:shardingsphere:";
//
//    @Override
//    public boolean accept(final String url) {
//        return !Strings.isNullOrEmpty(url) && url.contains(CLASSPATH_TYPE);
//    }
//
//
//    @Override
//    @SneakyThrows
//    public byte[] getContent(String url) {
//        String configPath = url.substring(URL_PREFIX.length(), url.contains("?") ? url.indexOf('?') : url.length());
//        String dataId = configPath.substring(CLASSPATH_TYPE.length());
//        NacosConfigProperties nacosConfigProperties = NacosConfigiServiceUtils.getConfigManager()
//                .getNacosConfigProperties();
//        String content = NacosConfigiServiceUtils.getConfigService()
//                .getConfig(dataId, nacosConfigProperties.getGroup(), nacosConfigProperties.getTimeout());
//        return content.getBytes(StandardCharsets.UTF_8);
//    }
//}

```

# com.medusa.gruul.common.sharding.jdbc.nacos.NacosUtilsBootstrapConfiguration

**文件路径**: `jdbc\nacos\NacosUtilsBootstrapConfiguration.java`

## 代码文档
///
@author 张治保
@since 2024/5/9
 
///


## 文件结构
```
项目根目录
└── jdbc\nacos
    └── NacosUtilsBootstrapConfiguration.java
```

## 完整代码
```java
package com.medusa.gruul.common.sharding.jdbc.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author 张治保
 * @since 2024/5/9
 */
@ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosConfigBootstrapConfiguration")
@ConditionalOnProperty(name = {"spring.cloud.nacos.config.enabled"}, matchIfMissing = true)
public class NacosUtilsBootstrapConfiguration {

    public NacosUtilsBootstrapConfiguration(NacosConfigManager nacosConfigManager) {

        NacosConfigiServiceUtils.init(nacosConfigManager);

    }

}

```

