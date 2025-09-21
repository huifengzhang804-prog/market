package com.medusa.gruul.addon.seckill.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.seckill.model.SeckillConf;
import com.medusa.gruul.addon.seckill.model.SeckillConst;
import com.medusa.gruul.addon.seckill.model.bo.ProductKey;
import com.medusa.gruul.addon.seckill.model.dto.*;
import com.medusa.gruul.addon.seckill.model.enums.SeckillQueryStatus;
import com.medusa.gruul.addon.seckill.model.enums.SeckillStatus;
import com.medusa.gruul.addon.seckill.model.vo.*;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillActivityDao;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillOrderDao;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillProductDao;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillActivity;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillOrder;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillProduct;
import com.medusa.gruul.addon.seckill.service.SeckillActivityService;
import com.medusa.gruul.addon.seckill.util.SeckillUtil;
import com.medusa.gruul.common.model.activity.SimpleActivity;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.CurrentActivityKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.addon.CommonConfigService;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.RangeDateTime;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.bo.StockPriceBO;
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.dto.activity.ActivityCreateDTO;
import com.medusa.gruul.storage.api.dto.activity.ActivitySkuDTO;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 秒杀活动 service impl
 *
 * @author 张治保
 * @since 2024/5/28
 */
@Service
@RequiredArgsConstructor
public class SeckillActivityServiceImpl implements SeckillActivityService {

    private final SeckillOrderDao seckillOrderDao;
    private final GoodsRpcService goodsRpcService;
    private final SeckillActivityDao seckillActivityDao;
    private final SeckillProductDao seckillProductDao;

    private final ShopRpcService shopRpcService;
    private final StorageRpcService storageRpcService;
    private final StorageActivityRpcService storageActivityRpcService;
    private final CommonConfigService<SeckillConf> confCommonConfigService;


