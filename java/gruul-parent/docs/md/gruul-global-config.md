# com.medusa.gruul.global.config.Global

**文件路径**: `global\config\Global.java`

## 代码文档
///
@author 张治保
@since 2024/6/6
 
///

///
全局任务线程池 bean name
     
///

///
全局线程池 bean name
     
///

///
拼接url

@param urls url
@return url
     
///


## 文件结构
```
项目根目录
└── global\config
    └── Global.java
```

## 完整代码
```java
package com.medusa.gruul.global.config;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;

/**
 * @author 张治保
 * @since 2024/6/6
 */
public interface Global {

    /**
     * 全局任务线程池 bean name
     */
    String GLOBAL_TASK_EXECUTOR = "globalTaskExecutor";

    /**
     * 全局线程池 bean name
     */
    String GLOBAL_EXECUTOR = "globalExecutor";


    /**
     * 拼接url
     *
     * @param urls url
     * @return url
     */
    static String concatUrl(String... urls) {
        char slash = '/';
        StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            if (StrUtil.isBlank(url)) {
                continue;
            }
            url = url.trim();
            if (url.equals(StrPool.SLASH)) {
                continue;
            }
            if (sb.isEmpty()) {
                sb.append(url);
                continue;
            }
            boolean lastIsSlash = sb.charAt(sb.length() - 1) == slash;
            boolean urlFirstSlash = url.charAt(0) == slash;
            // 两边都有斜杠时，去掉一个斜杠
            if (lastIsSlash && urlFirstSlash) {
                sb.append(url, 1, url.length());
                continue;
            }
            // 两边都没有斜杠时，添加一个斜杠
            if (!lastIsSlash && !urlFirstSlash) {
                sb.append(slash)
                        .append(url);
                continue;
            }
            // 其他情况下直接拼接
            sb.append(url);
        }
        return sb.toString();
    }
}

```

# com.medusa.gruul.global.config.GlobalAppProperties

**文件路径**: `global\config\GlobalAppProperties.java`

## 代码文档
///
@author 张治保
date 2022/3/23
 
///

///
应用服务名
     
///

///
应用程序版本号
     
///

///
基础请求 url
<a href="https://test.bgniao.cn/api">例子</a>
     
///

///
是否是单体应用
     
///

///
业务运行模式 默认B2B2C
     
///

///
线程池配置
     
///

///
@param path api路径
@return 完整url
     
///


## 文件结构
```
项目根目录
└── global\config
    └── GlobalAppProperties.java
```

## 完整代码
```java
package com.medusa.gruul.global.config;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.enums.Mode;
import com.medusa.gruul.global.model.o.ThreadPoolProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;


/**
 * @author 张治保
 * date 2022/3/23
 */
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gruul")
public class GlobalAppProperties implements EnvironmentAware, InitializingBean {


    private Environment environment;

    /**
     * 应用服务名
     */
    private String name;
    /**
     * 应用程序版本号
     */
    private String version = "1.0";

    /**
     * 基础请求 url
     * <a href="https://test.bgniao.cn/api">例子</a>
     */
    private String baseUrl;

    /**
     * 是否是单体应用
     */
    private boolean single = false;

    /**
     * 业务运行模式 默认B2B2C
     */
    private Mode mode = Mode.B2B2C;


    /**
     * 线程池配置
     */
    @NestedConfigurationProperty
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();


    @Override
    public void afterPropertiesSet() {
        if (StrUtil.isEmpty(this.name)) {
            this.name = environment.resolvePlaceholders("${spring.application.name}");
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }


    /**
     * @param path api路径
     * @return 完整url
     */
    public String fullUrl(String path) {
        return Global.concatUrl(this.baseUrl, path);
    }
}


```

# com.medusa.gruul.global.config.GlobalConfigAutoConfigure

**文件路径**: `global\config\GlobalConfigAutoConfigure.java`

## 代码文档
///
@author 张治保
date 2022/3/23
 
///

///
全局线程池配置类
     
///

///
全局普通线程池
         
///

///
全局可传递上下文的线程池
         
///


## 文件结构
```
项目根目录
└── global\config
    └── GlobalConfigAutoConfigure.java
```

