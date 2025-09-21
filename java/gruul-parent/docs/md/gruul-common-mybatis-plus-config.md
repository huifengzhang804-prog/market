# com.medusa.gruul.common.mp.IManualTransaction

**文件路径**: `common\mp\IManualTransaction.java`

## 代码文档
///
手动事务操作

@author 张治保
date 2022/8/8
 
///

///
设置事务管理器

@param platformTransactionManager 事务管理器
@param transactionDefinition      事务定义信息
     
///

///
当前事务提交之后 会执行的任务
     
///

///
手动开启一个事务

@param task 事务操作
     
///


## 文件结构
```
项目根目录
└── common\mp
    └── IManualTransaction.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 手动事务操作
 *
 * @author 张治保
 * date 2022/8/8
 */
public class IManualTransaction {
    private static PlatformTransactionManager platformTransactionManager;
    private static TransactionDefinition transactionDefinition;

    /**
     * 设置事务管理器
     *
     * @param platformTransactionManager 事务管理器
     * @param transactionDefinition      事务定义信息
     */
    public static void setManage(PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition) {
        IManualTransaction.platformTransactionManager = platformTransactionManager;
        IManualTransaction.transactionDefinition = transactionDefinition;

    }

    /**
     * 当前事务提交之后 会执行的任务
     */
    public static void afterCommit(Runnable... tasks) {
        //如果没有活动事务则直接执行
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            for (Runnable runnable : tasks) {
                runnable.run();
            }
            return;
        }
        //否则事务提交之后执行
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (Runnable runnable : tasks) {
                            runnable.run();
                        }
                    }
                }
        );
    }


    /**
     * 手动开启一个事务
     *
     * @param task 事务操作
     */
    public static void todo(Runnable task) {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        boolean hashError = false;
        try {
            task.run();
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception exception) {
            hashError = true;
            throw exception;
        } finally {
            if (hashError && !transactionStatus.isCompleted()) {
                platformTransactionManager.rollback(transactionStatus);
            }
        }
    }

}

```

# com.medusa.gruul.common.mp.MybatisPlusAutoconfigure

**文件路径**: `common\mp\MybatisPlusAutoconfigure.java`

## 代码文档
///
MybatisPlus 自动装配

@author 张治保
date 2022/2/10
 
///


## 文件结构
```
项目根目录
└── common\mp
    └── MybatisPlusAutoconfigure.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.mp.config.MybatisPlusConfig

**文件路径**: `mp\config\MybatisPlusConfig.java`

## 代码文档
///
description: MybatisPlusConfig.java

@author alan
date: 2019/7/13 19:21
 
///

///
分页插件

@return PaginationInterceptor
     
///

///
自定义id生成器
     
///


## 文件结构
```
项目根目录
└── mp\config
    └── MybatisPlusConfig.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.mp.config.TenantHandlerConfig

**文件路径**: `mp\config\TenantHandlerConfig.java`

## 代码文档
///
多租户配置
@author 张治保
date 2022/4/22
 
///


## 文件结构
```
项目根目录
└── mp\config
    └── TenantHandlerConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.config;

import com.medusa.gruul.common.mp.handler.ProviderTenantHandler;
import com.medusa.gruul.common.mp.handler.ShopTenantHandler;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 多租户配置
 * @author 张治保
 * date 2022/4/22
 */
@RequiredArgsConstructor
public class TenantHandlerConfig {


    @Bean
    @ConditionalOnProperty(prefix = "gruul.tenant",name = "enable-multi-provider")
    public ProviderTenantHandler providerTenantHandler(TenantConfigProperties properties){
        return new ProviderTenantHandler(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "gruul.tenant",name = "enable-multi-shop")
    public ShopTenantHandler shopTenantHandler(TenantConfigProperties properties){
        return new ShopTenantHandler(properties);
    }
}

```

# com.medusa.gruul.common.mp.config.TransactionalConfig

**文件路径**: `mp\config\TransactionalConfig.java`

## 代码文档
///
@author 张治保
date 2022/8/8
 
///


## 文件结构
```
项目根目录
└── mp\config
    └── TransactionalConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.config;

import com.medusa.gruul.common.mp.IManualTransaction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * @author 张治保
 * date 2022/8/8
 */
@Configuration
public class TransactionalConfig {

    @Bean
    public CommandLineRunner manualTransactionLineRunner(PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition) {
        return args -> IManualTransaction.setManage(platformTransactionManager, transactionDefinition);
    }
}

```

# com.medusa.gruul.common.mp.exception.TenantException

**文件路径**: `mp\exception\TenantException.java`

## 代码文档
///
@author 张治保
date 2022/3/25
 
///


## 文件结构
```
项目根目录
└── mp\exception
    └── TenantException.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.exception;

import com.medusa.gruul.global.model.exception.GlobalException;

/**
 * @author 张治保
 * date 2022/3/25
 */
public class TenantException extends GlobalException {

    public TenantException(int code, String message) {
        super(code, message);
    }

    public TenantException(String message) {
        super(message);
    }

    public TenantException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public TenantException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

# com.medusa.gruul.common.mp.handler.IMetaObjectHandler

**文件路径**: `mp\handler\IMetaObjectHandler.java`

## 代码文档
///
description: 填充器

@author alan
date: 2019/8/31 9:37
 
///


## 文件结构
```
项目根目录
└── mp\handler
    └── IMetaObjectHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * description: 填充器
 *
 * @author alan
 * date: 2019/8/31 9:37
 */
