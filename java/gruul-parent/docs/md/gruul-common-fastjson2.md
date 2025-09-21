# com.medusa.gruul.common.fastjson2.FastJson2

**文件路径**: `common\fastjson2\FastJson2.java`

## 代码文档
///
fastjson2通用配置与门面

@author 张治保
date 2023/4/6
 
///

///
反序列化配置
     
///

///
序列化配置
     
///

///
获取反序列化配置

@return 反序列化配置
     
///

///
获取序列化配置

@return 序列化配置
     
///

///
将对象转换为指定类型

@param value 值
@param type  类型
@param <T>   类型
@return 转换后的对象
     
///

///
将对象转换为指定类型

@param value    值
@param type     类型
@param features 配置
@param <T>      类型
@return 转换后的对象
     
///

///
将对象转换为指定类型

@param value 值
@param type  类型
@param <T>   类型
@return 转换后的对象
     
///

///
将对象转换为指定类型

@param value     值
@param reference 类型
@param <T>       类型
@return 转换后的对象
     
///

///
将对象转换为指定类型

@param value     值
@param reference 类型
@param features  配置
@param <T>       类型
@return 转换后的对象
     
///


## 文件结构
```
项目根目录
└── common\fastjson2
    └── FastJson2.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.util.TypeUtils;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * fastjson2通用配置与门面
 *
 * @author 张治保
 * date 2023/4/6
 */
public class FastJson2 {

    //时间格式化
    public static final String NO_SECONDS_TIME_PATTEN = "HH:mm";
    public static final DateTimeFormatter NO_SECONDS_TIME_FORMATTER = DateTimeFormatter.ofPattern(NO_SECONDS_TIME_PATTEN);
    public static final String TIME_PATTEN = DatePattern.NORM_TIME_PATTERN;
    public static final DateTimeFormatter TIME_FORMATTER = DatePattern.NORM_TIME_FORMATTER;
    //日期格式化
    public static final String DATE_PATTEN = DatePattern.NORM_DATE_PATTERN;
    public static final DateTimeFormatter DATE_FORMATTER = DatePattern.NORM_DATE_FORMATTER;
    //日期时间格式化
    public static final String DATETIME_PATTEN = DatePattern.NORM_DATETIME_PATTERN;
    public static final DateTimeFormatter DATETIME_FORMATTER = DatePattern.NORM_DATETIME_FORMATTER;
    public static final String NO_SECONDS_DATETIME_PATTEN = DatePattern.NORM_DATETIME_MINUTE_PATTERN;
    public static final DateTimeFormatter NO_SECONDS_DATETIME_FORMATTER = DatePattern.NORM_DATETIME_MINUTE_FORMATTER;
    /**
     * 反序列化配置
     */
    private static final JSONReader.Feature[] READ_FEATURE = {
            JSONReader.Feature.IgnoreSetNullValue,
            //JSONReader.Feature.TrimString,
            JSONReader.Feature.ErrorOnEnumNotMatch

    };
    /**
     * 序列化配置
     */
    private static final JSONWriter.Feature[] WRITE_FEATURE = {
            JSONWriter.Feature.WriteEnumsUsingName,
            JSONWriter.Feature.WriteBigDecimalAsPlain,
            //JSONWriter.Feature.WriteLongAsString,
            //JSONWriter.Feature.BrowserSecure

    };

    /**
     * 获取反序列化配置
     *
     * @return 反序列化配置
     */
    public static JSONReader.Feature[] readFeature() {
        return READ_FEATURE;
    }

    /**
     * 获取序列化配置
     *
     * @return 序列化配置
     */
    public static JSONWriter.Feature[] writeFeature() {
        return WRITE_FEATURE;
    }


    /**
     * 将对象转换为指定类型
     *
     * @param value 值
     * @param type  类型
     * @param <T>   类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, Class<T> type) {
        return convert(value, type, FastJson2.readFeature());
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value    值
     * @param type     类型
     * @param features 配置
     * @param <T>      类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, Class<T> type, JSONReader.Feature... features) {
        //判断目标type类型
        if (Map.class.isAssignableFrom(type)) {
            return JSONObject.from(value).to(type, features);
        }
        if (Collection.class.isAssignableFrom(type)) {
            return JSONArray.from(value).to(type);
        }
        return JSON.to(type, value);
    }


    /**
     * 将对象转换为指定类型
     *
     * @param value 值
     * @param type  类型
     * @param <T>   类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, Type type) {
        return TypeUtils.cast(value, type);
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value     值
     * @param reference 类型
     * @param <T>       类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, TypeReference<T> reference) {
        return convert(value, reference, FastJson2.readFeature());
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value     值
     * @param reference 类型
     * @param features  配置
     * @param <T>       类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, TypeReference<T> reference, JSONReader.Feature... features) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map<?, ?> mapValue) {
            return new JSONObject(mapValue).to(reference, FastJson2.readFeature());
        }
        if (value instanceof String strValue) {
            return JSON.parseObject(strValue, reference, FastJson2.readFeature());
        }
        Class<? super T> rawType = reference.getRawType();
        if (Map.class.isAssignableFrom(rawType)) {
            return JSONObject.from(value).to(reference, features);
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            return reference.to(JSONArray.from(value));
        }
        if (rawType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return JSON.parseObject(JSON.toJSONString(value), reference, features);
    }

}

```

