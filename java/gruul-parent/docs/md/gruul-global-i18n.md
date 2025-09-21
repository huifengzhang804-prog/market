# com.medusa.gruul.global.i18n.CustomMessageSourceAutoConfiguration

**文件路径**: `global\i18n\CustomMessageSourceAutoConfiguration.java`

## 代码文档
///
根据 {@link MessageSourceAutoConfiguration} 修改 功能拓展

@author 张治保
@see MessageSourceAutoConfiguration
 
///


## 文件结构
```
项目根目录
└── global\i18n
    └── CustomMessageSourceAutoConfiguration.java
```

## 完整代码
```java
package com.medusa.gruul.global.i18n;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Set;

/**
 * 根据 {@link MessageSourceAutoConfiguration} 修改 功能拓展
 *
 * @author 张治保
 * @see MessageSourceAutoConfiguration
 */
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Conditional(ResourceBundleCondition.class)
@EnableConfigurationProperties
public class CustomMessageSourceAutoConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        Set<String> paths = I18N.paths();
        if (StringUtils.hasText(properties.getBasename())) {
            paths.addAll(Set.of(
                    StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename()))
            ));
        }
        messageSource.setBasenames(paths.toArray(new String[0]));
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        I18N.setMessageSource(messageSource);
        return messageSource;
    }

}

```

# com.medusa.gruul.global.i18n.DubboI18NSpreadFilter

**文件路径**: `global\i18n\DubboI18NSpreadFilter.java`

## 代码文档
///
dubbo 传递国际化信息

@author 张治保
date 2023/6/26
 
///


## 文件结构
```
项目根目录
└── global\i18n
    └── DubboI18NSpreadFilter.java
```

## 完整代码
```java
package com.medusa.gruul.global.i18n;

import cn.hutool.core.util.StrUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * dubbo 传递国际化信息
 *
 * @author 张治保
 * date 2023/6/26
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class DubboI18NSpreadFilter implements Filter {

    private static final String LANGUAGE_KEY = "lang";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //如果是消费者端 则传递 语言信息
        if (RpcContext.getServerContext().isConsumerSide()) {
            //消费者
            RpcContext.getClientAttachment().setAttachment(LANGUAGE_KEY, I18N.getLocale().toLanguageTag());
            return invoker.invoke(invocation);
        }
        //否则是服务端 则解析语言信息
        String language = RpcContext.getServerAttachment().getAttachment(LANGUAGE_KEY);
        if (StrUtil.isEmpty(language)) {
            return invoker.invoke(invocation);
        }
        LocaleContextHolder.setLocale(Locale.forLanguageTag(language), true);
        try {
            return invoker.invoke(invocation);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}

```

# com.medusa.gruul.global.i18n.I18N

**文件路径**: `global\i18n\I18N.java`

## 代码文档
///
国际化 工具类 用于获取国际化消息

@author 张治保
date 2023/6/15
 
///

///
国际化配置路径
     
///

///
国际化消息源
     
///

///
获取国际化消息

@param code 消息code
@return 消息
     
///

///
使用系统默认locale 获取国际化消息

@param code 消息code
@return 国际化消息
     
///

///
获取国际化消息

@param code 消息code
@param args 参数
@return 消息
     
///

///
获取国际化消息 指定语言

@param code   消息code
@param locale 本地化 信息
@return 消息
     
///

///
获取国际化消息

@param code   消息code
@param locale 本地化 信息
@param args   参数信息
@return 消息
     
///

///
DCL 获取国际化消息源 {@link MessageSource}

@return 消息源
     
///

///
获取国际化配置路径

@return 路径集合
     
///

///
获取本地化信息 上下文不存在则默认使用简体中文

@return 本地化信息
     
///

///
设置消息源

@param messageSource 消息源
     
///


## 文件结构
```
项目根目录
└── global\i18n
    └── I18N.java
```

