# com.medusa.gruul.common.log.LogAutoConfigure

**文件路径**: `common\log\LogAutoConfigure.java`

## 代码文档
///
@author 张治保
date 2022/2/15
 
///


## 文件结构
```
项目根目录
└── common\log
    └── LogAutoConfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.log;

import com.medusa.gruul.common.log.aspect.LogAdvisor;
import com.medusa.gruul.common.log.aspect.LogInterceptor;
import com.medusa.gruul.common.log.properties.LogProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/2/15
 */
@EnableConfigurationProperties(LogProperties.class)
@Configuration
public class LogAutoConfigure {

    @Bean
    public LogAdvisor logAdvisor(LogProperties logProperties){
        return new LogAdvisor(new LogInterceptor(logProperties));
    }
}

```

# com.medusa.gruul.common.log.annotation.Log

**文件路径**: `log\annotation\Log.java`

## 代码文档
///
@author 张治保
date 2022/2/15
 
///

///
日志描述
     
///

///
日志级别
     
///

///
请求头名称
     
///


## 文件结构
```
项目根目录
└── log\annotation
    └── Log.java
```

## 完整代码
```java
package com.medusa.gruul.common.log.annotation;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.*;

/**
 * @author 张治保
 * date 2022/2/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log{

    /**
     * 日志描述
     */
    String value() default "";

    /**
     * 日志级别
     */
    LogLevel level() default LogLevel.DEBUG;

    /**
     * 请求头名称
     */
    String[] headers() default {};


}

```

# com.medusa.gruul.common.log.aspect.LogAdvisor

**文件路径**: `log\aspect\LogAdvisor.java`

## 代码文档
///
@author 张治保
date 2022/2/10
 
///

///
方法增强
     
///

///
切点
     
///


## 文件结构
```
项目根目录
└── log\aspect
    └── LogAdvisor.java
```

## 完整代码
```java
package com.medusa.gruul.common.log.aspect;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.global.model.constant.AspectOrder;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.NonNull;

/**
 * @author 张治保
 * date 2022/2/10
 */
public class LogAdvisor extends AbstractPointcutAdvisor {

    /**
     * 方法增强
     */
    private final Advice advice;
    /**
     * 切点
     */
    private final Pointcut pointcut;

    public LogAdvisor(LogInterceptor logInterceptor) {
        this.advice = logInterceptor;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(Log.class);
        super.setOrder(AspectOrder.LOG_ASPECT);
    }

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    @NonNull
    public Advice getAdvice() {
        return advice;
    }
}

```

# com.medusa.gruul.common.log.aspect.LogInterceptor

**文件路径**: `log\aspect\LogInterceptor.java`

## 代码文档
///
@author 张治保
date 2022/2/15
 
///

///
判断是否需要打印
     
///

///
渲染基础参数
     
///

///
渲染请求参数
     
///

///
渲染请求头参数
     
///

///
渲染返回值
     
///


## 文件结构
```
项目根目录
└── log\aspect
    └── LogInterceptor.java
```