# com.medusa.gruul.common.fastjson2.FastJson2Autoconfigure

**文件路径**: `common\fastjson2\FastJson2Autoconfigure.java`

## 代码文档
///
@author 张治保
@since 2023/11/6
 
///


## 文件结构
```
项目根目录
└── common\fastjson2
    └── FastJson2Autoconfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2;

/**
 * @author 张治保
 * @since 2023/11/6
 */
public class FastJson2Autoconfigure {

}

```

# com.medusa.gruul.common.fastjson2.FastJson2SpringListener

**文件路径**: `common\fastjson2\FastJson2SpringListener.java`

## 代码文档
///
@author 张治保
date 2023/7/17
 
///


## 文件结构
```
项目根目录
└── common\fastjson2
    └── FastJson2SpringListener.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.fastjson2.rw.ReaderWriter;
import com.medusa.gruul.common.spring.listener.functions.GruulSpringListener;
import org.springframework.boot.ConfigurableBootstrapContext;

/**
 * @author 张治保
 * date 2023/7/17
 */
public class FastJson2SpringListener extends GruulSpringListener {
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        //全局 date format
        JSON.configReaderDateFormat(FastJson2.DATETIME_PATTEN);
        JSON.configWriterDateFormat(FastJson2.DATETIME_PATTEN);
        //duration format
        for (ReaderWriter value : ReaderWriter.values()) {
            Class<?> type = value.READER_WRITER.codec().encodeClass();
            JSON.register(type, value.READER_WRITER.reader());
            JSON.register(type, value.READER_WRITER.writer());
        }
    }
}

```

# com.medusa.gruul.common.fastjson2.TypeNameHash

**文件路径**: `common\fastjson2\TypeNameHash.java`

## 代码文档
///
type name hash

@author 张治保
date 2023/5/18
 
///


## 文件结构
```
项目根目录
└── common\fastjson2
    └── TypeNameHash.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2;

import cn.hutool.core.map.WeakConcurrentMap;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.util.Fnv;
import com.alibaba.fastjson2.util.TypeUtils;

import java.util.Map;

/**
 * type name hash
 *
 * @author 张治保
 * date 2023/5/18
 */
public record TypeNameHash(long typeNameHash, byte[] typeNameJSONB) {
    public static final Map<Class<?>, TypeNameHash> TYPE_NAME_MAP = new WeakConcurrentMap<>();

    public static TypeNameHash get(Class<?> clazz) {
        return TYPE_NAME_MAP.computeIfAbsent(
                clazz,
                cls -> {
                    String typeName = TypeUtils.getTypeName(cls);
                    return new TypeNameHash(Fnv.hashCode64(typeName), JSONB.toBytes(typeName));
                }
        );
    }

}

```

# com.medusa.gruul.common.fastjson2.annotation.Desensitize

**文件路径**: `fastjson2\annotation\Desensitize.java`

