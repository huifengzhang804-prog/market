package com.medusa.gruul.addon.full.reduction.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionProduct;
import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionRule;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionDTO;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionPageDTO;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionShelfDTO;
import com.medusa.gruul.addon.full.reduction.model.enums.FullQueryStatus;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionError;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionJobType;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionOrderStatus;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionStatus;
import com.medusa.gruul.addon.full.reduction.model.vo.FullReductionDetailVO;
import com.medusa.gruul.addon.full.reduction.model.vo.FullReductionVO;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionDao;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionOrderDao;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReduction;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReductionOrder;
import com.medusa.gruul.addon.full.reduction.service.FullReductionService;
import com.medusa.gruul.addon.full.reduction.util.FullReductionUtil;
import com.medusa.gruul.common.model.base.CurrentActivityKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.medusa.gruul.global.model.o.KeyValue;
import com.medusa.gruul.global.model.o.RangeDateTime;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.Tuple;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张治保
 * @since 2024/6/15
 */

@Service
@RequiredArgsConstructor
public class FullReductionServiceImpl implements FullReductionService {

    private final JobService jobService;
    private final FullReductionDao fullReductionDao;
    private final ShopRpcService shopRpcService;
    private final GoodsRpcService goodsRpcService;
    private final FullReductionOrderDao fullReductionOrderDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long shopId, FullReductionDTO param) {
        Long id = param.getId();
        boolean isUpdate = id != null;
        FullReduction.JobId jobId = new FullReduction.JobId();
        LocalDateTime preStartTime = null;
        if (isUpdate) {
            FullReduction one = fullReductionDao.lambdaQuery()
                    .select(FullReduction::getStartTime, FullReduction::getStatus, FullReduction::getExtra)
                    .eq(FullReduction::getShopId, shopId)
                    .eq(FullReduction::getId, id)
                    .one();
            if (one == null) {
                throw SystemCode.DATA_NOT_EXIST.exception();
            }
            if (FullReductionStatus.OK != one.getStatus()) {
                throw FullReductionError.CANNOT_EDIT.exception();
            }
            jobId = one.getExtra().getJobId();
            preStartTime = one.getStartTime();
        }
        id = id == null ? MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(FullReduction.class).longValue() : id;
        //活动时间
        RangeDateTime time = param.getTime();
        LocalDateTime startTime = time.getStart();
        //是否需要立即开始活动 最多可提前2秒钟
        boolean startIdNow = LocalDateTime.now().plusSeconds(CommonPool.NUMBER_TWO).isAfter(startTime);
        //开始活动的定时任务 id
        Integer startId = jobId.getStartId();
        //删除之前的定时任务
        //todo 需要考虑 定时任务的移除时机，现在没有保证事务性
        if (startId != null) {
            jobService.remove(startId);
            startId = null;
        }
        String executeParam = JSON.toJSONString(
                new CurrentActivityKey().setShopId(shopId).setActivityId(id)
        );
        //如果不需要现在缓存则添加定时任务触发开始活动
        if (!startIdNow) {
            //如果 当前时间距碍事时间小于 30 秒则提示错误 需要给定时任务和程序留足执行时机
            if (Duration.between(LocalDateTime.now(), startTime).toSeconds() < CommonPool.NUMBER_THIRTY) {
                throw FullReductionError.START_TIME_NOT_IN_RANGE.exception();
            }
            startId = jobService.add(FullReductionJobType.START.toXxlJobInfo(startTime, executeParam));
        }
        //定时任务关闭活动
        int stopId = jobService.add(FullReductionJobType.STOP.toXxlJobInfo(time.getEnd(), executeParam));
        //渲染活动商品信息
        Map<Long, FullReductionProduct> products = MapUtil.newHashMap();
        if (param.getProductType().getIsAssigned()) {
            Map<ShopProductKey, Product> productMap = goodsRpcService.getProductBatch(
                    param.getProductIds()
                            .stream()
                            .map(productId -> new ShopProductKey(shopId, productId))
                            .collect(Collectors.toSet())
            );
            for (Long productId : param.getProductIds()) {
                Product product = productMap.get(new ShopProductKey(shopId, productId));
                if (product == null) {
                    throw SystemCode.DATA_NOT_EXIST.exception();
                }
                products.put(
                        productId,
                        new FullReductionProduct()
                                .setId(productId)
                                .setName(product.getName())
                                .setImage(product.getPic())
                );
            }
        }
        //渲染满减活动信息
        FullReduction reduction = new FullReduction()
                .setShopId(shopId)
                .setName(param.getName())
                .setStatus(FullReductionStatus.OK)
                .setStartTime(startTime)
                .setEndTime(time.getEnd())
                .setRules(param.getRules())
                .setProductType(param.getProductType())
                .setProducts(products)
                .setExtra(
                        new FullReduction
                                .Extra()
                                .setJobId(
                                        new FullReduction.JobId()
                                                .setStartId(startId)
                                                .setStopId(stopId)
                                )
                );
        reduction.setId(id);
        if (isUpdate) {
            fullReductionDao.lambdaUpdate()
                    .set(FullReduction::getName, reduction.getName())
                    .set(FullReduction::getStartTime, reduction.getStartTime())
                    .set(FullReduction::getEndTime, reduction.getEndTime())
                    .set(FullReduction::getRules, JSON.toJSONString(reduction.getRules()))
                    .set(FullReduction::getProductType, reduction.getProductType())
                    .set(FullReduction::getProducts, JSON.toJSONString(KeyValue.of(products)))
                    .set(FullReduction::getExtra, JSON.toJSONString(reduction.getExtra()))
                    .eq(FullReduction::getShopId, shopId)
                    .eq(FullReduction::getId, id)
                    .update();
        } else {
            //保存 更新数据
            fullReductionDao.save(reduction);
        }
        if (!startIdNow) {
            //如果当前不是开始状态 且之前的已经开始 则删除之前的缓存
            if (preStartTime != null && preStartTime.isBefore(LocalDateTime.now())) {
                FullReductionUtil.deleteActivity(shopId, id);
            }
            return;
        }
        //如果需要立即开始活动 缓存活动
        FullReductionUtil.cacheActivity(
                shopId,
                reduction.getId(),
                reduction.getRules(),
                reduction.getProductType(),
                reduction.getProducts().keySet(),
                Duration.between(LocalDateTime.now(), time.getEnd())
        );
    }

    @Override
    public IPage<FullReductionVO> page(FullReductionPageDTO param) {
        Long shopId = param.getShopId();
        boolean isShop = shopId != null;
        LambdaQueryChainWrapper<FullReduction> queryWrapper = fullReductionDao.lambdaQuery()
                .select(
                        FullReduction::getShopId,
                        FullReduction::getId,
                        FullReduction::getName,
                        FullReduction::getStartTime,
                        FullReduction::getEndTime,
                        FullReduction::getRules,
                        FullReduction::getProductType,
                        FullReduction::getProducts,
                        FullReduction::getStatus,
                        FullReduction::getExtra
                )
                .eq(isShop, FullReduction::getShopId, shopId)
                .like(StrUtil.isNotBlank(param.getName()), FullReduction::getName,
                        StrUtil.emptyIfNull(param.getName()).trim());
        FullQueryStatus status = param.getStatus();
        if (status != null) {
            LocalDateTime now = LocalDateTime.now();
            switch (status) {
                case NOT_STARTED -> queryWrapper.gt(FullReduction::getStartTime, now)
                        .notIn(FullReduction::getStatus,
                                Lists.newArrayList(FullReductionStatus.OFF_SHELF,
                                        FullReductionStatus.VIOLATION_OFF_SHELF));
                case IN_PROGRESS -> queryWrapper.le(FullReduction::getStartTime, now).ge(FullReduction::getEndTime, now)
                        .notIn(FullReduction::getStatus,
                                Lists.newArrayList(FullReductionStatus.OFF_SHELF,
                                        FullReductionStatus.VIOLATION_OFF_SHELF));
                case FINISHED -> queryWrapper.lt(FullReduction::getEndTime, now).notIn(FullReduction::getStatus,
                        Lists.newArrayList(FullReductionStatus.OFF_SHELF,
                                FullReductionStatus.VIOLATION_OFF_SHELF));
                case OFF_SHELF -> queryWrapper.eq(FullReduction::getStatus, FullReductionStatus.OFF_SHELF);
                case VIOLATION_OFF_SHELF ->
                        queryWrapper.eq(FullReduction::getStatus, FullReductionStatus.VIOLATION_OFF_SHELF);
            }
        }
        IPage<FullReduction> page = queryWrapper
                .orderByDesc(FullReduction::getCreateTime)
                .page(param);
        //
        List<FullReduction> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page.convert(record -> null);
        }
        return isShop ? shopQuery(shopId, page) : platformQuery(page);
    }

    @Override
    public FullReductionDetailVO detail(CurrentActivityKey key) {
        FullReduction reduction = fullReductionDao.lambdaQuery()
                .eq(FullReduction::getShopId, key.getShopId())
                .eq(FullReduction::getId, key.getActivityId())
                .one();
        if (reduction == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        return new FullReductionDetailVO()
                .setShopId(reduction.getShopId())
                .setId(reduction.getId())
                .setName(reduction.getName())
                .setTime(new RangeDateTime().setStart(reduction.getStartTime()).setEnd(reduction.getEndTime()))
                .setRules(reduction.getRules())
                .setProductType(reduction.getProductType())
                .setProducts(List.copyOf(reduction.getProducts().values()));
    }

    @Override
    public void batchDelete(Long shopId, Set<Long> activityIds) {
        Set<String> cacheKeys = activityIds.stream()
                .map(id -> FullReductionUtil.activityKey(shopId, id))
                .collect(Collectors.toSet());
        RedisUtil.doubleDeletion(
                () -> fullReductionDao.lambdaUpdate()
                        .eq(FullReduction::getShopId, shopId)
                        .in(FullReduction::getId, activityIds)
                        .remove(),
                () -> RedisUtil.delete(cacheKeys)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shelf(boolean isShop, FullReductionShelfDTO param) {
        Long shopId = param.getShopId();
        Long activityId = param.getId();
        FullReduction one = fullReductionDao.lambdaQuery()
                .select(FullReduction::getStatus, FullReduction::getExtra)
                .eq(FullReduction::getShopId, shopId)
                .eq(FullReduction::getId, activityId)
                .one();
        if (one == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (FullReductionStatus.OK != one.getStatus()) {
            throw FullReductionError.CANNOT_SHELF.exception();
        }
        RedisUtil.doubleDeletion(
                () -> {
                    fullReductionDao.lambdaUpdate()
                            .set(FullReduction::getStatus,
                                    isShop ? FullReductionStatus.OFF_SHELF : FullReductionStatus.VIOLATION_OFF_SHELF)
                            .setSql(
                                    !isShop,
                                    SqlHelper.renderJsonSetSql(
                                            "extra",
                                            Tuple.of("violation", param.getViolation())
                                    )
                            )
                            .eq(FullReduction::getShopId, shopId)
                            .eq(FullReduction::getId, activityId)
                            .update();
                    FullReduction.JobId jobId = one.getExtra().getJobId();
                    if (jobId == null) {
                        return;
                    }
                    //删除定时任务
                    Integer startId = jobId.getStartId();
                    if (startId != null) {
                        jobService.remove(startId);
                    }
                    Integer stopId = jobId.getStopId();
                    if (stopId != null) {
                        jobService.remove(stopId);
                    }
                },
                FullReductionUtil.activityKey(shopId, activityId)
        );


    }

    @Override
    public Map<Long, List<String>> shopRuleLabels(Set<Long> shopIds) {
        LocalDateTime now = LocalDateTime.now();
        long shopLimitSize = CommonPool.NUMBER_FOUR;
        return fullReductionDao.lambdaQuery()
                .select(FullReduction::getShopId, FullReduction::getRules)
                .in(FullReduction::getShopId, shopIds)
                //状态正常
                .eq(FullReduction::getStatus, FullReductionStatus.OK)
                //进行中
                .le(FullReduction::getStartTime, now)
                .gt(FullReduction::getEndTime, now)
                .list()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                FullReduction::getShopId,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .map(FullReduction::getRules)
                                                .flatMap(List::stream)
                                                .limit(shopLimitSize)
                                                .map(FullReductionRule::getDesc)
                                                .toList()
                                )
                        )
                );
    }

    /**
     * 渲染店铺的查询数据
     *
     * @param shopId 店铺id
     * @param page   分页数据
     * @return IPage<FullReductionVO> 渲染数据后的分页结果
     */
    private IPage<FullReductionVO> shopQuery(Long shopId, IPage<FullReduction> page) {
        List<FullReduction> records = page.getRecords();
        Set<Long> activityIds = records.stream()
                .map(FullReduction::getId)
                .collect(Collectors.toSet());
        //user id 作为参与用户数量，order 作为支付订单数
        List<FullReductionOrder> orders = fullReductionOrderDao.query()
                .select(
                        "activity_id",
                        "COUNT(DISTINCT user_id) as user",
                        "SUM(IF(`status` = " + FullReductionOrderStatus.PAID.getValue() + ",1,0)) as `order`"
                )
                .eq("shop_id", shopId)
                .in("activity_id", activityIds)
                .groupBy("activity_id")
                .list();
        Map<Long, Long> orderMap = orders.stream()
                .collect(Collectors.toMap(
                        FullReductionOrder::getActivityId,
                        FullReductionOrder::getOrder
                ));
        return page.convert(
                record -> toVo(record)
                        .setOrder(orderMap.getOrDefault(record.getId(), (long) CommonPool.NUMBER_ZERO))
        );
    }

    private IPage<FullReductionVO> platformQuery(IPage<FullReduction> page) {
        List<FullReduction> records = page.getRecords();
        Map<Long, String> shopNameMap = shopRpcService.getShopInfoByShopIdList(
                        records.stream().map(FullReduction::getShopId).collect(Collectors.toSet()))
                .stream()
                .collect(
                        Collectors.toMap(
                                ShopInfoVO::getId,
                                ShopInfoVO::getName
                        )
                );
        Map<CurrentActivityKey, Long> orderMap = fullReductionOrderDao.query()
                .select(
                        "shop_id",
                        "activity_id",
                        "SUM(IF(`status` = " + FullReductionOrderStatus.PAID.getValue() + ",1,0)) as `order`"
                )
                .inSql(
                        new StringJoiner(StrPool.COMMA, "(", ")")
                                .add("shop_id")
                                .add("activity_id")
                                .toString(),
                        page.getRecords()
                                .stream()
                                .map(key -> "(%s,%s)".formatted(key.getShopId(), key.getId()))
                                .collect(Collectors.joining(","))
                )
                .groupBy("shop_id", "activity_id")
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                order -> new CurrentActivityKey().setShopId(order.getShopId())
                                        .setActivityId(order.getActivityId()),
                                FullReductionOrder::getOrder
                        )
                );
        return page.convert(
                record -> {
                    String shopName = shopNameMap.get(record.getShopId());
                    CurrentActivityKey key = new CurrentActivityKey().setShopId(record.getShopId())
                            .setActivityId(record.getId());
                    return toVo(record)
                            .setShopName(shopName)
                            .setOrder(orderMap.getOrDefault(key, (long) CommonPool.NUMBER_ZERO));
                }
        );

    }


    /**
     * 渲染单挑查询数据
     *
     * @param record 分页单挑数据
     * @return FullReductionVO 渲染数据后的单挑结果
     */
    private FullReductionVO toVo(FullReduction record) {
        LocalDateTime startTime = record.getStartTime();
        LocalDateTime endTime = record.getEndTime();
        List<FullReductionRule> rules = record.getRules();
        FullReductionRule firstRule = rules.get(CommonPool.NUMBER_ZERO);
        return new FullReductionVO()
                .setShopId(record.getShopId())
                .setId(record.getId())
                .setStatus(
                        switch (record.getStatus()) {
                            case OFF_SHELF -> FullQueryStatus.OFF_SHELF;
                            case VIOLATION_OFF_SHELF -> FullQueryStatus.VIOLATION_OFF_SHELF;
                            case FINISHED -> FullQueryStatus.FINISHED;
                            case OK -> {
                                LocalDateTime now = LocalDateTime.now();
                                if (now.isBefore(startTime)) {
                                    yield FullQueryStatus.NOT_STARTED;
                                }
                                if (now.isAfter(endTime)) {
                                    yield FullQueryStatus.FINISHED;
                                }
                                yield FullQueryStatus.IN_PROGRESS;
                            }
                        }
                )
                .setName(record.getName())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setProductType(record.getProductType())
                .setProduct(record.getProducts().size())
                .setFirstRuleType(firstRule.getType())
                .setFirstRuleDesc(firstRule.getDesc())
                .setViolation(record.getExtra().getViolation());
    }

}
