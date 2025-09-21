package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.o.FormInput;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.OrderForm;
import com.medusa.gruul.order.api.entity.OrderTimeout;
import com.medusa.gruul.order.service.modules.order.service.OrderConfigService;
import com.medusa.gruul.order.service.mp.service.IOrderFormService;
import com.medusa.gruul.order.service.mp.service.IOrderTimeoutService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/2/8
 */
@Service
@RequiredArgsConstructor
public class OrderConfigServiceImpl implements OrderConfigService {


    private final IOrderFormService orderFormService;
    private final IOrderTimeoutService orderTimeoutService;
    private volatile OrderTimeout timeout;

    @Override
    @Redisson(name = OrderConstant.ORDER_TIMEOUT_LOCK)
    public void timeout(OrderTimeout timeout) {
        this.timeout = null;
        RedisUtil.doubleDeletion(
                () -> {
                    Option<OrderTimeout> option = Option.of(orderTimeoutService.lambdaQuery().one());
                    if (option.isEmpty()) {
                        orderTimeoutService.save(timeout);
                    }
                    orderTimeoutService.update(timeout, null);
                },
                OrderConstant.ORDER_TIMEOUT_CACHE
        );
    }

    @Override
    public OrderTimeout timeout() {
        if (this.timeout != null) {
            return this.timeout;
        }
        return this.timeout = RedisUtil.getCacheMap(
                OrderTimeout.class,
                this::getTimeout,
                Duration.ofDays(7),
                OrderConstant.ORDER_TIMEOUT_CACHE
        );
    }


    private OrderTimeout getTimeout() {
        return Option.of(orderTimeoutService.lambdaQuery().one())
                .getOrElse(
                        () -> {
                            //默认值
                            return new OrderTimeout()
                                    .setPayTimeout(OrderTimeout.DEFAULT_PAY_TIME_OUT)
                                    .setConfirmTimeout(OrderTimeout.DEFAULT_CONFIRM_TIME_OUT)
                                    .setCommentTimeout(OrderTimeout.DEFAULT_COMMENT_TIME_OUT)
                                    .setAfsAuditTimeout(OrderTimeout.DEFAULT_AFS_AUDIT_TIME_OUT);
                        }
                );
    }


    /**
     * 需要加锁操作
     *
     * @param shopId    店铺id
     * @param orderForm shopDealSettings
     */
    @Override
    @Redisson(value = OrderConstant.ORDER_FORM_LOCK, key = "#shopId")
    public void orderForm(Long shopId, OrderForm orderForm) {
        orderForm.setShopId(shopId);
        boolean exists = orderFormService.lambdaQuery()
                .eq(OrderForm::getShopId, shopId)
                .exists();
        String hKey = shopId.toString();
        //保存数据 缓存双删
        RedisUtil.doubleDeletion(
                () -> {
                    boolean success = exists ?
                            orderFormService.lambdaUpdate()
                                    .set(OrderForm::getOrderNotify, orderForm.getOrderNotify())
                                    .set(OrderForm::getCustomForm, JSON.toJSONString(orderForm.getCustomForm()))
                                    .eq(OrderForm::getShopId, shopId)
                                    .update()
                            : orderFormService.save(orderForm);
                    SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
                },
                () -> {
                    Lazy.ORDER_FORM_CACHE.invalidate(shopId);
                    RedisUtil.delCacheMapValue(OrderConstant.ORDER_FORM_CACHE, hKey);
                }
        );

    }

    @Override
    public Map<Long, List<FormInput>> orderFormsInput(Set<Long> shopIds) {
        return orderForms(shopIds)
                .values()
                .stream()
                .collect(Collectors.toMap(OrderForm::getShopId, orderForm -> CollUtil.emptyIfNull(orderForm.getCustomForm())));
    }

    @Override
    @SuppressWarnings("all")
    public Map<Long, OrderForm> orderForms(Set<Long> shopIds) {
        //本地内存缓存中取数据
        Map<Long, OrderForm> orderForms = new HashMap<>(Lazy.ORDER_FORM_CACHE.getAllPresent(shopIds));
        shopIds = shopIds.stream().filter(shopId -> !orderForms.containsKey(shopId)).collect(Collectors.toSet());
        if (shopIds.isEmpty()) {
            return orderForms;
        }
        //转string
        Set<String> shopIdsStr = shopIds.stream().map(shopId -> Long.toString(shopId)).collect(Collectors.toSet());
        //redis中查询
        Map<Long, OrderForm> redisFormMap = RedisUtil.getMultiCacheMapValue(OrderConstant.ORDER_FORM_CACHE, shopIdsStr, OrderForm.class)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(OrderForm::getShopId, v -> v));
        //不为空放入本地缓存
        if (CollUtil.isNotEmpty(redisFormMap)) {
            Lazy.ORDER_FORM_CACHE.putAll(redisFormMap);
        }
        orderForms.putAll(redisFormMap);
        shopIds = shopIds.stream().filter(shopId -> !orderForms.containsKey(shopId)).collect(Collectors.toSet());
        if (shopIds.isEmpty()) {
            return orderForms;
        }
        //数据库中查询
        List<OrderForm> shopForms = orderFormService.lambdaQuery()
                .select(OrderForm::getId, OrderForm::getShopId, OrderForm::getOrderNotify, OrderForm::getCustomForm)
                .in(OrderForm::getShopId, shopIds)
                .list();
        Map<Long, OrderForm> dbFormMap = shopForms.stream().collect(Collectors.toMap(OrderForm::getShopId, v -> v));
        shopIds.stream()
                .filter(shopId -> !dbFormMap.containsKey(shopId))
                .forEach(shopId -> dbFormMap.put(shopId, new OrderForm().setShopId(shopId).setOrderNotify(Boolean.TRUE).setCustomForm(Collections.emptyList())));
        //存放至redis缓存 和本地缓存中
        RedisUtil.executePipelined(
                redisOperations -> {
                    Lazy.ORDER_FORM_CACHE.putAll(dbFormMap);
                    dbFormMap.forEach((shopId, orderForm) -> redisOperations.opsForHash().putIfAbsent(OrderConstant.ORDER_FORM_CACHE, shopId.toString(), orderForm));
                }
        );
        orderForms.putAll(dbFormMap);
        return orderForms;
    }


    @Override
    public OrderForm orderForm(Long shopId) {
        return orderForms(Set.of(shopId)).get(shopId);
    }

    /**
     * 懒加载缓存
     */
    static class Lazy {
        /**
         * 本地缓存
         * 1. 缓存过期时间 15小时
         * 2. 缓存最大容量 100
         */
        static final Cache<Long, OrderForm> ORDER_FORM_CACHE = CacheBuilder.newBuilder()
                .expireAfterAccess(CommonPool.NUMBER_FIFTEEN, TimeUnit.HOURS)
                .maximumSize(CommonPool.UNIT_CONVERSION_HUNDRED)
                .build();
    }


}