## 代码文档
///
数据脱敏注解 只针对 string类型字段

@author 张治保
date 2023/5/17
 
///

///
脱敏类型

@return DesensitizedUtil.DesensitizedType
     
///

///
脱敏开始位置

@return int
     
///

///
脱敏结束位置

@return int
     
///

///
正则表达式 脱敏 通过正则查找到字符串，template，$1表示分组1的字符串
     
///

///
替代模板
     
///


## 文件结构
```
项目根目录
└── fastjson2\annotation
    └── Desensitize.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.annotation;

import cn.hutool.core.util.DesensitizedUtil;

import java.lang.annotation.*;

/**
 * 数据脱敏注解 只针对 string类型字段
 *
 * @author 张治保
 * date 2023/5/17
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@JSONField(serializeUsing = DesensitizeWriter.class)
@Inherited
public @interface Desensitize {

    /**
     * 脱敏类型
     *
     * @return DesensitizedUtil.DesensitizedType
     */
    DesensitizedUtil.DesensitizedType type() default DesensitizedUtil.DesensitizedType.CHINESE_NAME;

    /**
     * 脱敏开始位置
     *
     * @return int
     */
    int start() default -1;

    /**
     * 脱敏结束位置
     *
     * @return int
     */
    int end() default -1;

    /**
     * 正则表达式 脱敏 通过正则查找到字符串，template，$1表示分组1的字符串
     */
    String regx() default "";

    /**
     * 替代模板
     */
    String template() default "";
}

```

# com.medusa.gruul.common.fastjson2.codec.DefaultCodec