## 完整代码
```java
package com.medusa.gruul.global.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.global.config.helper.ExecutorHelper;
import com.medusa.gruul.global.config.spel.SpringElEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;

import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/3/23
 */
@Import(GlobalConfigAutoConfigure.ExecutorConfig.class)
@EnableConfigurationProperties(GlobalAppProperties.class)
public class GlobalConfigAutoConfigure implements ApplicationContextAware {


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringElEngine.setApplicationContext(applicationContext);
    }


    /**
     * 全局线程池配置类
     */
    @RequiredArgsConstructor
    public static class ExecutorConfig {
        private final GlobalAppProperties properties;

        /**
         * 全局普通线程池
         */
        @Bean(name = Global.GLOBAL_TASK_EXECUTOR)
        public TaskExecutor globalTaskExecutor() {
            return ExecutorHelper.toTaskExecutor(properties.getThreadPool());
        }

        /**
         * 全局可传递上下文的线程池
         */
        @Bean(name = Global.GLOBAL_EXECUTOR)
        public Executor globalExecutor(TaskExecutor globalTaskExecutor) {
            return TtlExecutors.getTtlExecutor(globalTaskExecutor);
        }
    }


}

```

# com.medusa.gruul.global.config.NumberPropertySource

**文件路径**: `global\config\NumberPropertySource.java`

## 代码文档
///
配置增加 number.add() 函数

@author 张治保
date 2023/6/28
 
///


## 文件结构
```
项目根目录
└── global\config
    └── NumberPropertySource.java
```

## 完整代码
```java
package com.medusa.gruul.global.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.*;

/**
 * 配置增加 number.add() 函数
 *
 * @author 张治保
 * date 2023/6/28
 */
public class NumberPropertySource extends PropertySource<NumberPropertySource> implements EnvironmentPostProcessor {

	public static final String NAME = "number";
	public static final String PREFIX = "number.";
	public static final String ADD_FUNC_NAME = "add";
	private Environment environment;

	public NumberPropertySource() {
		super(NAME);
	}


	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		this.environment = environment;
		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.addAfter(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
				this
		);
	}

	@Override
	public Object getProperty(String name) {
		if (!name.startsWith(PREFIX)) {
			return null;
		}
		String function = name.substring(PREFIX.length());
		return evaluate(function);
	}

	private Object evaluate(String function) {
		if (!function.startsWith(ADD_FUNC_NAME)) {
			return null;
		}
		String numbersStr = function.substring(ADD_FUNC_NAME.length() + 1, function.length() - 1);
		String[] numbers = numbersStr.split(",");
		long result = 0L;
		for (String numberStr : numbers) {
			String trim = numberStr.trim();
			Long number = environment.getProperty(trim, Long.class);
			if (number == null) {
				number = Long.parseLong(trim);
			}
			result += number;
		}
		return result;
	}

}

```

# com.medusa.gruul.global.config.helper.ExecutorHelper

**文件路径**: `config\helper\ExecutorHelper.java`

## 代码文档
///
线程池辅助类

@author 张治保
@since 2023/12/8
 
///

///
线程池通用配置详情

@param executorProperties 线程池通用配置详情
@return org.springframework.core.task.TaskExecutor 线程池
     
///

///
线程池通用配置详情

@param rejectedExecutionHandler 任务拒绝策略
@param executorProperties       线程池通用配置详情
@return org.springframework.core.task.TaskExecutor 线程池
     
///


## 文件结构
```
项目根目录
└── config\helper
    └── ExecutorHelper.java
```

## 完整代码
```java
package com.medusa.gruul.global.config.helper;

import com.medusa.gruul.global.model.o.ThreadPoolProperties;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池辅助类
 *
 * @author 张治保
 * @since 2023/12/8
 */
public interface ExecutorHelper {

    /**
     * 线程池通用配置详情
     *
     * @param executorProperties 线程池通用配置详情
     * @return org.springframework.core.task.TaskExecutor 线程池
     */
    static TaskExecutor toTaskExecutor(ThreadPoolProperties executorProperties) {
        return toTaskExecutor(new ThreadPoolExecutor.CallerRunsPolicy(), executorProperties);
    }

    /**
     * 线程池通用配置详情
     *
     * @param rejectedExecutionHandler 任务拒绝策略
     * @param executorProperties       线程池通用配置详情
     * @return org.springframework.core.task.TaskExecutor 线程池
     */
    static TaskExecutor toTaskExecutor(RejectedExecutionHandler rejectedExecutionHandler, ThreadPoolProperties executorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(executorProperties.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.initialize();
        return executor;
    }
}

```

# com.medusa.gruul.global.config.spel.SpElContext

**文件路径**: `config\spel\SpElContext.java`

## 代码文档
///
sp el 表达式上下文 用于准备执行表达式的上下文信息
example ExpressionUtil.eval("a+b", SpElContext.of().set("a", 1).set("b", 2)) = 3
example ExpressionUtil.eval("#a+#b", SpElContext.of().method(method, arguments))

@author 张治保
@see ExpressionUtil,SpringElEngine
@since 2023/11/10
 
///

///
初始化 SpElContext 对象

@return 一个新的SpElContext
     
///

///
初始化 SpElContext 对象

@param context 上下文初始化
     
///

///
设置根对象 可以通过 spring el 直接调用根对象的方法

