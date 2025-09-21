package com.medusa.gruul.common.redis.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.expression.ExpressionUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.MultiRedisson;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.constant.RedisConstant;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.config.spel.SpElContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/2/15
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonInterceptor implements MethodInterceptor {

    private final RedissonClient redissonClient;

    /**
     * 将参数值转换为集合
     *
     * @param value 参数值 可以是集合、Map、数组（数组做有限支持，部分基础数据类型不支持，基础数据类型仅支持int、long、double）
     * @return 集合
     */
    private static Collection<?> valueToCollection(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection;
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet();
        }
        Class<?> valueClass;
        if (value != null && (valueClass = value.getClass()).isArray()) {
            //非基础数据类型
            if (!valueClass.getComponentType().isPrimitive()) {
                return Arrays.asList((Object[]) value);
            }
            // 处理基础数据类型的情况
            if (value instanceof int[] vals) {
                return Arrays.stream(vals).boxed().toList();
            }
            if (value instanceof double[] vals) {
                return Arrays.stream(vals).boxed().toList();
            }
            if (value instanceof long[] vals) {
                return Arrays.stream(vals).boxed().toList();
            }
            //其它基础类型 暂不支持
        }
        throw SystemCode.PARAM_MISS.exception();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //获取注解 有序的LinkedHashSet
        Set<Redisson> redissonSet = AnnotatedElementUtils.getMergedRepeatableAnnotations(invocation.getMethod(), Redisson.class, MultiRedisson.class);
        if (CollUtil.isEmpty(redissonSet)) {
            return invocation.proceed();
        }
        //按照注解顺序挨个加锁 最后执行业务逻辑返回结果
        return this.batchLockInvoke(redissonSet.iterator(), invocation);
    }

    /**
     * 按照注解顺序执行加锁 递归调用
     *
     * @param iterator   注解迭代器
     * @param invocation 代理方法执行器
     * @return 执行结果
     * @throws Throwable 异常
     */
    private Object batchLockInvoke(Iterator<Redisson> iterator, MethodInvocation invocation) throws Throwable {
        if (!iterator.hasNext()) {
            return invocation.proceed();
        }
        Redisson annotation = iterator.next();
        if (conditionFail(annotation.condition(), invocation)) {
            log.debug("....redisson condition match fail.... [{}]", annotation.condition());
            return this.batchLockInvoke(iterator, invocation);
        }
        //渲染锁生成锁
        RLock lock = this.getLock(annotation, invocation);
        //尝试获取锁
        boolean success = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.unit());
        //锁名称
        String name = lock.getName();
        //获取失败 抛出系统繁忙的异常
        if (!success) {
            log.debug("....redisson try lock fail.... [{}]", name);
            throw SystemCode.SYSTEM_BUSY.exception();
        }
        log.debug("....redisson locked.... [{}]", name);
        try {
            return this.batchLockInvoke(iterator, invocation);
        } finally {
            lock.unlock();
            log.debug("....redisson unlocked .... [{}]", name);
        }
    }

    /**
     * 条件判断是否不使用锁
     *
     * @return true 不使用锁 false 使用锁
     */
    private boolean conditionFail(String condition, MethodInvocation invocation) {
        if (StrUtil.isEmpty(condition)) {
            return false;
        }
        return !((boolean) ExpressionUtil.eval(
                condition,
                SpElContext.of()
                        .root(invocation.getThis())
                        .method(invocation.getMethod(), invocation.getArguments())
        ));
    }

    /**
     * 获取分布式锁
     * 1。 单个锁
     * 2。 批量锁
     *
     * @param annotation 分布式锁 注解
     * @param invocation 代理方法执行器
     * @return 分布式锁
     */
    private RLock getLock(Redisson annotation, MethodInvocation invocation) {
        String param = annotation.batchParamName();
        //是否是批量锁
        if (StrUtil.isBlank(param)) {
            //单锁
            String key = this.singleKey(annotation.value(), annotation.key(), invocation);
            return redissonClient.getLock(key);
        }
        //批量锁
        //渲染所有锁的key
        Set<String> multiLockKeys = getMultiLockKeys(annotation, invocation);
        return new RedissonMultiLock(
                multiLockKeys
                        .stream()
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        ) {
            @Override
            public String getName() {
                return multiLockKeys.toString();
            }
        };
    }

    /**
     * 获取批量锁 key 集合
     *
     * @param annotation 注解
     * @param invocation 代理方法执行期
     * @return 批量锁 key 集合
     */
    private Set<String> getMultiLockKeys(Redisson annotation, MethodInvocation invocation) {
        String name = annotation.value();
        String batchParam = annotation.batchParamName();
        String key = annotation.key();
        //取出 批量参数值
        Object batchParamValue = ExpressionUtil.eval(
                batchParam,
                SpElContext.of()
                        .root(invocation.getThis())
                        .method(invocation.getMethod(), invocation.getArguments())
        );
        // 批量参数转换为集合
        Collection<?> items = valueToCollection(batchParamValue);
        // 渲染所有锁的key
        return items.stream()
                .map(item -> this.batchItemKey(name, key, invocation, item))
                .collect(Collectors.toSet());
    }

    /**
     * 获取批量锁 单个锁的key值
     *
     * @param name       配置的注解锁名称
     * @param key        配置的key值 支持spEL
     * @param invocation 代理方法执行器
     * @param item       单个对象值
     * @return 锁的全路径key
     */
    private String batchItemKey(String name, String key, MethodInvocation invocation, Object item) {
        return this.key(name, key, invocation, Map.of(RedisConstant.BATCH_LOCK_ITEM, item));
    }


    /**
     * 单个锁的key值
     *
     * @param name       配置的注解锁名称
     * @param key        配置的key值 支持spEL
     * @param invocation 代理方法执行器
     * @return 锁的全路径key
     */
    private String singleKey(String name, String key, MethodInvocation invocation) {
        return this.key(name, key, invocation, null);
    }

    /**
     * 单个锁的key值
     *
     * @param name       配置的注解锁名称
     * @param key        配置的key值 支持spEL
     * @param invocation 代理方法执行器
     * @param otherParam 其它参数列表
     * @return 锁的全路径key
     */
    private String key(String name, String key, MethodInvocation invocation, Map<String, Object> otherParam) {
        return RedisUtil.getLockKey(name, key(key, invocation, otherParam));
    }

    /**
     * 获取key值
     *
     * @param key        配置的key值 支持spEL
     * @param invocation 代理方法执行器
     * @param otherParam 其它参数列表
     * @return key值
     */
    private String key(String key, MethodInvocation invocation, Map<String, Object> otherParam) {
        return StrUtil.isEmpty(key) ?
                null :
                String.valueOf(
                        ExpressionUtil.eval(
                                key,
                                SpElContext.of(otherParam)
                                        .root(invocation.getThis())
                                        .method(invocation.getMethod(), invocation.getArguments())
                        )
                );
    }
}