**文件路径**: `fastjson2\codec\DefaultCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
时长
     
///

///
时间
     
///

///
日期
     
///

///
年月
     
///

///
日期时间
     
///


## 文件结构
```
项目根目录
└── fastjson2\codec
    └── DefaultCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public enum DefaultCodec {


    /**
     * 时长
     */
    DURATION(new DurationCodec()),

    /**
     * 时间
     */
    LOCAL_TIME(new LocalTimeCodec()),

    /**
     * 日期
     */
    LOCAL_DATE(new LocalDateCodec()),

    /**
     * 年月
     */
    YEAR_MONTH(new YearMonthCodec()),

    /**
     * 日期时间
     */
    LOCAL_DATE_TIME(new LocalDateTimeCodec()),

    ;

    public static final Map<CodecClass, Codec<Object, Object>> CODEC_MAP = MapUtil.newHashMap();

    static {
        for (DefaultCodec value : DefaultCodec.values()) {
            Codec<Object, Object> curCodec = value.codec();
            Class<?> key = curCodec.encodeClass();
            CODEC_MAP.put(new CodecClass(key, curCodec.decodeClass()), curCodec);
        }
    }

    private final Codec<Object, Object> codec;

    @SuppressWarnings("unchecked")
    DefaultCodec(Codec<?, ?> codec) {
        this.codec = (Codec<Object, Object>) codec;
    }

    public static Codec<Object, Object> getCodec(Class<?> encode, Class<?> decode) {
        return CODEC_MAP.get(new CodecClass(encode, decode));
    }

    @SuppressWarnings("unchecked")
    public <T, R> Codec<T, R> codec() {
        return (Codec<T, R>) codec;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class CodecClass {

        private final Class<?> encode;
        private final Class<?> decode;

    }
}

```

# com.medusa.gruul.common.fastjson2.codec.DurationCodec

**文件路径**: `fastjson2\codec\DurationCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///


## 文件结构
```
项目根目录
└── fastjson2\codec
    └── DurationCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.Duration;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class DurationCodec implements ObjectStringCodec<Duration> {


    @Override
    public String encodeToStr(Duration data) {
        return data.toString();
    }

    @Override
    public Duration decodeStr(String dataStr) {
        return Duration.parse(dataStr);
    }

}

```

# com.medusa.gruul.common.fastjson2.codec.LocalDateCodec

**文件路径**: `fastjson2\codec\LocalDateCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///


## 文件结构
```
项目根目录
└── fastjson2\codec
    └── LocalDateCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class LocalDateCodec implements ObjectStringCodec<LocalDate> {


    @Override
    public String encodeToStr(LocalDate data) {
        return data.format(FastJson2.DATE_FORMATTER);
    }

    @Override
    public LocalDate decodeStr(String dataStr) {
        return LocalDate.parse(dataStr, FastJson2.DATE_FORMATTER);
    }

}

```

# com.medusa.gruul.common.fastjson2.codec.LocalDateTimeCodec

**文件路径**: `fastjson2\codec\LocalDateTimeCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///


## 文件结构
```
项目根目录
└── fastjson2\codec
    └── LocalDateTimeCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class LocalDateTimeCodec implements ObjectStringCodec<LocalDateTime> {

    private static final int DATE_LENGTH = FastJson2.DATE_PATTEN.length();
    private static final int NO_SECONDS_DATE_TIME_LENGTH = FastJson2.NO_SECONDS_DATETIME_PATTEN.length();


    @Override
    public String encodeToStr(LocalDateTime data) {
        return data.format(FastJson2.DATETIME_FORMATTER);
    }

    @Override
    public LocalDateTime decodeStr(String dataStr) {
        if (dataStr.length() == DATE_LENGTH) {
            LocalDate localDate = LocalDate.parse(dataStr, FastJson2.DATE_FORMATTER);
            return localDate.atStartOfDay();
        }
        if (dataStr.length() == NO_SECONDS_DATE_TIME_LENGTH) {
            return LocalDateTime.parse(dataStr, FastJson2.NO_SECONDS_DATETIME_FORMATTER);
        }
        return LocalDateTime.parse(dataStr, FastJson2.DATETIME_FORMATTER);
    }
}

```

# com.medusa.gruul.common.fastjson2.codec.LocalTimeCodec

**文件路径**: `fastjson2\codec\LocalTimeCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///


## 文件结构
```
项目根目录
└── fastjson2\codec
    └── LocalTimeCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.LocalTime;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class LocalTimeCodec implements ObjectStringCodec<LocalTime> {

    @Override
    public LocalTime decodeStr(String dataStr) {
        return dataStr.length() == FastJson2.NO_SECONDS_TIME_PATTEN.length() ?
                LocalTime.parse(dataStr, FastJson2.NO_SECONDS_TIME_FORMATTER) :
                LocalTime.parse(dataStr, FastJson2.TIME_FORMATTER)
                ;
    }

    @Override
    public String encodeToStr(LocalTime data) {
        return data.format(
//                data.getSecond() == 0 ?
//                        FastJson2.NO_SECONDS_TIME_FORMATTER :
                FastJson2.TIME_FORMATTER
        );
    }
}

```

# com.medusa.gruul.common.fastjson2.codec.YearMonthCodec

**文件路径**: `fastjson2\codec\YearMonthCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///


## 文件结构
```
项目根目录
└── fastjson2\codec
    └── YearMonthCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec;

import cn.hutool.core.date.DatePattern;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.YearMonth;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class YearMonthCodec implements ObjectStringCodec<YearMonth> {


    @Override
    public String encodeToStr(YearMonth data) {
        return data.format(DatePattern.NORM_MONTH_FORMATTER);
    }

    @Override
    public YearMonth decodeStr(String dataStr) {
        return YearMonth.parse(dataStr, DatePattern.NORM_MONTH_FORMATTER);
    }
}

```

# com.medusa.gruul.common.fastjson2.filter.AutoTypeSupportFilter

**文件路径**: `fastjson2\filter\AutoTypeSupportFilter.java`

## 代码文档
///
auto type support filter
自动类型推断 支持器

@author 张治保
@since 2023/11/8
 
///

///
默认实例
     
///

///
class cache for name
     
///


## 文件结构
```
项目根目录
└── fastjson2\filter
    └── AutoTypeSupportFilter.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.filter;

import cn.hutool.core.map.WeakConcurrentMap;
import com.alibaba.fastjson2.filter.ContextAutoTypeBeforeHandler;
import com.alibaba.fastjson2.util.TypeUtils;

import java.util.Map;

/**
 * auto type support filter
 * 自动类型推断 支持器
 *
 * @author 张治保
 * @since 2023/11/8
 */
public class AutoTypeSupportFilter extends ContextAutoTypeBeforeHandler {

    /**
     * 默认实例
     */
    public static final AutoTypeSupportFilter DEFAULT_INSTANCE = new AutoTypeSupportFilter();

    /**
     * class cache for name
     */
    final Map<String, Class<?>> classCache = new WeakConcurrentMap<>();


    public AutoTypeSupportFilter() {
        super(true);
    }


    public AutoTypeSupportFilter(String... acceptNames) {
        super(true, acceptNames);
    }

    public AutoTypeSupportFilter(Class<?>... classes) {
        super(true, classes);
    }


    @Override
    public Class<?> apply(String typeName, Class<?> expectClass, long features) {
        Class<?> tryLoad = super.apply(typeName, expectClass, features);
        if (tryLoad != null) {
            return tryLoad;
        }
        return loadClassDirectly(typeName);
    }


    public Class<?> loadClassDirectly(String typeName) {
        Class<?> clazz = classCache.get(typeName);
        if (clazz == null) {
            clazz = TypeUtils.getMapping(typeName);
        }
        if (clazz == null) {
            clazz = TypeUtils.loadClass(typeName);
        }
        if (clazz != null) {
            Class<?> origin = classCache.putIfAbsent(typeName, clazz);
            if (origin != null) {
                clazz = origin;
            }
        }
        return clazz;
    }

}
```

# com.medusa.gruul.common.fastjson2.filter.DesensitizeValueFilter

**文件路径**: `fastjson2\filter\DesensitizeValueFilter.java`

## 代码文档
///
数据脱敏拦截器

@author 张治保`
@since 2023/11/27
 
///

///
脱敏

@param annotation 脱敏注解
@param value      原始值
@return 脱敏后的值
     
///


## 文件结构
```
项目根目录
└── fastjson2\filter
    └── DesensitizeValueFilter.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.filter;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.filter.BeanContext;
import com.alibaba.fastjson2.filter.ContextValueFilter;
import com.alibaba.fastjson2.filter.Filter;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;

/**
 * 数据脱敏拦截器
 *
 * @author 张治保`
 * @since 2023/11/27
 */
public class DesensitizeValueFilter implements ContextValueFilter {


    public static final Filter INSTANCE = new DesensitizeValueFilter();

    /**
     * 脱敏
     *
     * @param annotation 脱敏注解
     * @param value      原始值
     * @return 脱敏后的值
     */
    public static String hide(Desensitize annotation, String value) {
        int length = value.length();
        //如果设置了开始和结束位置
        int start, end;
        if ((start = annotation.start()) != -1 && (end = annotation.end()) != -1) {
            return StrUtil.hide(value, start, length - end);
        }
        //如果设置了正则表达式和模板
        String regx, template;
        if (StrUtil.isNotEmpty(regx = annotation.regx()) && StrUtil.isNotEmpty(template = annotation.template())) {
            return ReUtil.replaceAll(value, regx, template);
        }
        //否则使用 脱敏工具脱敏
        return DesensitizedUtil.desensitized(value, annotation.type());
    }

    @Override
    public Object process(BeanContext context, Object object, String name, Object value) {
        if (!(value instanceof String)) {
            return value;
        }
        Desensitize annotation = context.getAnnotation(Desensitize.class);
        if (annotation == null) {
            return value;
        }
        return DesensitizeValueFilter.hide(annotation, value.toString());
    }
}

```

# com.medusa.gruul.common.fastjson2.rw.ReaderWriter

**文件路径**: `fastjson2\rw\ReaderWriter.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
时长
     
///

///
时间
     
///

///
日期
     
///

///
年月
     
///

///
日期时间
     
///


## 文件结构
```
项目根目录
└── fastjson2\rw
    └── ReaderWriter.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.rw;

import com.medusa.gruul.common.fastjson2.codec.DefaultCodec;
import com.medusa.gruul.common.fastjson2.rw.def.StringReaderWriter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/12/19
 */
@RequiredArgsConstructor
public enum ReaderWriter {

    /**
     * 时长
     */
    DURATION(new StringReaderWriter<>(DefaultCodec.DURATION.codec())),

    /**
     * 时间
     */
    LOCAL_TIME(new StringReaderWriter<>(DefaultCodec.LOCAL_TIME.codec())),

    /**
     * 日期
     */
    LOCAL_DATE(new StringReaderWriter<>(DefaultCodec.LOCAL_DATE.codec())),

    /**
     * 年月
     */
    YEAR_MONTH(new StringReaderWriter<>(DefaultCodec.YEAR_MONTH.codec())),

    /**
     * 日期时间
     */
    LOCAL_DATE_TIME(new StringReaderWriter<>(DefaultCodec.LOCAL_DATE_TIME.codec())),

    ;
    public final com.medusa.gruul.common.fastjson2.rw.def.ReaderWriter<?> READER_WRITER;


}

```

# com.medusa.gruul.common.fastjson2.rw.def.Reader

**文件路径**: `rw\def\Reader.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
反序列化数据

@param jsonReader jsonReader
@param fieldType  字段类型
@param fieldName  字段名称
@param features   特性
@return 反序列化后的数据
     
///

///
反序列化数据

@param jsonReader jsonReader
@return 反序列化后的数据
     
///


## 文件结构
```
项目根目录
└── rw\def
    └── Reader.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;

import java.lang.reflect.Type;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Reader<T> extends ObjectReader<T> {

    /**
     * 反序列化数据
     *
     * @param jsonReader jsonReader
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性
     * @return 反序列化后的数据
     */
    @Override
    default T readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        if (jsonReader.isNull()) {
            return null;
        }
        return read(jsonReader);
    }

    /**
     * 反序列化数据
     *
     * @param jsonReader jsonReader
     * @return 反序列化后的数据
     */
    T read(JSONReader jsonReader);
}