@Slf4j
public class IMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "version", Integer.class, 0);
        this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

}

```

# com.medusa.gruul.common.mp.handler.ProviderTenantHandler

**文件路径**: `mp\handler\ProviderTenantHandler.java`

## 代码文档
///
description: 租户维护处理器

@author alan
date: 2019/7/13 19:32
 
///

///
获取租户值

TODO 校验租户状态

@return 租户值
     
///

///
获取租户字段名

@return 租户字段名
     
///

///
根据表名判断是否进行过滤

@param tableName 表名
@return 是否进行过滤
     
///


## 文件结构
```
项目根目录
└── mp\handler
    └── ProviderTenantHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * description: 租户维护处理器
 *
 * @author alan
 * date: 2019/7/13 19:32
 */
@Slf4j
@RequiredArgsConstructor
public class ProviderTenantHandler implements TenantLineHandler {

    private final TenantConfigProperties properties;

    /**
     * 获取租户值
     * 
     * TODO 校验租户状态
     *
     * @return 租户值
     */
    @Override
    public Expression getTenantId() {
        return new LongValue(-1);
    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getPlatformId();
    }

    /**
     * 根据表名判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤
     */
    @Override
    public boolean ignoreTable(String tableName) {
        return true;
    }
}

```

# com.medusa.gruul.common.mp.handler.ShopTenantHandler

**文件路径**: `mp\handler\ShopTenantHandler.java`

## 代码文档
///
description: 租户维护处理器

@author alan
Date: 2019/7/13 19:32
 
///

///
获取租户值

@return 租户值
     
///

///
获取租户字段名

@return 租户字段名
     
///

///
根据表名判断是否进行过滤

@param tableName 表名
@return 是否进行过滤
     
///

///
获取 ``字符修饰的表名 `t_menu`
     
///


## 文件结构
```
项目根目录
└── mp\handler
    └── ShopTenantHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.handler;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.medusa.gruul.common.mp.exception.TenantException;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.mp.model.TenantShopError;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.i18n.I18N;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.HashSet;
import java.util.Set;

/**
 * description: 租户维护处理器
 *
 * @author alan
 * Date: 2019/7/13 19:32
 */
@Slf4j
@RequiredArgsConstructor
public class ShopTenantHandler implements TenantLineHandler {

    private static final TenantException EXCEPTION = new TenantException(TenantShopError.SHOP_ID_INVALID.code(), TenantShopError.SHOP_ID_INVALID.msg());
    private final TenantConfigProperties properties;

    /**
     * 获取租户值
     *
     * @return 租户值
     */
    @Override
    public Expression getTenantId() {
        Long shopId = ISystem.shopIdMust();
        if (log.isDebugEnabled()) {
            log.debug(I18N.msg("mp.shop.id.debug", shopId));
        }
        return new LongValue(shopId);

    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getShopId();
    }

    /**
     * 根据表名判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤
     */
    @Override
    public boolean ignoreTable(String tableName) {

        if (TenantShop.isDisable()) {
            return true;
        }
        Set<String> ignoreAllTables = properties.getIgnoreAllTables();
        Set<String> ignoreShopIdTables = properties.getIgnoreShopIdTables();

        Set<String> ignoreTables = new HashSet<>(ignoreAllTables);
        ignoreTables.addAll(ignoreShopIdTables);
        /*
         * 避免保留字的表名
         */
        return ignoreTables.stream().anyMatch(
                name -> StrUtil.equals(tableName, name) || StrUtil.equals(tableName, avoidReservedWordTableName(name))
        );
    }

    /**
     * 获取 ``字符修饰的表名 `t_menu`
     */
    private String avoidReservedWordTableName(String tableName) {
        return "`" + tableName + "`";
    }
}

```

# com.medusa.gruul.common.mp.page.PageBean

**文件路径**: `mp\page\PageBean.java`

## 代码文档
///
封装Page分页对象

@author WuDi
date: 2022/11/7
@deprecated 禁用 未来将删除  多余配置
 
///


## 文件结构
```
项目根目录
└── mp\page
    └── PageBean.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 封装Page分页对象
 *
 * @author WuDi
 * date: 2022/11/7
 * @deprecated 禁用 未来将删除  多余配置
 */
@Deprecated
public class PageBean {
	public static <T, E> Page<T> getPage(Page<E> queryPage, List<T> recordList, long total, long pages) {
		Page<T> page = new Page<>();
		page.setTotal(total)
				.setPages(pages)
				.setSize(queryPage.getSize())
				.setCurrent(queryPage.getCurrent())
				.setRecords(recordList);
		return page;
	}
}

