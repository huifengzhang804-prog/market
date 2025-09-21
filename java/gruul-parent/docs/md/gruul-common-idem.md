# com.medusa.gruul.common.idem.IdemAutoconfigure

**文件路径**: `common\idem\IdemAutoconfigure.java`

## 代码文档
///
幂等性校验工具自动装配
增加幂等响应头 IDEM

@author 张治保
date 2022/2/9
 
///


## 文件结构
```
项目根目录
└── common\idem
    └── IdemAutoconfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.idem;

import com.medusa.gruul.common.idem.aspect.IdemAdvisor;
import com.medusa.gruul.common.idem.aspect.IdemInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * 幂等性校验工具自动装配
 * 增加幂等响应头 IDEM
 *
 * @author 张治保
 * date 2022/2/9
 */
@Import({IdemAdvisor.class, IdemInterceptor.class})
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate", "org.springframework.aop.support.annotation.AnnotationMatchingPointcut"})
public class IdemAutoconfigure {


}

```

# com.medusa.gruul.common.idem.IdemI18NLoader

**文件路径**: `common\idem\IdemI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── common\idem
    └── IdemI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.idem;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class IdemI18NLoader implements I18NPropertiesLoader {
	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/idem");
	}
}

```

# com.medusa.gruul.common.idem.annotation.Idem

**文件路径**: `idem\annotation\Idem.java`

## 代码文档
///
接口幂等性注解

@author 张治保
 
///

///
过期时间

@return 过期时间/单位 毫秒 默认2秒 2秒内重复提交无效
     
///

///
过期时间

@return 过期时间/单位 毫秒 默认2秒 2秒内重复提交无效
     
///

///
时间单位

@return 时间单位
     
///


## 文件结构
```
项目根目录
└── idem\annotation
    └── Idem.java
```

## 完整代码
```java
package com.medusa.gruul.common.idem.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口幂等性注解
 *
 * @author 张治保
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Idem {
    /**
     * 过期时间
     *
     * @return 过期时间/单位 毫秒 默认2秒 2秒内重复提交无效
     */
    @AliasFor("expire")
    long value() default 2000;

    /**
     * 过期时间
     *
     * @return 过期时间/单位 毫秒 默认2秒 2秒内重复提交无效
     */
    @AliasFor("value")
    long expire() default 2000;

    /**
     * 时间单位
     *
     * @return 时间单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}

```

# com.medusa.gruul.common.idem.aspect.IdemAdvisor

**文件路径**: `idem\aspect\IdemAdvisor.java`

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
└── idem\aspect
    └── IdemAdvisor.java
```

## 完整代码
```java
package com.medusa.gruul.common.idem.aspect;

import com.medusa.gruul.common.idem.annotation.Idem;
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
public class IdemAdvisor extends AbstractPointcutAdvisor {

    /**
     * 方法增强
     */
    private final Advice advice;
    /**
     * 切点
     */
    private final Pointcut pointcut;

    public IdemAdvisor(IdemInterceptor advice) {
        this.advice = advice;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(Idem.class);
        super.setOrder(AspectOrder.IDEM_ASPECT);
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

# com.medusa.gruul.common.idem.aspect.IdemInterceptor

**文件路径**: `idem\aspect\IdemInterceptor.java`

## 代码文档
///
@author 张治保
date 2022/2/15
 
///

///
@param deviceId   设备id
@param methodName 执行方法名
@param expire     过期时间
@return 是否允许继续执行
     
///

///
获取执行方法名 用于拼接缓存key
     
///


## 文件结构
```
项目根目录
└── idem\aspect
    └── IdemInterceptor.java
```

## 完整代码
```java
package com.medusa.gruul.common.idem.aspect;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.idem.model.IdemError;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @author 张治保
 * date 2022/2/15
 */
@Slf4j
@RequiredArgsConstructor
public class IdemInterceptor implements MethodInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        log.debug("@Idem annotation aspect working...");
        /*
         * 检查设备id
         */
        String deviceId = ISystem.deviceIdOpt().getOrElseThrow(() -> new GlobalException("bad request"));
        /*
         * 获取注解
         */
        Idem annotation = AnnotationUtils.findAnnotation(invocation.getMethod(), Idem.class);
        assert annotation != null;
        boolean allow = isAllow(
                deviceId,
                getMethodName(invocation.getMethod()),
                annotation.unit().toMillis(annotation.expire())
        );
        if (allow) {
            return invocation.proceed();
        }
        throw IdemError.REPEAT_SUBMIT.exception();
    }

    /**
     * @param deviceId   设备id
     * @param methodName 执行方法名
     * @param expire     过期时间
     * @return 是否允许继续执行
     */
    public final boolean isAllow(String deviceId, String methodName, long expire) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(
                "gruul:idem:" + methodName + StrPool.COLON + deviceId,
                StrUtil.EMPTY,
                Duration.ofMillis(expire)
        );
        return result != null && result;
    }

    /**
     * 获取执行方法名 用于拼接缓存key
     */
    public final String getMethodName(Method method) {
        return method.getDeclaringClass().getName() + ":" + method.getName();
    }


}

```

# com.medusa.gruul.common.idem.model.IdemError

**文件路径**: `idem\model\IdemError.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///

///
重复提交不生效
     
///


## 文件结构
```
项目根目录
└── idem\model
    └── IdemError.java
```

## 完整代码
```java
package com.medusa.gruul.common.idem.model;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/16
 */
@Getter
@RequiredArgsConstructor
public enum IdemError implements Error {
    /**
     * 重复提交不生效
     */
    REPEAT_SUBMIT(1001, "");

    private final int code;

    private final String msgCode;

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return I18N.msg("idem.repeat.submit");
    }
}

```