```

# com.medusa.gruul.common.fastjson2.rw.def.ReaderWriter

**文件路径**: `rw\def\ReaderWriter.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
获取 codec 编码解码器

@return codec 编码解码器
     
///

///
获取 reader 反序列化器

@return reader 反序列化器
     
///

///
获取 writer 序列化器

@return writer 序列化器
     
///


## 文件结构
```
项目根目录
└── rw\def
    └── ReaderWriter.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface ReaderWriter<T> {


    /**
     * 获取 codec 编码解码器
     *
     * @return codec 编码解码器
     */
    Codec<T, ?> codec();

    /**
     * 获取 reader 反序列化器
     *
     * @return reader 反序列化器
     */
    ObjectReader<T> reader();

    /**
     * 获取 writer 序列化器
     *
     * @return writer 序列化器
     */
    ObjectWriter<T> writer();
}

```

# com.medusa.gruul.common.fastjson2.rw.def.StringReaderWriter

**文件路径**: `rw\def\StringReaderWriter.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///


## 文件结构
```
项目根目录
└── rw\def
    └── StringReaderWriter.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public class StringReaderWriter<T> implements ReaderWriter<T> {

    private final Codec<T, String> codec;
    private final Reader<T> reader;
    private final Writer<T> writer;


    public StringReaderWriter(Codec<T, String> codec) {
        this.codec = codec;
        this.reader = jsonReader -> codec.decode(jsonReader.readString());
        this.writer = (jsonWriter, value) -> jsonWriter.writeString(codec.encode(value));
    }


    @Override
    public Codec<T, ?> codec() {
        return codec;
    }

    @Override
    public ObjectReader<T> reader() {
        return reader;
    }

    @Override
    public ObjectWriter<T> writer() {
        return writer;
    }

}