```

# com.medusa.gruul.common.mp.properties.TenantConfigProperties

**文件路径**: `mp\properties\TenantConfigProperties.java`

## 代码文档
///
@author sombody
 
///

///
是否启用多服务商模式
     
///

///
是否使用多店铺模式
     
///

///
维护服务商列名称
     
///

///
维护商铺列名称
     
///

///
忽略所有租户注入的表
     
///

///
忽略tenantId注入的表
     
///

///
忽略shopId注入的表
     
///


## 文件结构
```
项目根目录
└── mp\properties
    └── TenantConfigProperties.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sombody
 */
@Data
@ConfigurationProperties(prefix = "gruul.tenant")
public class TenantConfigProperties {

    /**
     * 是否启用多服务商模式
     */
    private Boolean enableMultiProvider = Boolean.FALSE;
    /**
     * 是否使用多店铺模式
     */
    private Boolean enableMultiShop = Boolean.FALSE;
    /**
     * 维护服务商列名称
     */
    private String platformId = "platform_id";
    /**
     * 维护商铺列名称
     */
    private String shopId = "shop_id";
    /**
     * 忽略所有租户注入的表
     */
    private Set<String> ignoreAllTables = new HashSet<>();
    /**
     * 忽略tenantId注入的表
     */
    private Set<String> ignorePlatformIdTables = new HashSet<>();
    /**
     * 忽略shopId注入的表
     */
    private Set<String> ignoreShopIdTables = new HashSet<>();
}

```

# com.medusa.gruul.common.mp.handler.type.DurationTypeHandler

**文件路径**: `handler\type\DurationTypeHandler.java`

## 代码文档
///
精确到毫秒的Duration类型处理器

@author 张治保
date 2023/4/10
 
///


## 文件结构
```
项目根目录
└── handler\type
    └── DurationTypeHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

/**
 * 精确到毫秒的Duration类型处理器
 *
 * @author 张治保
 * date 2023/4/10
 */
@MappedTypes(Duration.class)
@MappedJdbcTypes(JdbcType.BIGINT)
public class DurationTypeHandler extends BaseTypeHandler<Duration> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Duration parameter, JdbcType jdbcType) throws SQLException {
		ps.setLong(i, parameter.toMillis());
	}

	@Override
	public Duration getNullableResult(ResultSet rs, String columnName) throws SQLException {
		long value = rs.getLong(columnName);
		return value == 0 ? null : Duration.ofMillis(value);
	}

	@Override
	public Duration getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		long value = rs.getLong(columnIndex);
		return value == 0 ? null : Duration.ofMillis(value);
	}

	@Override
	public Duration getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		long value = cs.getLong(columnIndex);
		return value == 0 ? null : Duration.ofMillis(value);
	}
}

```

# com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler

**文件路径**: `handler\type\MapToKeyValuesTypeHandler.java`

## 代码文档
///
处理复杂类型的 KEY 的 map 序列化 反序列化 处理器

@author 张治保
date 2023/7/28
<K> key 类型
<V> value 类型
 
///

///
交易流水号
         
///

///
是否可分账
         
///


## 文件结构
```
项目根目录
└── handler\type
    └── MapToKeyValuesTypeHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.handler.type;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.util.ParameterizedTypeImpl;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.global.model.o.KeyValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 处理复杂类型的 KEY 的 map 序列化 反序列化 处理器
 *
 * @author 张治保
 * date 2023/7/28
 * <K> key 类型
 * <V> value 类型
 */
@MappedTypes({Map.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class MapToKeyValuesTypeHandler extends Fastjson2TypeHandler {


    public MapToKeyValuesTypeHandler(Class<?> type) {
        super(type);
    }

    public MapToKeyValuesTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }


    @Override
    @SuppressWarnings("rawtypes")
    public Map<Object, Object> parse(String json) {
        if (StrUtil.isEmpty(json)) {
            return new HashMap<>(0);
        }
        Type fieldType = getFieldType();
        if (fieldType instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length != 2) {
                throw new RuntimeException("MapTypeHandler only support Map<K, V>");
            }
            fieldType = new ParameterizedTypeImpl(actualTypeArguments, null, KeyValue.class);
        }
        List<KeyValue> keyValues = JSON.parseArray(json, fieldType);
        Map<Object, Object> result = new HashMap<>(keyValues.size());
        for (KeyValue keyValue : keyValues) {
            result.put(keyValue.getKey(), keyValue.getValue());
        }
        return result;
    }

    @Override
    public String toJson(Object obj) {
        if (!(obj instanceof Map<?, ?> keyValueMap)) {
            throw new RuntimeException("MapTypeHandler only support Map");
        }
        Set<KeyValue<?, ?>> keyValues = new HashSet<>(keyValueMap.size());
        for (Map.Entry<?, ?> entry : keyValueMap.entrySet()) {
            keyValues.add(new KeyValue<>().setKey(entry.getKey()).setValue(entry.getValue()));
        }
        return JSON.toJSONString(keyValues, JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty);
    }

    @Getter
    @Setter
    @ToString
    public static class Bean {

        private Map<Long, Transaction> map;

    }

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Transaction implements Serializable {
        /**
         * 交易流水号
         */
        private String transactionId;

        /**
         * 是否可分账
         */
        private Boolean profitSharing;
    }
}

```