    @Override
    public List<RoundVO> rounds(LocalDate date) {
        //活动日期不能早于当前日期
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        //获取当前的秒杀活动每场活动持续时间
        Integer duration = confCommonConfigService.get().getDuration();

        List<RoundVO> rounds = CollUtil.newArrayList();
        //是否是同一天
        boolean sameDay = date.equals(nowDate);
        for (int round = 0; round < SeckillUtil.rounds(duration); round++) {
            //获取当前场次的时间范围
            Tuple2<LocalDateTime, LocalDateTime> roundTimeRange = SeckillUtil.timeRange(date, round, duration);
            LocalDateTime endTime = roundTimeRange._2();
            if (sameDay && SeckillUtil.isWrongEndTime(endTime)) {
                continue;
            }
            rounds.add(
                    new RoundVO().setRound(round)
                            .setStartTime(roundTimeRange._1().toLocalTime())
                            .setEndTime(endTime.toLocalTime())
            );
        }
        return rounds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = SeckillConst.SECKILL_ACTIVITY_CONF_LOCK_KEY)
    public void create(Long shopId, SeckillActivityDTO activity) {
        ShopInfoVO shopInfoVo = shopRpcService.getShopInfoByShopId(shopId);
        if (ShopMode.O2O.equals(shopInfoVo.getShopMode())) {
            throw new GlobalException("【O2O店铺暂不支持该活动】");
        }
        SeckillConf seckillConf = confCommonConfigService.get();
        Integer round = activity.getRound();
        Integer duration = seckillConf.getDuration();
        if (SeckillUtil.isWrongRound(round, duration)) {
            throw SystemCode.PARAM_TYPE_ERROR.exception();
        }
        LocalDate date = activity.getDate();
        // 保存秒杀活动
        LocalDateTime endTime = SeckillUtil.endTime(date, round, duration);
        if (SeckillUtil.isWrongEndTime(endTime)) {
            throw new GlobalException("本场活动还有不到五分种就结束了，请选择其他场次");
        }
        //查询活动商品信息
        Map<ShopProductKey, Product> productMap = goodsRpcService.getProductBatch(
                activity.getProducts()
                        .stream()
                        .map(product -> new ShopProductKey(shopId, product.getId()))
                        .collect(Collectors.toSet())

        );
        LocalDateTime startTime = SeckillUtil.startTime(date, round, duration);
        SeckillActivity seckillActivity = new SeckillActivity()
                .setShopId(shopId)
                .setStatus(SeckillStatus.OK)
                .setName(activity.getName())
                .setDate(date)
                .setRound(round)
                .setStackable(activity.getStackable())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setExtra(new SeckillActivity.Extra());
        //保存秒杀活动数据
        seckillActivityDao.save(seckillActivity);
        Long activityId = seckillActivity.getId();
        Set<SeckillActivityProductDTO> products = activity.getProducts();
        //保存活动商品 sku 库存信息
        List<ActivitySkuDTO> activitySkus = CollUtil.newArrayList();
        //渲染秒杀商品信息
        List<SeckillProduct> seckillProducts = products.stream()
                .flatMap(
                        product -> {
                            Long productId = product.getId();
                            ShopProductKey key = new ShopProductKey(shopId, productId);
                            Product prod = productMap.get(key);
                            if (prod == null) {
                                GlobalException exception = new GlobalException("商品不存在");
                                exception.setData(key);
                                throw exception;
                            }
                            return product.getSkus()
                                    .stream()
                                    .map(
                                            sku -> {
                                                Long skuId = sku.getId();
                                                Long skuStock = sku.getStock();
                                                Long skuPrice = sku.getPrice();
                                                activitySkus.add(
                                                        new ActivitySkuDTO()
                                                                .setProductId(productId)
                                                                .setSkuId(skuId)
                                                                .setStock(skuStock.intValue())
                                                                .setSalePrice(skuPrice)
                                                );
                                                return new SeckillProduct()
                                                        .setActivityId(activityId)
                                                        .setShopId(shopId)
                                                        .setProductId(productId)
                                                        .setProductName(prod.getName())
                                                        .setProductImage(prod.getPic())
                                                        .setSkuId(skuId)
                                                        .setSpecs(sku.getSpecs())
                                                        .setStock(skuStock)
                                                        .setPrice(skuPrice);
                                            }
                                    );
                        }
                ).toList();
        //保存秒杀商品数据
        seckillProductDao.saveBatch(seckillProducts);
        //是否可以开始 如果可以开始则缓存改场次数据 否则使用定时任务开启该该任务
        boolean cacheNow = SeckillUtil.cacheNow(startTime);
        RangeDateTime range = new RangeDateTime()
                .setStart(startTime)
                .setEnd(endTime);
        //如果不能直接缓存 则添加定时任务 由定时任务触发缓存
        //生成该场活动库存信息
        storageActivityRpcService.activityCreate(
                new ActivityCreateDTO()
                        .setShopId(shopId)
                        .setActivityType(OrderType.SPIKE)
                        .setActivityId(activityId)
                        .setSkus(activitySkus)
                        .setStartTime(startTime)
                        .setEndTime(seckillActivity.getEndTime())
        );
        //缓存活动 key
        SeckillUtil.cacheRound(range);
        if (!cacheNow) {
            return;
        }
        //缓存活动商品信息
        SeckillUtil.cacheActivityProducts(
                new CurrentActivityKey().setShopId(shopId).setActivityId(activityId),
                new SimpleActivity()
                        .setRange(range)
                        .setStackable(seckillActivity.getStackable()),
                seckillProducts.stream()
                        .collect(
                                Collectors.groupingBy(
                                        SeckillProduct::getProductId,
                                        Collectors.toMap(
                                                SeckillProduct::getSkuId,
                                                SeckillProduct::getPrice
                                        )
                                )
                        )
        );
    }

