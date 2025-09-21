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