```

# com.medusa.gruul.common.fastjson2.rw.def.Writer

**文件路径**: `rw\def\Writer.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
write

@param jsonWriter jsonWriter
@param object     原始数据
@param fieldName  字段名称
@param fieldType  字段类型
@param features   特性
     
///

///
write

@param jsonWriter jsonWriter
@param data       原始数据
     
///


## 文件结构
```
项目根目录
└── rw\def
    └── Writer.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Writer<T> extends ObjectWriter<T> {

    /**
     * write
     *
     * @param jsonWriter jsonWriter
     * @param object     原始数据
     * @param fieldName  字段名称
     * @param fieldType  字段类型
     * @param features   特性
     */
    @Override
    @SuppressWarnings("unchecked")
    default void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }
        doWrite(jsonWriter, (T) object);
    }

    /**
     * write
     *
     * @param jsonWriter jsonWriter
     * @param data       原始数据
     */

    void doWrite(JSONWriter jsonWriter, T data);
}

```

# com.medusa.gruul.common.fastjson2.codec.def.Codec

**文件路径**: `codec\def\Codec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
获取被转码的类 也就是序列化前的类

@return 目标类
     
///

///
获取被解码的类 也就是序列化后的类

@return 返回类
     
///


## 文件结构
```
项目根目录
└── codec\def
    └── Codec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec.def;