    @Override
    public IPage<SeckillActivityVO> page(SeckillActivityQueryDTO query) {
        //是否是店铺查询
        boolean isShop = ISecurity.anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN);
        //查询活动信息
        Long shopId = ISecurity.userMust().getShopId();
        IPage<SeckillActivity> page = seckillActivityDao.lambdaQuery()
                .eq(isShop, SeckillActivity::getShopId, shopId)
                .like(StrUtil.isNotBlank(query.getName()), SeckillActivity::getName, query.getName())
                .and(query.getStatus() != null, queryWrapper -> query.getStatus().getConsumer().accept(queryWrapper))
                .orderByDesc(SeckillActivity::getCreateTime)
                .page(query);
        if (CollUtil.isEmpty(page.getRecords())) {
            return page.convert(v -> null);
        }
        //数据格式转换
        return isShop ? shopPage(shopId, page) : platformPage(page);
    }


    @Override
    public SeckillActivityDetailVO detail(Long shopId, Long activityId) {
        SeckillActivity seckillActivity = seckillActivityDao.lambdaQuery()
                .eq(SeckillActivity::getShopId, shopId)
                .eq(SeckillActivity::getId, activityId)
                .one();
        StackableDiscount stackable = seckillActivity.getStackable();

        List<SeckillProduct> products = seckillProductDao.lambdaQuery()
                .eq(SeckillProduct::getShopId, shopId)
                .eq(SeckillProduct::getActivityId, activityId)
                .list();
        //转换数据格式
        return new SeckillActivityDetailVO()
                .setName(seckillActivity.getName())
                .setDate(seckillActivity.getDate())
                .setStartTime(seckillActivity.getStartTime().toLocalTime())
                .setEndTime(seckillActivity.getEndTime().toLocalTime())
                .setPayTimeout((int) stackable.getPayTimeout().toMinutes())
                .setStackable(stackable)
                .setProducts(
                        products.stream()
                                .collect(
                                        Collectors.groupingBy(
                                                product -> new ProductKey(
                                                        product.getProductId(),
                                                        product.getProductName(),
                                                        product.getProductImage()
                                                )
                                        )
                                )
                                .entrySet()
                                .stream()
                                .map(
                                        entry -> {
                                            ProductKey key = entry.getKey();
                                            return new SeckillProductVO()
                                                    .setId(key.getId())
                                                    .setName(key.getName())
                                                    .setImage(key.getImage())
                                                    .setSkus(
                                                            entry.getValue().stream()
                                                                    .map(
                                                                            sku -> new SeckillSkuVO()
                                                                                    .setId(sku.getSkuId())
                                                                                    .setSpecs(sku.getSpecs())
                                                                                    .setStock(sku.getStock())
                                                                                    .setPrice(sku.getPrice())
                                                                    ).toList()
                                                    );
                                        }
                                ).toList()
                );
    }


    @Override
    public IPage<SeckillRoundVO> roundPage(SeckillRoundPageDTO page) {
        Long shopId = page.getShopId();
        if (shopId != null) {
            Page<SeckillActivity> shopActivityPage = seckillActivityDao.page(
                    new Page<>(page.getCurrent(), page.getSize()),
                    Wrappers.<SeckillActivity>query()
                            .select("start_time", "ANY_VALUE(end_time) as end_time")
                            .lambda()
                            .eq(SeckillActivity::getShopId, shopId)
                            .eq(SeckillActivity::getStatus, SeckillStatus.OK)
                            .groupBy(SeckillActivity::getStartTime)
                            .orderByAsc(SeckillActivity::getStartTime)
            );
            return shopActivityPage.convert(
                    activity -> new SeckillRoundVO()
                            .setTime(
                                    new RangeDateTime()
                                            .setStart(activity.getStartTime())
                                            .setEnd(activity.getEndTime()))
                            .setStatus(
                                    activity.getStartTime().isBefore(LocalDateTime.now()) ?
                                            SeckillQueryStatus.IN_PROGRESS :
                                            SeckillQueryStatus.NOT_START
                            )
            );
        }
        IPage<RangeDateTime> roundPage = SeckillUtil.pageRound(page.getCurrent(), page.getSize());
        LocalDateTime now = LocalDateTime.now();
        return roundPage.convert(
                range -> {
                    SeckillQueryStatus status = SeckillQueryStatus.IN_PROGRESS;
                    LocalDateTime startTime = range.getStart();
                    LocalDateTime endTime = range.getEnd();
                    if (now.isBefore(startTime)) {
                        status = SeckillQueryStatus.NOT_START;
                    } else if (now.isAfter(endTime)) {
                        status = SeckillQueryStatus.FINISHED;
                    }
                    return new SeckillRoundVO()
                            .setTime(range)
                            .setStatus(status);
                }
        );
    }

    @Override
    public IPage<SeckillRoundProductVO> roundProductPage(SeckillRoundProductPageDTO page) {
        IPage<SeckillRoundProductVO> result = seckillProductDao.roundProductPage(page);
        List<SeckillRoundProductVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }

        //查询库存
        Map<ActivityShopProductKey, Map<Long, StockPriceBO>> activitySkuStockMap = storageRpcService.productSkuStockBatchMap(
                records.stream()
                        .map(record -> (ActivityShopProductKey) new ActivityShopProductKey()
                                .setProductId(record.getProductId())
                                .setShopId(record.getShopId())
                                .setActivityType(OrderType.SPIKE)
                                .setActivityId(record.getActivityId())
                        )
                        .collect(Collectors.toSet())
        );


        for (SeckillRoundProductVO record : records) {
            Map<Long, StockPriceBO> skuStockMap = activitySkuStockMap.get(
                    (ActivityShopProductKey) new ActivityShopProductKey()
                            .setProductId(record.getProductId())
                            .setShopId(record.getShopId())
                            .setActivityType(OrderType.SPIKE)
                            .setActivityId(record.getActivityId())
            );
            //活动库存
            long activityStock = 0;
            //划线价
            long price = 0;
            long minSalePrice = Long.MAX_VALUE;
            for (StockPriceBO value : MapUtil.emptyIfNull(skuStockMap).values()) {
                Long salePrice = value.getSalePrice();
                if (salePrice > minSalePrice) {
                    continue;
                }
                minSalePrice = salePrice;
                price = value.getPrice();
                activityStock += value.getStock();
            }
            record.setActivityStock(activityStock)
                    .setPrice(price);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offShelf(boolean isShop, OffShelfDTO offShelf) {
        Long shopId = offShelf.getShopId();
        Long activityId = offShelf.getActivityId();
        SeckillActivity activity = seckillActivityDao.lambdaQuery()
                .select(SeckillActivity::getStartTime, SeckillActivity::getExtra)
                .eq(SeckillActivity::getShopId, shopId)
                .eq(SeckillActivity::getStatus, SeckillStatus.OK)
                .eq(SeckillActivity::getId, activityId)
                .one();
        if (activity == null) {
            return;
        }
        //下架活动
        LambdaUpdateChainWrapper<SeckillActivity> update = seckillActivityDao.lambdaUpdate()
                .eq(SeckillActivity::getId, activityId)
                .eq(SeckillActivity::getShopId, shopId)
                //下架 平台下架为违规下架
                .set(SeckillActivity::getStatus, isShop ? SeckillStatus.OFF_SHELF : SeckillStatus.VIOLATION_OFF_SHELF);
        if (!isShop) {
            update.set(SeckillActivity::getExtra, JSON.toJSONString(activity.getExtra().setViolation(offShelf.getReason())));
        }
        update.update();

        //关闭活动库存
        storageActivityRpcService.activityClose(
                Set.of(
                        new ActivityCloseKey()
                                .setShopId(shopId)
                                .setActivityType(OrderType.SPIKE)
                                .setActivityId(activityId)
                )
        );

        //删除缓存信息
        // 删除店铺活动
        SeckillUtil.clearShopActivityCache(shopId, Set.of(activityId));
        //该场次活动计数器减一
        SeckillUtil.clearActivityRoundsCache(
                Map.of(
                        activity.getStartTime(),
                        (long) CommonPool.NUMBER_ONE
                )
        );
    }

    @Override
    public void delete(Long shopId, Set<Long> activityIds) {
        LocalDateTime now = LocalDateTime.now();
        List<SeckillActivity> activities = seckillActivityDao.lambdaQuery()
                .select(SeckillActivity::getStartTime, SeckillActivity::getId)
                .eq(SeckillActivity::getShopId, shopId)
                .in(SeckillActivity::getId, activityIds)
                .and(
                        //非正常状态
                        wrapper -> wrapper.ne(SeckillActivity::getStatus, SeckillStatus.OK)
                                //或活动未开始
                                .or().gt(SeckillActivity::getStartTime, now)
                                //或 活动已结束
                                .or().lt(SeckillActivity::getEndTime, now)
                )
                .list();
        //如果数据为空则直接跳过处理
        if (CollUtil.isEmpty(activities)) {
            return;
        }
        activityIds = activities.stream()
                .map(SeckillActivity::getId)
                .collect(Collectors.toSet());
        //删除数据
        seckillActivityDao.lambdaUpdate()
                .eq(SeckillActivity::getShopId, shopId)
                .in(SeckillActivity::getId, activityIds)
                .remove();
        //未开始的活动及对应的开始时间
        Map<Long, LocalDateTime> unstartActivityStarTimeMap = activities.stream()
                .filter(activity -> activity.getStartTime().isAfter(now))
                .collect(Collectors.toMap(SeckillActivity::getId, SeckillActivity::getStartTime));
        if (MapUtil.isEmpty(unstartActivityStarTimeMap)) {
            return;
        }
        //查询出未开始的活动绑定的商品 id 渲染成活动关闭需要的参数
        //关闭活动库存
        storageActivityRpcService.activityClose(
                activityIds
                        .stream()
                        .map(
                                activityId -> new ActivityCloseKey()
                                        .setShopId(shopId)
                                        .setActivityType(OrderType.SPIKE)
                                        .setActivityId(activityId)
                        ).collect(Collectors.toSet())
        );

        //删除缓存信息
        // 删除店铺活动
        SeckillUtil.clearShopActivityCache(shopId, unstartActivityStarTimeMap.keySet());
        //根据开始时间分组计数 同一个开始时间表示是同一场次活动        
        Map<LocalDateTime, Long> roundCountMap = unstartActivityStarTimeMap.values()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        )
                );
        SeckillUtil.clearActivityRoundsCache(roundCountMap);
    }

    /**
     * 平台查询需要查询参加人数和店铺名称
     *
     * @param page 原始分页数据
     * @return 分页数据
     */
    private IPage<SeckillActivityVO> platformPage(IPage<SeckillActivity> page) {
        //获取所有的店铺 id 集合
        Set<Long> shopIds = page.getRecords()
                .stream()
                .map(SeckillActivity::getShopId)
                .collect(Collectors.toSet());
        //获取店铺信息
        Map<Long, String> shopMap = shopRpcService.getShopInfoByShopIdList(shopIds)
                .stream()
                .collect(Collectors.toMap(
                        ShopInfoVO::getId,
                        ShopInfoVO::getName
                ));

        //查询参加人数
        String activityKeyInSql = page.getRecords()
                .stream()
                .map(key -> "(%s,%s)".formatted(key.getShopId(), key.getId()))
                .collect(Collectors.joining(","));
        List<SeckillOrder> orders = seckillOrderDao.query()
                //用户 id 作为参加人数统计
                .select("shop_id", "activity_id", "count(distinct user_id) AS user_id", "count(order_no) AS order_no")
                .inSql("(shop_id,activity_id)", activityKeyInSql)
                .groupBy("shop_id", "activity_id")
                .list();

        //以productId作为商品数量
        Map<Long, Map<Long, Long>> productCountMap = seckillProductDao.query()
                .select("shop_id", "activity_id", "count(distinct product_id) AS product_id")
                .inSql("(shop_id,activity_id)", activityKeyInSql)
                .groupBy("shop_id", "activity_id")
                .list()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                SeckillProduct::getShopId,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                SeckillProduct::getActivityId,
                                                                SeckillProduct::getProductId
                                                        )
                                                )
                                )
                        )
                );

        Map<CurrentActivityKey, SeckillOrder> activityUserCountMap = orders.stream()
                .collect(
                        Collectors.toMap(
                                order -> new CurrentActivityKey().setShopId(order.getShopId()).setActivityId(order.getActivityId()),
                                Function.identity()
                        )
                );
        return page.convert(
                record -> {
                    SeckillOrder curOrder = activityUserCountMap.get(new CurrentActivityKey().setShopId(record.getShopId()).setActivityId(record.getId()));
                    Map<Long, Long> shopActivityProducts = productCountMap.get(record.getShopId());
                    long productNum = shopActivityProducts == null ? CommonPool.NUMBER_ZERO :
                            shopActivityProducts.getOrDefault(record.getId(), (long) CommonPool.NUMBER_ZERO);
                    return new SeckillActivityVO()
                            .setShopId(record.getShopId())
                            .setShopName(shopMap.getOrDefault(record.getShopId(), "--"))
                            .setActivityId(record.getId())
                            .setName(record.getName())
                            .setStartTime(record.getStartTime())
                            .setEndTime(record.getEndTime())
                            .setStatus(queryStatus(record.getStatus(), record.getStartTime(), record.getEndTime()))
                            .setUser(curOrder == null ? CommonPool.NUMBER_ZERO : curOrder.getUserId())
                            .setPayOrder(curOrder == null ? CommonPool.NUMBER_ZERO : Long.parseLong(curOrder.getOrderNo()))
                            .setProduct(productNum)
                            .setViolation(record.getExtra().getViolation());
                }

        );
    }

    /**
     * 店铺查询 店铺查询需要查询 商品数、参加人数和支付单数
     *
     * @param shopId 店铺 id
     * @param page   分页数据
     * @return 分页数据
     */
    private IPage<SeckillActivityVO> shopPage(Long shopId, IPage<SeckillActivity> page) {
        //查询商品数
        Set<Long> activityIds = page.getRecords().stream().map(SeckillActivity::getId).collect(Collectors.toSet());
        //以productId作为商品数量
        Map<Long, Long> productCountMap = seckillProductDao.query()
                .select("activity_id", "count(distinct product_id) AS product_id")
                .in("activity_id", activityIds)
                .eq("shop_id", shopId)
                .groupBy("activity_id")
                .list()
                .stream()
                .collect(Collectors.toMap(SeckillProduct::getActivityId, SeckillProduct::getProductId));
        //查询参加人数和支付单数
        List<SeckillOrder> orders = seckillOrderDao.query()
                //用户 id 作为参加人数统计 订单号作为支付单数统计
                .select("activity_id", "count(distinct user_id) AS user_id, count(order_no) AS order_no")
                .in("activity_id", activityIds)
                .eq("shop_id", shopId)
                .groupBy("activity_id")
                .list();
        Map<Long, SeckillOrder> activityCount = orders.stream().collect(Collectors.toMap(SeckillOrder::getActivityId, Function.identity()));
        return page.convert(
                record -> {
                    SeckillOrder order = activityCount.getOrDefault(
                            record.getId(),
                            new SeckillOrder()
                                    .setUserId((long) CommonPool.NUMBER_ZERO)
                                    .setOrderNo(CommonPool.NUMBER_ZERO + "")
                    );
                    return new SeckillActivityVO()
                            .setActivityId(record.getId())
                            .setName(record.getName())
                            .setStartTime(record.getStartTime())
                            .setEndTime(record.getEndTime())
                            .setStatus(queryStatus(record.getStatus(), record.getStartTime(), record.getEndTime()))
                            .setUser(order.getUserId())
                            .setPayOrder(Long.valueOf(order.getOrderNo()))
                            .setProduct(productCountMap.getOrDefault(record.getId(), (long) CommonPool.NUMBER_ZERO))
                            .setViolation(record.getExtra().getViolation());
                }
        );
    }

    /**
     * 渲染前端使用的活动状态
     *
     * @param status    活动状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 前端使用的活动状态
     */
    private SeckillQueryStatus queryStatus(SeckillStatus status, LocalDateTime startTime, LocalDateTime endTime) {
        if (status != SeckillStatus.OK) {
            return SeckillQueryStatus.valueOf(status.name());
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return SeckillQueryStatus.NOT_START;
        }
        if (now.isAfter(endTime)) {
            return SeckillQueryStatus.FINISHED;
        }
        return SeckillQueryStatus.IN_PROGRESS;
    }


}
