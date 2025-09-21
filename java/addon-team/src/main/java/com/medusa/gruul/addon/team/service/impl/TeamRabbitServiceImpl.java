package com.medusa.gruul.addon.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.addon.team.model.OrderTeamCache;
import com.medusa.gruul.addon.team.model.constant.TeamConst;
import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import com.medusa.gruul.addon.team.mp.service.ITeamActivityService;
import com.medusa.gruul.addon.team.mp.service.ITeamOrderService;
import com.medusa.gruul.addon.team.service.TeamRabbitService;
import com.medusa.gruul.addon.team.util.TeamUtil;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.xxl.job.CronHelper;
import com.medusa.gruul.common.xxl.job.model.ScheduleTypeEnum;
import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/3/16
 */
@Service
@RequiredArgsConstructor
public class TeamRabbitServiceImpl implements TeamRabbitService {

    private final JobService jobService;
    private final OrderRpcService orderRpcService;
    private TeamRabbitService teamRabbitService;
    private final RedissonClient redissonClient;
    private final ITeamOrderService teamOrderService;
    private final ITeamActivityService teamActivityService;


    @Autowired
    public void setTeamRabbitService(TeamRabbitService teamRabbitService) {
        this.teamRabbitService = teamRabbitService;
    }

    @Override
    public void orderPaid(OrderPaidBroadcastDTO orderPaid) {
        if (OrderType.TEAM != orderPaid.getActivityType()) {
            return;
        }
        String orderNo = orderPaid.getOrderNo();
        String key = RedisUtil.key(TeamConst.ORDER_TEAM_CACHE_KEY, orderNo);
        //不存在说明 不是拼团订单 或者 拼团订单已经处理过了 或者 拼团订单已经过期
        if (!BooleanUtil.isTrue(RedisUtil.getRedisTemplate().hasKey(key))) {
            return;
        }
        RLock lock = redissonClient.getLock(key + "lock");
        lock.lock();
        try {
            OrderTeamCache cache = RedisUtil.getCacheMap(key, OrderTeamCache.class);
            if (cache == null) {
                return;
            }
            this.teamHandler(orderPaid, cache);
            RedisUtil.delete(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加入正在拼团的老团
     *
     * @param teamOrder         当前订单
     * @param countAndCommander 当前团订单统计、团长订单、与订单列表
     * @param skuKey            商品sku key
     * @return 订单号集合
     */
    private Set<String> joinTeaming(TeamOrder teamOrder, Tuple3<Integer, TeamOrder, List<TeamOrder>> countAndCommander, ShopProductSkuKey skuKey) {
        Integer count = countAndCommander._1();
        TeamOrder commanderOrder = countAndCommander._2();
        TeamOrderStatus commanderOrderStatus = commanderOrder.getStatus();
        //老团
        teamOrder.setNum(commanderOrder.getNum())
                .setCommander(Boolean.FALSE)
                .setStatus(commanderOrderStatus)
                .setOpenTime(commanderOrder.getOpenTime());
        teamOrderService.save(teamOrder);
        //是否大于等于拼团人数
        boolean isEnough = count + 1 >= commanderOrder.getNum();
        if (isEnough) {
            teamOrderService.lambdaUpdate()
                    .set(TeamOrder::getStatus, TeamOrderStatus.SUCCESS)
                    .eq(TeamOrder::getShopId, skuKey.getShopId())
                    .eq(TeamOrder::getProductId, skuKey.getProductId())
                    .eq(TeamOrder::getActivityId, teamOrder.getActivityId())
                    .eq(TeamOrder::getTeamNo, teamOrder.getTeamNo())
                    .update();
        }
        Set<String> orderNos = new HashSet<>();
        if (!isEnough) {
            return orderNos;
        }
        orderNos.add(teamOrder.getOrderNo());
        if (TeamOrderStatus.ING == commanderOrderStatus) {
            orderNos.addAll(countAndCommander._3().stream().map(TeamOrder::getOrderNo).collect(Collectors.toSet()));
        }
        return orderNos;
    }

    /**
     * 拼团处理
     *
     * @param oldTeamFailed 老团是否拼团失败
     * @param isOverTime    是否超过活动时间
     * @param userNum       拼团人数
     * @param teamOrder     当前订单
     * @param skuKey        商品sku key
     * @return 订单号集合
     */
    private Set<String> newTeam(boolean oldTeamFailed, boolean isOverTime, int userNum, TeamOrder teamOrder, ShopProductSkuKey skuKey) {
        //老团拼团失败则 删除老团订单数据
        if (oldTeamFailed) {
            teamOrderService.lambdaUpdate()
                    .eq(TeamOrder::getShopId, skuKey.getShopId())
                    .eq(TeamOrder::getProductId, skuKey.getProductId())
                    .eq(TeamOrder::getActivityId, teamOrder.getActivityId())
                    .eq(TeamOrder::getTeamNo, teamOrder.getTeamNo())
                    .remove();
        }
        //保存并声称新团数据
        teamOrder.setNum(isOverTime ? CommonPool.NUMBER_ONE : userNum)
                .setCommander(Boolean.TRUE)
                .setStatus(isOverTime ? TeamOrderStatus.SUCCESS : TeamOrderStatus.ING)
                .setOpenTime(LocalDateTime.now());
        teamOrderService.save(teamOrder);
        Set<String> orderNos = new HashSet<>();
        //未超时 则为新的处于拼团状态的订单 创建定时任务 用于拼团超时处理
        if (!isOverTime) {
            jobService.add(
                    new XxlJobInfo()
                            .setJobDesc("拼团订单超时检查")
                            .setAuthor("system")
                            .setScheduleType(ScheduleTypeEnum.CRON.name())
                            .setScheduleConf(CronHelper.toCron(Duration.ofMinutes(teamOrder.getEffectTimeout())))
                            .setExecutorHandler(TeamConst.TEAM_ORDER_CLOSE_JOB_HANDLER)
                            .setExecutorParam(teamOrder.getTeamNo())
            );
            return orderNos;
        }
        //超过拼团时间则直接拼团成功 更新拼团订单
        orderNos.add(teamOrder.getOrderNo());
        return orderNos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinTeam(boolean isOverTime, int userNum, TeamOrder teamOrder, ShopProductSkuKey skuKey) {
        String teamNo = teamOrder.getTeamNo();
        Long activityId = teamOrder.getActivityId();
        Tuple3<Integer, TeamOrder, List<TeamOrder>> countAndCommander = getCountAndCommander(teamNo, activityId, skuKey);
        int count = countAndCommander._1();
        TeamOrder commanderOrder = countAndCommander._2();
        //是否新团
        boolean isNew = count == 0;
        //老团是否已拼团失败  1.不是新团 2.老团状态已失败
        boolean isFailed = !isNew && TeamOrderStatus.FAIL == commanderOrder.getStatus();
        //不是新团 且 老团拼团未失败 则 直接加入老团
        //否则 建新团
        Set<String> orderNos = (!isNew && TeamOrderStatus.FAIL != commanderOrder.getStatus()) ?
                this.joinTeaming(teamOrder, countAndCommander, skuKey) :
                this.newTeam(isFailed, isOverTime, userNum, teamOrder, skuKey);
        IManualTransaction.afterCommit(
                () -> RedisUtil.getRedisTemplate().opsForValue().setIfAbsent(
                        RedisUtil.key(TeamConst.USER_TEAM_SUCCESS_CACHE_KEY, teamNo, teamOrder.getUserId()),
                        1,
                        Duration.ofMinutes(CommonPool.NUMBER_FIVE)
                )
        );
        //rpc更新订单状态
        if (CollUtil.isEmpty(orderNos)) {
            return;
        }
        orderRpcService.updateOrderStatus(orderNos, OrderStatus.TEAMING, OrderStatus.PAID);
    }


    /**
     * 处理拼团订单数据
     * 1。退款 2 生成新的拼团订单
     *
     * @param orderPaid 订单支付成功消息
     * @param cache     缓存的订单信息
     */
    private void teamHandler(OrderPaidBroadcastDTO orderPaid, OrderTeamCache cache) {
        Long activityId = cache.getActivityId();
        TeamActivity activity = teamActivityService.lambdaQuery()
                .eq(TeamActivity::getId, activityId)
                .one();
        if (activity == null) {
            return;
        }
        ShopProductSkuKey skuKey = cache.getSkuKey();
        String teamNo = cache.getTeamNo();
        TeamOrder teamOrder = new TeamOrder()
                .setTeamNo(teamNo)
                .setShopId(skuKey.getShopId())
                .setActivityId(activityId)
                .setEffectTimeout(activity.getEffectTimeout())
                .setOrderNo(orderPaid.getOrderNo())
                .setProductId(skuKey.getProductId())
                .setProductNum(cache.getProductNum())
                .setPrice(cache.getSkuPrice())
                .setAmount(orderPaid.getPayAmount())
                .setUserId(orderPaid.getBuyerId())
                .setNickname(orderPaid.getBuyerNickname())
                .setAvatar(orderPaid.getBuyerAvatar());

        //拼团锁
        TeamUtil.lockTeam(
                redissonClient, teamNo,
                () -> teamRabbitService.joinTeam(LocalDateTime.now().isAfter(activity.getEndTime()), cache.getUserNum(), teamOrder, skuKey)
        );
    }

    private Tuple3<Integer, TeamOrder, List<TeamOrder>> getCountAndCommander(String teamNo, Long activityId, ShopProductSkuKey skuKey) {
        List<TeamOrder> orders = teamOrderService.lambdaQuery()
                .eq(TeamOrder::getShopId, skuKey.getShopId())
                .eq(TeamOrder::getProductId, skuKey.getProductId())
                .eq(TeamOrder::getActivityId, activityId)
                .eq(TeamOrder::getTeamNo, teamNo)
                .list();
        int count = CollUtil.isEmpty(orders) ? 0 : orders.size();
        return Tuple.of(
                count,
                orders.stream().filter(TeamOrder::getCommander).findFirst().orElse(null),
                orders
        );

    }


}