@param root 根对象
@return this
     
///

///
设置方法和方法参数

@param method    方法
@param arguments 方法参数
@return this
     
///

///
设置方法返回值

@param result 方法返回值
@return this
     
///

///
获取方法

@return 方法
     
///

///
获取方法参数

@return 方法参数
     
///

///
获取根对象

@return 根对象
     
///


## 文件结构
```
项目根目录
└── config\spel
    └── SpElContext.java
```

## 完整代码
```java
package com.medusa.gruul.global.config.spel;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.expression.ExpressionUtil;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * sp el 表达式上下文 用于准备执行表达式的上下文信息
 * example ExpressionUtil.eval("a+b", SpElContext.of().set("a", 1).set("b", 2)) = 3
 * example ExpressionUtil.eval("#a+#b", SpElContext.of().method(method, arguments))
 *
 * @author 张治保
 * @see ExpressionUtil,SpringElEngine
 * @since 2023/11/10
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SpElContext extends Dict {

    private static final String ROOT_OBJECT = SpringElEngine.class.getName() + "rootObject";
    private static final String METHOD = SpringElEngine.class.getName() + "method";
    private static final String METHOD_ARGUMENTS = SpringElEngine.class.getName() + "methodArguments";
    private static final String RESULT = "result";


    private SpElContext(Map<String, Object> map) {
        super(map);
    }

    /**
     * 初始化 SpElContext 对象
     *
     * @return 一个新的SpElContext
     */
    public static SpElContext of() {
        return new SpElContext();
    }

    /**
     * 初始化 SpElContext 对象
     *
     * @param context 上下文初始化
     */
    public static SpElContext of(Map<String, Object> context) {
        if (context instanceof SpElContext spElContext) {
            return spElContext;
        }
        return new SpElContext(context);
    }

    /**
     * 设置根对象 可以通过 spring el 直接调用根对象的方法
     *
     * @param root 根对象
     * @return this
     */
    public SpElContext root(Object root) {
        return (SpElContext) this.set(ROOT_OBJECT, root);


    }

    /**
     * 设置方法和方法参数
     *
     * @param method    方法
     * @param arguments 方法参数
     * @return this
     */
    public SpElContext method(Method method, Object[] arguments) {
        return (SpElContext) this.set(METHOD, method)
                .set(METHOD_ARGUMENTS, arguments);
    }

    /**
     * 设置方法返回值
     *
     * @param result 方法返回值
     * @return this
     */
    public SpElContext result(Object result) {
        return (SpElContext) this.set(RESULT, result);
    }

    /**
     * 获取方法
     *
     * @return 方法
     */
    public Method getMethod() {
        return (Method) get(METHOD);
    }

    /**
     * 获取方法参数
     *
     * @return 方法参数
     */
    public Object[] getMethodArguments() {
        return (Object[]) get(METHOD_ARGUMENTS);
    }

    /**
     * 获取根对象
     *
     * @return 根对象
     */
    public Object getRoot() {
        return get(ROOT_OBJECT);
    }

}

```

# com.medusa.gruul.global.config.spel.SpringElEngine

**文件路径**: `config\spel\SpringElEngine.java`

## 代码文档
///
sp el 表达式解析引擎

@author 张治保
@since 2023/11/10
 
///


## 文件结构
```
项目根目录
└── config\spel
    └── SpringElEngine.java
```

## 完整代码
```java
package com.medusa.gruul.global.config.spel;

import cn.hutool.extra.expression.ExpressionEngine;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * sp el 表达式解析引擎
 *
 * @author 张治保
 * @since 2023/11/10
 */
@Component
public class SpringElEngine implements ExpressionEngine {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static BeanResolver beanResolver;

    public static void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringElEngine.beanResolver = new BeanFactoryResolver(applicationContext);
    }

    @Override
    public Object eval(String expression, Map<String, Object> context, Collection<Class<?>> allowClassSet) {
        return EXPRESSION_PARSER.parseExpression(expression).getValue(evaluationContext(context));
    }

    private EvaluationContext evaluationContext(Map<String, Object> context) {
        SpElContext spElContext = SpElContext.of(context);
        StandardEvaluationContext evaluationContext;
        Method method = spElContext.getMethod();
        if (method != null) {
            evaluationContext = new MethodBasedEvaluationContext(
                    null, method, spElContext.getMethodArguments(), PARAMETER_NAME_DISCOVERER
            );
        } else {
            evaluationContext = new StandardEvaluationContext();
        }
        evaluationContext.setRootObject(spElContext.getRoot());
        if (beanResolver != null) {
            evaluationContext.setBeanResolver(beanResolver);
        }
        context.forEach(evaluationContext::setVariable);
        return evaluationContext;
    }
}

```