## 完整代码
```java
package com.medusa.gruul.common.log.aspect;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.log.properties.LogProperties;
import com.medusa.gruul.common.system.model.ISystem;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 张治保
 * date 2022/2/15
 */
@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements MethodInterceptor {

    private final LogProperties properties;

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        log.debug("@Log annotation aspect working...");

        Log logAnnotation = this.getLogAnnotation(invocation.getMethod());
        if (logAnnotation == null) {
            return invocation.proceed();
        }
        boolean logEnable = isLogEnable(logAnnotation.level());
        //不能打印该级别日志 直接返回执行结果
        if (!logEnable) {
            return invocation.proceed();
        }
        Object invokeResult;
        TimeInterval timer = new TimeInterval();
        try {
            invokeResult = invocation.proceed();
        } catch (Exception exception) {
            invokeResult = exception;
        }
        long timeConsuming = timer.intervalMs();
        boolean hashError = invokeResult instanceof Exception;
        Object result;
        if (hashError) {
            result = invokeResult;
        } else {
            result = invocation.getMethod().getReturnType() == void.class ? void.class : invokeResult;
        }
        /*
         * 异步打印
         */
        Request requestInfo = requestInfo(ISystem.ipOpt().getOrNull(), Thread.currentThread().getName());
        ThreadUtil.execute(() -> this.renderAndPrintLog(logAnnotation, requestInfo, result, invocation, timeConsuming));
        if (hashError) {
            throw (Exception) invokeResult;
        }
        return invokeResult;
    }

    private Request requestInfo(String ip, String threadName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Request requestInfo = new Request()
                .setIp(ip)
                .setThreadName(threadName);
        if (attributes == null) {
            return requestInfo;
        }
        HttpServletRequest request = attributes.getRequest();
        Map<String, String> headers = new HashMap<>(16);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        return requestInfo.setMethod(request.getMethod())
                .setUrl(request.getRequestURL().toString())
                .setHeaders(headers);
    }

    private void renderAndPrintLog(
            Log logAnnotation,
            Request request,
            Object result,
            MethodInvocation invocation,
            long time
    ) {
        StringBuilder logs = new StringBuilder();
        List<Object> args = new LinkedList<>();
        // 打印请求相关参数
        logs.append("\n========================================== Start ==========================================\n");
        /*
         * 基础信息
         */
        this.renderBaseInfo(logAnnotation.value(), invocation.getMethod(), request, logs, args);
        /*
         * 请求头信息
         */
        this.renderRequestHeaders(logAnnotation.headers(), request.getHeaders(), logs, args);

        /*
         * 请求参数
         */
        this.renderRequestParam(invocation.getArguments(), logs, args);
        /*
         * 返回值
         */
        this.renderResponse(result, logs, args);
        /*
         *执行时间
         */
        logs.append("Time-Consuming : {} ms\n");
        args.add(time);
        logs.append("=========================================== End ===========================================\n");

        this.printLog(logAnnotation, logs.toString(), args.toArray());
    }

    private Log getLogAnnotation(Method method) {
        Log annotation = AnnotationUtils.findAnnotation(method, Log.class);
        if (annotation != null) {
            return annotation;
        }
        return AnnotationUtils.findAnnotation(method.getDeclaringClass(), Log.class);
    }

    /**
     * 判断是否需要打印
     */
    private boolean isLogEnable(LogLevel level) {
        return switch (level) {
            case TRACE -> log.isTraceEnabled();
            case DEBUG -> log.isDebugEnabled();
            case INFO -> log.isInfoEnabled();
            case WARN -> log.isWarnEnabled();
            case ERROR -> log.isErrorEnabled();
            default -> false;
        };
    }


    private void printLog(Log logAnnotation, String logStr, Object... args) {
        switch (logAnnotation.level()) {
            case TRACE -> LogInterceptor.log.trace(logStr, args);
            case DEBUG -> LogInterceptor.log.debug(logStr, args);
            case INFO -> LogInterceptor.log.info(logStr, args);
            case WARN -> LogInterceptor.log.warn(logStr, args);
            case ERROR -> LogInterceptor.log.error(logStr, args);
            default -> {
            }
        }
    }

    /**
     * 渲染基础参数
     */
    private void renderBaseInfo(String description, Method method, Request request, StringBuilder logs, List<Object> args) {
        //线程id
        logs.append("ThreadName     : {} \n");
        args.add(request.getThreadName());

        if (StrUtil.isNotEmpty(description)) {
            logs.append("Description    : {} \n");
            args.add(description);
        }
        // 打印请求 url
        if (StrUtil.isNotEmpty(request.getMethod())) {
            logs.append("URL            : [{}]{}\n");
            args.add(request.getMethod());
            args.add(request.getUrl());
        }
        // 打印调用 controller 的全路径以及执行方法
        logs.append("Class Method   : {}.{}\n");
        args.add(method.getDeclaringClass().getName());
        args.add(method.getName());
        /*
         * 客户端ip
         */
        String ip = request.getIp();
        if (StrUtil.isNotEmpty(ip)) {
            logs.append("IP             : {}\n");
            args.add(ip);
        }
    }

    /**
     * 渲染请求参数
     */
    private void renderRequestParam(Object[] arguments, StringBuilder logs, List<Object> args) {
        if (ArrayUtil.isEmpty(arguments)) {
            return;
        }
        logs.append("Request Args   : (");
        for (Object arg : arguments) {
            if ((arg instanceof ServletResponse) || (arg instanceof ServletRequest)) {
                continue;
            }
            logs.append("{}").append(",");
            args.add(arg);
        }
        logs.deleteCharAt(logs.length() - 1);
        logs.append(")\n");
    }

    /**
     * 渲染请求头参数
     */
    private void renderRequestHeaders(String[] annotationHeaders, Map<String, String> headers, StringBuilder logs, List<Object> args) {
        Set<String> configHeaders = new HashSet<>(properties.getHeaderNames());
        configHeaders.addAll(Arrays.asList(annotationHeaders));
        configHeaders.forEach(
                configHeader -> {
                    String value = headers.get(configHeader);
                    logs.append("Header         : {}: {}\n");
                    args.add(configHeader);
                    args.add(value);
                }
        );
    }

    /**
     * 渲染返回值
     */
    private void renderResponse(Object response, StringBuilder logs, List<Object> args) {
        logs.append("Response Args  : {}\n");
        if (response == null) {
            args.add("null");
        }
        args.add(response == void.class ? "void" : response);
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    private static class Request {
        private String ip;
        private String threadName;
        private String method;
        private String url;
        private Map<String, String> headers;
    }

}

```

# com.medusa.gruul.common.log.properties.LogProperties

**文件路径**: `log\properties\LogProperties.java`

## 代码文档
///
日志配置类
@author 张治保
date 2022/2/15
 
///


## 文件结构
```
项目根目录
└── log\properties
    └── LogProperties.java
```

## 完整代码
```java
package com.medusa.gruul.common.log.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志配置类
 * @author 张治保
 * date 2022/2/15
 */
@ConfigurationProperties(prefix = "logging")
@Getter
@Setter
public class LogProperties {

    private Set<String> headerNames = new HashSet<>();

}

```

