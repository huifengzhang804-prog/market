package com.medusa.gruul.addon.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.team.model.constant.TeamConst;
import com.medusa.gruul.addon.team.model.dto.TeamSkuDTO;
import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import com.medusa.gruul.addon.team.model.enums.TeamStatus;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import com.medusa.gruul.addon.team.mp.entity.TeamProduct;
import com.medusa.gruul.addon.team.mp.service.ITeamActivityService;
import com.medusa.gruul.addon.team.mp.service.ITeamOrderService;
import com.medusa.gruul.addon.team.mp.service.impl.TeamProductServiceImpl;
import com.medusa.gruul.addon.team.service.TeamService;
import com.medusa.gruul.addon.team.util.TeamUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/3/16
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final OrderRpcService orderRpcService;
    private final ITeamOrderService teamOrderService;
    private final PaymentRpcService paymentRpcService;
    private final ITeamActivityService teamActivityService;
    private final TeamProductServiceImpl teamProductService;
    private final StorageActivityRpcService storageActivityRpcService;

    @Override
    @Redisson(name = TeamConst.TEAM_ACTIVITY_OPEN_LOCK_KEY, key = "#teamKey.shopId+':'+#teamKey.activityId")
    public void openTeamActivity(TeamKey teamKey) {
        LocalDateTime now = LocalDateTime.now();
        Long shopId = teamKey.getShopId();
        Long activityId = teamKey.getActivityId();
        TeamActivity activity = teamActivityService.lambdaQuery()
                //活动未删除 且状态正确
                .eq(TeamActivity::getStatus, TeamStatus.OK)
                .eq(TeamActivity::getShopId, shopId)
                .eq(TeamActivity::getId, activityId)
                .ge(TeamActivity::getEndTime, now)
                .one();
        if (activity == null) {
            return;
        }
        List<TeamProduct> products = teamProductService.lambdaQuery()
                .eq(TeamProduct::getShopId, shopId)
                .eq(TeamProduct::getActivityId, activityId)
                .list();
        if (CollUtil.isEmpty(products)) {
            return;
        }
        this.openTeamActivity(activity, products);
    }

    @Override
    public void openTeamActivity(TeamActivity activity, List<TeamProduct> products) {
        Long activityId = activity.getId();
        Long shopId = activity.getShopId();
        LocalDateTime now = LocalDateTime.now();
        Map<String, List<TeamProduct>> keyProductMap = products.stream()
                .collect(
                        Collectors.groupingBy(
                                product -> TeamUtil.getTeamProductCacheKey(shopId, product.getProductId())
                        )
                );
        Map<String, TeamProductVO> productMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        keyProductMap.forEach(
                (key, value) -> productMap.put(
                        key,
                        new TeamProductVO()
                                .setActivityId(activityId)
                                .setEffectTimeout(activity.getEffectTimeout())
                                .setMode(activity.getMode())
                                .setUsers(activity.getUsers())
//                                .setTeamStatus()
                                .setHuddle(activity.getHuddle())
                                .setStartTime(activity.getStartTime())
                                .setEndTime(activity.getEndTime())
                                .setStackable(activity.getStackable())
                                .setSkus(
                                        value.stream()
                                                .map(
                                                        sku -> new TeamSkuDTO()
                                                                .setSkuId(sku.getSkuId())
                                                                .setStock(sku.getStock())
                                                                .setPrices(sku.getPrices())
                                                ).collect(Collectors.toList())
                                )
                )
        );
        this.productActivityCache(now, productMap);
    }

    /**
     * 批量操作商品活动缓存
     * 1. 批量操作
     * 2. 移除过期数据
     * 3. 添加新数据
     *
     * @param now        当前时间
     * @param productMap 商品活动数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void productActivityCache(LocalDateTime now, Map<String, TeamProductVO> productMap) {
        Set<String> productKeys = productMap.keySet();
        Map<String, Long> keyExpireSeconds = getKeyExpireSeconds(new ArrayList<>(productKeys));
        //当前时间的得分
        double nowTimeScore = TeamUtil.getScoreByTime(now);
        RedisUtil.executePipelined(
                redisOperations -> {
                    //小于当时间前分值的数据都是过期数据 直接删除
                    ZSetOperations zSetOperations = redisOperations.opsForZSet();
                    productKeys.forEach(
                            productKey -> {
                                zSetOperations.removeRangeByScore(productKey, 0, nowTimeScore);
                                TeamProductVO teamProduct = productMap.get(productKey);
                                zSetOperations.add(productKey, teamProduct,
                                        TeamUtil.getScoreByTime(teamProduct.getEndTime()));
                                //剩下的有效时间
                                Long expireSeconds =
                                        LocalDateTimeUtil.between(now, teamProduct.getEndTime()).toSeconds();
                                expireSeconds = expireSeconds > keyExpireSeconds.get(productKey) ? expireSeconds :
                                        keyExpireSeconds.get(productKey);
                                redisOperations.expire(productKey, expireSeconds, TimeUnit.SECONDS);
                            }
                    );
                }
        );
    }


    /**
     * 查询剩下的有效时间
     *
     * @param keys key集合
     * @return key对应的有效时间
     */

    @SuppressWarnings({"unchecked"})
    private Map<String, Long> getKeyExpireSeconds(List<String> keys) {
        Map<String, Long> keyExpireSecondsMap = new HashMap<>();
        List<Object> seconds = RedisUtil.executePipelined(redisOperations -> keys.forEach(redisOperations::getExpire));
        for (int i = 0; i < keys.size(); i++) {
            Object second = seconds.get(i);
            keyExpireSecondsMap.put(keys.get(i), second == null ? 0L : ((Long) second));
        }
        return keyExpireSecondsMap;
    }

    @Override
    public void closeTeamActivity(TeamKey teamKey) {
        TeamActivity activity = teamActivityService.lambdaQuery()
                .notIn(TeamActivity::getStatus, Lists.newArrayList(TeamStatus.SHOP_OFF_SHELF, TeamStatus.VIOLATION))
                .eq(TeamActivity::getShopId, teamKey.getShopId())
                .eq(TeamActivity::getId, teamKey.getActivityId())
                .one();
        if (activity == null) {
            return;
        }
        this.closeTeamActivity(activity);
    }

    @Override
    public void closeTeamActivity(TeamActivity teamActivity) {
        Long shopId = teamActivity.getShopId();
        Long activityId = teamActivity.getId();
        List<TeamProduct> products = teamProductService.lambdaQuery()
                .eq(TeamProduct::getShopId, shopId)
                .eq(TeamProduct::getActivityId, activityId)
                .list();
        if (products == null) {
            return;
        }
        this.removeProductsActivityCache(
                Map.of(
                        TeamUtil.getScoreByTime(teamActivity.getEndTime()),
                        products.stream()
                                .map(product -> TeamUtil.getTeamProductCacheKey(shopId, product.getProductId()))
                                .collect(Collectors.toSet())
                )
        );
        storageActivityRpcService.activityClose(
                Collections.singleton(
                        new ActivityCloseKey()
                                .setShopId(shopId)
                                .setActivityType(OrderType.TEAM)
                                .setActivityId(activityId)
                )
        );
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void removeProductsActivityCache(Map<Long, Set<String>> scoreProductKeysMap) {
        RedisUtil.executePipelined(
                redisOperations -> {
                    ZSetOperations zSetOperations = redisOperations.opsForZSet();
                    scoreProductKeysMap.forEach(
                            (score, productKeys) -> productKeys.forEach(
                                    productKey -> zSetOperations.removeRangeByScore(productKey, score, score)
                            )
                    );
                }
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = TeamConst.TEAM_LOCK_KEY, key = "#teamNo")
    public void closeTeamOrder(String teamNo) {
        //查询拼团订单
        List<TeamOrder> teamOrders = teamOrderService.lambdaQuery()
                .eq(TeamOrder::getTeamNo, teamNo)
                .eq(TeamOrder::getStatus, TeamOrderStatus.ING)
                .list();
        if (CollUtil.isEmpty(teamOrders)) {
            return;
        }
        //查询是否开启了模拟成团
        TeamOrder order = teamOrders.get(0);
        TeamActivity activity = teamActivityService.lambdaQuery()
                .eq(TeamActivity::getShopId, order.getShopId())
                .eq(TeamActivity::getId, order.getActivityId())
                .one();
        //开启了模拟成团
        if (BooleanUtil.isTrue(activity.getSimulate())) {
            this.simulateTeam(teamNo, teamOrders);
            return;
        }
        teamOrderService.lambdaUpdate()
                .eq(TeamOrder::getTeamNo, teamNo)
                .eq(TeamOrder::getStatus, TeamOrderStatus.ING)
                .set(TeamOrder::getStatus, TeamOrderStatus.FAIL)
                .update();
        //退款
        teamOrders.forEach(teamOrder -> {
            RefundRequestDTO refundRequest = new RefundRequestDTO();
            refundRequest.setOrderNum(teamOrder.getOrderNo());
            refundRequest.setShopId(teamOrder.getShopId());
            refundRequest.setAfsNum(teamOrder.getTeamNo());
            refundRequest.setRefundFee(teamOrder.getAmount());
            paymentRpcService.refundRequest(refundRequest);
        });

        //批量关闭订单
        orderRpcService.updateOrderStatus(
                teamOrders.stream().map(TeamOrder::getOrderNo).collect(Collectors.toSet()),
                OrderStatus.TEAMING,
                OrderStatus.TEAM_FAIL
        );
    }

    private void simulateTeam(String teamNo, List<TeamOrder> teamOrders) {
        //修改拼团订单状态 为成功
        teamOrderService.lambdaUpdate()
                .eq(TeamOrder::getTeamNo, teamNo)
                .eq(TeamOrder::getStatus, TeamOrderStatus.ING)
                .set(TeamOrder::getStatus, TeamOrderStatus.SUCCESS)
                .update();

        //修改订单状态 为已付款
        orderRpcService.updateOrderStatus(
                teamOrders.stream().map(TeamOrder::getOrderNo).collect(Collectors.toSet()),
                OrderStatus.TEAMING,
                OrderStatus.PAID
        );
    }
}