## 完整代码
```java
package com.medusa.gruul.global.i18n;

import cn.hutool.core.collection.CollUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 国际化 工具类 用于获取国际化消息
 *
 * @author 张治保
 * date 2023/6/15
 */
public class I18N {

    /**
     * 国际化配置路径
     */
    private static final Set<String> PATHS = new HashSet<>();
    /**
     * 国际化消息源
     */
    private static volatile MessageSource messageSource;


    /**
     * 获取国际化消息
     *
     * @param code 消息code
     * @return 消息
     */
    public static String msg(String code) {
        return I18N.msg(code, (Object[]) null);
    }

    /**
     * 使用系统默认locale 获取国际化消息
     *
     * @param code 消息code
     * @return 国际化消息
     */
    public static String msgDefault(String code) {
        return I18N.msg(code, Locale.getDefault());
    }

    /**
     * 获取国际化消息
     *
     * @param code 消息code
     * @param args 参数
     * @return 消息
     */
    public static String msg(String code, @Nullable Object... args) {
        return I18N.msg(code, getLocale(), args);
    }

    /**
     * 获取国际化消息 指定语言
     *
     * @param code   消息code
     * @param locale 本地化 信息
     * @return 消息
     */
    public static String msg(String code, Locale locale) {
        return I18N.msg(code, locale, (Object[]) null);
    }


    /**
     * 获取国际化消息
     *
     * @param code   消息code
     * @param locale 本地化 信息
     * @param args   参数信息
     * @return 消息
     */
    public static synchronized String msg(String code, Locale locale, @Nullable Object... args) {
        return I18N.messageSource().getMessage(code, args, locale);
    }

    /**
     * DCL 获取国际化消息源 {@link MessageSource}
     *
     * @return 消息源
     */
    public static MessageSource messageSource() {
        if (I18N.messageSource != null) {
            return I18N.messageSource;
        }
        synchronized (I18N.class) {
            if (I18N.messageSource == null) {
                ResourceBundleMessageSource newMessageSource = new ResourceBundleMessageSource();
                newMessageSource.setBasenames(I18N.paths().toArray(new String[0]));
                newMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
                newMessageSource.setFallbackToSystemLocale(true);
                newMessageSource.setUseCodeAsDefaultMessage(true);
                I18N.setMessageSource(newMessageSource);
            }
        }
        return I18N.messageSource;
    }

    /**
     * 获取国际化配置路径
     *
     * @return 路径集合
     */
    public static Set<String> paths() {
        if (CollUtil.isEmpty(PATHS)) {
            ServiceLoader.load(I18NPropertiesLoader.class)
                    .forEach(loader -> {
                        PATHS.addAll(loader.paths());
                        String path = loader.path();
                        if (path != null) {
                            PATHS.add(path);
                        }
                    });
        }
        return PATHS;
    }

    /**
     * 获取本地化信息 上下文不存在则默认使用简体中文
     *
     * @return 本地化信息
     */
    public static Locale getLocale() {
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
        }
        if (locale == null) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        return locale;
    }

    /**
     * 设置消息源
     *
     * @param messageSource 消息源
     */
    public static void setMessageSource(MessageSource messageSource) {
        synchronized (I18N.class) {
            I18N.messageSource = messageSource;
        }
    }
}

```

# com.medusa.gruul.global.i18n.I18NPropertiesLoader

**文件路径**: `global\i18n\I18NPropertiesLoader.java`

## 代码文档
///
国际化路径配置加载器

@author 张治保
date 2023/6/15
 
///

///
国际化配置路径 多个

@return 路径集合
     
///

///
国际化配置路径 单个

@return 路径
     
///


## 文件结构
```
项目根目录
└── global\i18n
    └── I18NPropertiesLoader.java
```

## 完整代码
```java
package com.medusa.gruul.global.i18n;

import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * 国际化路径配置加载器
 *
 * @author 张治保
 * date 2023/6/15
 */
public interface I18NPropertiesLoader {

    /**
     * 国际化配置路径 多个
     *
     * @return 路径集合
     */
    @NonNull
    default Set<String> paths() {
        return Set.of();
    }


    /**
     * 国际化配置路径 单个
     *
     * @return 路径
     */
    default String path() {
        return null;
    }
}

```

# com.medusa.gruul.global.i18n.ResourceBundleCondition

**文件路径**: `global\i18n\ResourceBundleCondition.java`

## 代码文档
///

 
///


## 文件结构
```
项目根目录
└── global\i18n
    └── ResourceBundleCondition.java
```

## 完整代码
```java
package com.medusa.gruul.global.i18n;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/**
 *
 */
public class ResourceBundleCondition extends SpringBootCondition {

    private static final Resource[] NO_RESOURCES = {};
    private static final ConcurrentReferenceHashMap<String, ConditionOutcome> CACHE = new ConcurrentReferenceHashMap<>();

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String basename = context.getEnvironment().getProperty("spring.messages.basename", "messages");
        ConditionOutcome outcome = CACHE.get(basename);
        if (outcome == null) {
            outcome = getMatchOutcomeForBasename(context, basename);
            CACHE.put(basename, outcome);
        }
        return outcome;
    }

    private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context, String basename) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("ResourceBundle");
        for (String name : StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename))) {
            for (Resource resource : getResources(context.getClassLoader(), name)) {
                if (resource.exists()) {
                    return ConditionOutcome.match(message.found("bundle").items(resource));
                }
            }
        }
        return ConditionOutcome.noMatch(message.didNotFind("bundle with basename " + basename).atAll());
    }

    private Resource[] getResources(ClassLoader classLoader, String name) {
        String target = name.replace('.', '/');
        try {
            return new PathMatchingResourcePatternResolver(classLoader)
                    .getResources("classpath*:" + target + ".properties");
        } catch (Exception ex) {
            return NO_RESOURCES;
        }
    }

}
```

