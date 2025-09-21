# com.medusa.gruul.common.mp.model.BaseEntity

**文件路径**: `mp\model\BaseEntity.java`

## 代码文档
///
@author 张治保
date 2022/2/17
 
///

///
id
{@link com.medusa.gruul.common.mp.config.MybatisPlusConfig#identifierGenerator()}
     
///

///
乐观锁版本号
     
///

///
逻辑删除标记
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── BaseEntity.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * @author 张治保
 * date 2022/2/17
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Accessors(chain = true)
public class BaseEntity extends com.medusa.gruul.common.mp.model.base.BaseEntity {
    @Serial
    private static final long serialVersionUID = -5441749920695254908L;
    /**
     * id
     * {@link com.medusa.gruul.common.mp.config.MybatisPlusConfig#identifierGenerator()}
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Integer version;
    /**
     * 逻辑删除标记
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Boolean deleted;
}

```

# com.medusa.gruul.common.mp.model.DS

**文件路径**: `mp\model\DS.java`

## 代码文档
///
手动切换数据源

@author 张治保
@since 2024/5/14
 
///

///
shardingSphere数据源名称
     
///

///
动态数据源切换至 sharding jdbc

@param task 切换执行的任务
     
///

///
动态数据源切换至 sharding jdbc

@param task 切换执行的任务
@param <T>  任务的返回值类型
@return 返回结果
     
///

///
动态数据源切换

@param dsName 数据源名称
@param task   切换执行的任务
     
///

///
动态数据源切换

@param dsName 数据源名称
@param task   切换执行的任务
@param <T>    任务的返回值类型
@return 返回结果
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── DS.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

import java.util.function.Supplier;

/**
 * 手动切换数据源
 *
 * @author 张治保
 * @since 2024/5/14
 */
public interface DS {


    /**
     * shardingSphere数据源名称
     */
    String SHARDING_SPHERE_DS = "shardingSphere";

    /**
     * 动态数据源切换至 sharding jdbc
     *
     * @param task 切换执行的任务
     */
    static void sharding(Runnable task) {
        to(SHARDING_SPHERE_DS, task);
    }

    /**
     * 动态数据源切换至 sharding jdbc
     *
     * @param task 切换执行的任务
     * @param <T>  任务的返回值类型
     * @return 返回结果
     */
    static <T> T sharding(Supplier<T> task) {
        return to(SHARDING_SPHERE_DS, task);
    }

    /**
     * 动态数据源切换
     *
     * @param dsName 数据源名称
     * @param task   切换执行的任务
     */
    static void to(String dsName, Runnable task) {
        DynamicDataSourceContextHolder.push(dsName);
        try {
            task.run();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 动态数据源切换
     *
     * @param dsName 数据源名称
     * @param task   切换执行的任务
     * @param <T>    任务的返回值类型
     * @return 返回结果
     */
    static <T> T to(String dsName, Supplier<T> task) {
        DynamicDataSourceContextHolder.push(dsName);
        try {
            return task.get();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

}

```

# com.medusa.gruul.common.mp.model.MpI18NLoader

**文件路径**: `mp\model\MpI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── mp\model
    └── MpI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class MpI18NLoader implements I18NPropertiesLoader {
	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/mp");
	}
}

```

# com.medusa.gruul.common.mp.model.SqlHelper

**文件路径**: `mp\model\SqlHelper.java`

## 代码文档
///
sql 工具了

@author 张治保
date 2022/9/23
 
///

///
格式化 占位符
     
///

///
json set template
     
///

///
json set json template
     
///

///
limit
     
///

///
limit 1
     
///

///
limit n
     
///

///
JSON_SET
     
///

///
IN sql template
     
///

///
limit sql

@param size 条数
@return 格式化后的 sql
     
///

///
limit sql

@param current 偏移量
@param size    条数
@return 格式化后的 sql
     
///

///
生成 json set sql
例子: times=JSON_SET(times，'$.a',aValue,'$.b',bValue,'$.c',cValue)

@param column      列名
@param fieldValues json 字段名与字段值 映射关系
@return 格式化后的 sql
@
     
///

///
生成 in sql

@param column 列名
@param values 值
@param <T>    类型
@return 格式化后的 sql
     
///

///
生成 in sql

@param columns 列名
@param values  值
@param <T>     类型
@return 格式化后的 sql
     
///

///
生成 in sql

@param columns      列名
@param values       值
@param valueMappers 值映射函数
@param <T>          类型
@return 格式化后的 sql
     
///

///
生成 in sql

@param columns     列名
@param values      值
@param valueMapper 值映射函数
@param <T>         类型
@return 格式化后的 sql
     
///

///
拼接所有字段
例子:  '$.a',aValue,'$.b',bValue,'$.c',cValue

@param fieldValues json 字段名与字段值 列表
@return 所有json
     
///

///
单个字段拼接
例子: '$.a',aValue

@param fieldValue json 字段名与字段值 列表
@return 所有json
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── SqlHelper.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


/**
 * sql 工具了
 *
 * @author 张治保
 * date 2022/9/23
 */
public interface SqlHelper {

    /**
     * 格式化 占位符
     */
    String PLACEHOLDER = "{}";

    /**
     * json set template
     */
    String JSON_OPERATION_TEMPLATE = "'$.{}',{}";

    /**
     * json set json template
     */
    String JSON_OPERATION_JSON_TEMPLATE = "'$.{}',JSON_EXTRACT({}, '$')";

    /**
     * limit
     */
    String SQL_LIMIT = "LIMIT {},{}";

    /**
     * limit 1
     */
    String SQL_LIMIT_1 = "LIMIT 1";

    /**
     * limit n
     */
    String SQL_LIMIT_N = "LIMIT {}";

    /**
     * JSON_SET
     */
    String JSON_SET_TEMPLATE = "{}=JSON_SET({},{})";


    /**
     * IN sql template
     */
    String IN_SQL_TEMPLATE = "{} in ({})";

    /**
     * limit sql
     *
     * @param size 条数
     * @return 格式化后的 sql
     */
    static String limit(long size) {
        return StrUtil.format(SQL_LIMIT_N, size);
    }

    /**
     * limit sql
     *
     * @param current 偏移量
     * @param size    条数
     * @return 格式化后的 sql
     */
    static String limit(long current, long size) {
        return StrUtil.format(SQL_LIMIT, (current - 1) * size, size);
    }

    /**
     * 生成 json set sql
     * 例子: times=JSON_SET(times，'$.a',aValue,'$.b',bValue,'$.c',cValue)
     *
     * @param column      列名
     * @param fieldValues json 字段名与字段值 映射关系
     * @return 格式化后的 sql
     * @
     */
    static String renderJsonSetSql(String column, Tuple... fieldValues) {
        return StrUtil.format(JSON_SET_TEMPLATE, column, column, jsonFieldWithValues(fieldValues));
    }


    /**
     * 生成 in sql
     *
     * @param column 列名
     * @param values 值
     * @param <T>    类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(String column, Collection<T> values) {
        return StrUtil.format(IN_SQL_TEMPLATE, column, StrUtil.join(StrPool.COMMA, values));
    }

    /**
     * 生成 in sql
     *
     * @param columns 列名
     * @param values  值
     * @param <T>     类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(List<String> columns, Collection<T> values) {
        return inSql(columns, values, (Function<T, ? extends Serializable>) null);
    }

    /**
     * 生成 in sql
     *
     * @param columns      列名
     * @param values       值
     * @param valueMappers 值映射函数
     * @param <T>          类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(List<String> columns, Collection<T> values, List<Function<T, ? extends Serializable>> valueMappers) {
        if (CollUtil.isEmpty(columns)) {
            return inSql(columns, values, (Function<T, ? extends Serializable>) null);
        }
        if (valueMappers.size() == 1) {
            return inSql(columns, values, valueMappers.get(0));
        }
        return inSql(
                columns,
                values,
                val -> "(" +
                        StrUtil.join(
                                StrPool.COMMA,
                                valueMappers.stream()
                                        .map(valueMapper -> valueMapper.apply(val))
                                        .toList()
                        ) +
                        ")"
        );
    }

    /**
     * 生成 in sql
     *
     * @param columns     列名
     * @param values      值
     * @param valueMapper 值映射函数
     * @param <T>         类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(List<String> columns, Collection<T> values, Function<T, ? extends Serializable> valueMapper) {
        return StrUtil.format(
                IN_SQL_TEMPLATE,
                // 列名
                columns.size() == 1 ? columns.get(0) : "(" + StrUtil.join(StrPool.COMMA, columns) + ")",
                // 值
                StrUtil.join(StrPool.COMMA,
                        valueMapper != null
                                ? values.stream()
                                .map(valueMapper)
                                .toList()
                                : values
                )
        );
    }


    /**
     * 拼接所有字段
     * 例子:  '$.a',aValue,'$.b',bValue,'$.c',cValue
     *
     * @param fieldValues json 字段名与字段值 列表
     * @return 所有json
     */
    static String jsonFieldWithValues(Tuple... fieldValues) {
        int length = fieldValues.length;
        if (length == 1) {
            return jsonFieldWithValue(fieldValues[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < length; index++) {
            if (index != 0) {
                sb.append(StrPool.COMMA);
            }
            sb.append(jsonFieldWithValue(fieldValues[index]));
        }
        return sb.toString();
    }

    /**
     * 单个字段拼接
     * 例子: '$.a',aValue
     *
     * @param fieldValue json 字段名与字段值 列表
     * @return 所有json
     */
    static String jsonFieldWithValue(Tuple fieldValue) {
        String template;
        Tuple2<?, ?> tuple2;
        if (fieldValue instanceof Tuple2<?, ?> tuple2Instance) {
            tuple2 = tuple2Instance;
            template = JSON_OPERATION_TEMPLATE;
        } else {
            Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) fieldValue;
            tuple2 = Tuple.of(tuple3._1(), tuple3._2());
            template = JSON_OPERATION_JSON_TEMPLATE;
        }
        Object value = tuple2._2();
        return StrUtil.format(template, tuple2._1(), value instanceof CharSequence ? StrUtil.format("'{}'", value) : value);
    }
}
```

# com.medusa.gruul.common.mp.model.Tenant

**文件路径**: `mp\model\Tenant.java`

## 代码文档
///
@author 张治保
date 2022/4/16
 
///

///
是否禁用平台多租户
     
///

///
是否禁用商家店铺多租户
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── Tenant.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * date 2022/4/16
 */
@Getter
@Setter
public class Tenant {
    /**
     * 是否禁用平台多租户
     */
    private boolean disableProvider = false;
    /**
     * 是否禁用商家店铺多租户
     */
    private boolean disableShop = false;
}

```

# com.medusa.gruul.common.mp.model.TenantErrorCode

**文件路径**: `mp\model\TenantErrorCode.java`

## 代码文档
///
@author 张治保
date 2022/4/22
 
///

///
店铺id不可用
     
///

///
平台服务商id不存在
     
///

///
店铺id不可用
     
///

///
店铺id不存在
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── TenantErrorCode.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

/**
 * @author 张治保
 * date 2022/4/22
 */
public interface TenantErrorCode {
    /**
     * 店铺id不可用
     */
    int PLATFORM_ID_INVALID = 3001;

    /**
     * 平台服务商id不存在
     */
    int PLATFORM_ID_NOT_EXISTS = 3002;

    /**
     * 店铺id不可用
     */
    int SHOP_ID_INVALID = 3003;

    /**
     * 店铺id不存在
     */
    int SHOP_ID_NOT_EXISTS = 3004;
}

```

# com.medusa.gruul.common.mp.model.TenantShop

**文件路径**: `mp\model\TenantShop.java`

## 代码文档
///
店铺多租户

@author 张治保
date 2022/4/16
 
///

///
禁用店铺id多租户并执行计算

@param runnable 计算逻辑
     
///

///
禁用店铺id多租户 并传参进行计算

@param param    参数
@param consumer 计算逻辑
@param <T>      参数类型
     
///

///
两个参数的
     
///

///
禁用店铺id多租户 并计算产出结果

@param supplier 计算逻辑
@param <R>      结果类型
     
///

///
禁用店铺id多租户 根据传参计算产出结果

@param param    参数
@param function 计算逻辑
@param <T>      参数类型
@param <R>      返回值类型
@return 返回结果
     
///

///
两个参数的
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── TenantShop.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.function.*;

/**
 * 店铺多租户
 *
 * @author 张治保
 * date 2022/4/16
 */
public class TenantShop {

    private static final ThreadLocal<Boolean> DISABLE = new TransmittableThreadLocal<>();

    public static boolean isDisable() {
        Boolean disable = DISABLE.get();
        if (disable == null) {
            return false;
        }
        return disable;
    }


    /**
     * 禁用店铺id多租户并执行计算
     *
     * @param runnable 计算逻辑
     */
    public static void disable(Runnable runnable) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            runnable.run();
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 禁用店铺id多租户 并传参进行计算
     *
     * @param param    参数
     * @param consumer 计算逻辑
     * @param <T>      参数类型
     */
    public static <T> void disable(T param, Consumer<T> consumer) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            consumer.accept(param);
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 两个参数的
     */
    public static <T1, T2> void disable(T1 param1, T2 param2, BiConsumer<T1, T2> consumer) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            consumer.accept(param1, param2);
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 禁用店铺id多租户 并计算产出结果
     *
     * @param supplier 计算逻辑
     * @param <R>      结果类型
     */
    public static <R> R disable(Supplier<R> supplier) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            return supplier.get();
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 禁用店铺id多租户 根据传参计算产出结果
     *
     * @param param    参数
     * @param function 计算逻辑
     * @param <T>      参数类型
     * @param <R>      返回值类型
     * @return 返回结果
     */
    public static <T, R> R disable(T param, Function<T, R> function) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            return function.apply(param);
        } finally {
            DISABLE.set(preDisable);
        }
    }

    /**
     * 两个参数的
     */
    public static <T1, T2, R> R disable(T1 param1, T2 param2, BiFunction<T1, T2, R> function) {
        Boolean preDisable = DISABLE.get();
        DISABLE.set(Boolean.TRUE);
        try {
            return function.apply(param1, param2);
        } finally {
            DISABLE.set(preDisable);
        }
    }

}

```

# com.medusa.gruul.common.mp.model.TenantShopError

**文件路径**: `mp\model\TenantShopError.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///

///
店铺id不可用
     
///

///
店铺id不存在
     
///


## 文件结构
```
项目根目录
└── mp\model
    └── TenantShopError.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/16
 */
@RequiredArgsConstructor
public enum TenantShopError implements Error {

    /**
     * 店铺id不可用
     */
    SHOP_ID_INVALID(TenantErrorCode.SHOP_ID_INVALID, "mp.shop.id.invalid"),

    /**
     * 店铺id不存在
     */
    SHOP_ID_NOT_EXISTS(TenantErrorCode.SHOP_ID_NOT_EXISTS, "mp.shop.id.not.exist");

    private final int code;
    private final String msgCode;


    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return I18N.msg(msgCode);
    }
}

```

# com.medusa.gruul.common.mp.model.base.BaseEntity

**文件路径**: `model\base\BaseEntity.java`

## 代码文档
///
@author 张治保
@since 2023/11/14
 
///

///
创建时间
     
///

///
更新时间
     
///


## 文件结构
```
项目根目录
└── model\base
    └── BaseEntity.java
```

## 完整代码
```java
package com.medusa.gruul.common.mp.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

```