import cn.hutool.core.util.TypeUtil;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Codec<T, R> extends Encode<T, R>, Decode<T, R> {


    /**
     * 获取被转码的类 也就是序列化前的类
     *
     * @return 目标类
     */
    @SuppressWarnings("unchecked")
    default Class<T> encodeClass() {
        return (Class<T>) TypeUtil.getTypeArgument(this.getClass(), 0);
    }

    /**
     * 获取被解码的类 也就是序列化后的类
     *
     * @return 返回类
     */
    @SuppressWarnings("unchecked")
    default Class<R> decodeClass() {
        return (Class<R>) TypeUtil.getTypeArgument(this.getClass(), 1);
    }


}

```

# com.medusa.gruul.common.fastjson2.codec.def.Decode

**文件路径**: `codec\def\Decode.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
反序列化

@param data 序列化后的数据
@return 反序列化后的数据
     
///


## 文件结构
```
项目根目录
└── codec\def
    └── Decode.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec.def;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Decode<T, R> {


    /**
     * 反序列化
     *
     * @param data 序列化后的数据
     * @return 反序列化后的数据
     */
    T decode(R data);
}

```

# com.medusa.gruul.common.fastjson2.codec.def.Encode

**文件路径**: `codec\def\Encode.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
序列化

@param data 序列化前数据
@return 序列化后的数据
     
///


## 文件结构
```
项目根目录
└── codec\def
    └── Encode.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec.def;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Encode<T, R> {
    /**
     * 序列化
     *
     * @param data 序列化前数据
     * @return 序列化后的数据
     */
    R encode(T data);
}

```

# com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec

**文件路径**: `codec\def\ObjectStringCodec.java`

## 代码文档
///
@author 张治保
@since 2023/12/19
 
///

///
序列化

@param data 序列化前数据
@return 序列化后的数据
     
///

///
反序列化

@param dataStr 序列化后的数据
@return 反序列化后的数据
     
///

///
序列化成字符串

@param data 原始数据
@return 序列化后的字符串
     
///

///
反序列化字符串

@param dataStr 序列化后的字符串
@return 原始数据
     
///

///
获取被解码的类 也就是序列化后的类

@return 返回类
     
///


## 文件结构
```
项目根目录
└── codec\def
    └── ObjectStringCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.fastjson2.codec.def;

import cn.hutool.core.util.StrUtil;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface ObjectStringCodec<T> extends Codec<T, String> {

    /**
     * 序列化
     *
     * @param data 序列化前数据
     * @return 序列化后的数据
     */
    @Override
    default String encode(T data) {
        return data == null ? null : encodeToStr(data);
    }


    /**
     * 反序列化
     *
     * @param dataStr 序列化后的数据
     * @return 反序列化后的数据
     */
    @Override
    default T decode(String dataStr) {
        return StrUtil.isBlank(dataStr) ? null : decodeStr(dataStr);
    }

    /**
     * 序列化成字符串
     *
     * @param data 原始数据
     * @return 序列化后的字符串
     */
    String encodeToStr(T data);

    /**
     * 反序列化字符串
     *
     * @param dataStr 序列化后的字符串
     * @return 原始数据
     */
    T decodeStr(String dataStr);


    /**
     * 获取被解码的类 也就是序列化后的类
     *
     * @return 返回类
     */
    @Override
    default Class<String> decodeClass() {
        return String.class;
    }
}

```

