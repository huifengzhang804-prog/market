package com.medusa.gruul.addon.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.enums.TeamErrorEnums;
import com.medusa.gruul.addon.team.model.constant.TeamConst;
import com.medusa.gruul.addon.team.model.count.TeamOrderCount;
import com.medusa.gruul.addon.team.model.count.TeamProductCount;
import com.medusa.gruul.addon.team.model.count.TeamProductInfoCount;
import com.medusa.gruul.addon.team.model.dto.*;
import com.medusa.gruul.addon.team.model.enums.TeamStatus;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderSummaryVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderUserPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import com.medusa.gruul.addon.team.mp.entity.TeamProduct;
import com.medusa.gruul.addon.team.mp.service.ITeamActivityService;
import com.medusa.gruul.addon.team.mp.service.ITeamOrderService;
import com.medusa.gruul.addon.team.mp.service.ITeamProductService;
import com.medusa.gruul.addon.team.service.TeamManagerService;
import com.medusa.gruul.addon.team.service.TeamService;
import com.medusa.gruul.addon.team.util.TeamUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.xxl.job.CronHelper;
import com.medusa.gruul.common.xxl.job.model.ScheduleTypeEnum;
import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.exception.RemoteResult;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import com.medusa.gruul.overview.api.rpc.ActivityConfigRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.dto.activity.ActivityCreateDTO;
import com.medusa.gruul.storage.api.dto.activity.ActivitySkuDTO;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 张治保 date 2023/3/2
 */
@Service
@RequiredArgsConstructor
public class TeamManagerServiceImpl implements TeamManagerService {


    private final JobService jobService;
    private final TeamService teamService;
    private final ShopRpcService shopRpcService;
    private final GoodsRpcService goodsRpcService;
    private final ITeamOrderService teamOrderService;
    private final ITeamActivityService teamActivityService;
    private final ITeamProductService teamProductService;
    private final StorageActivityRpcService storageActivityRpcService;
    private final ActivityConfigRpcService activityConfigRpcService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void newActivity(Long shopId, TeamDTO teamActivity) {
        //校验参数
        teamActivity.validParam();
        Tuple2<TeamActivity, List<TeamProduct>> saveActivity = this.saveActivity(shopId, teamActivity);
        TeamActivity activity = saveActivity._1();
        List<TeamProduct> teamProducts = saveActivity._2();
        //保存商品活动数据
        Long activityId = activity.getId();
        //获取商品id
        Set<Long> teamProductIds = teamProducts.stream().map(TeamProduct::getProductId).collect(Collectors.toSet());

        //添加定时任务 关闭活动
        int closeActivityJobId = this.addJob(false, shopId, activityId, activity.getEndTime(), teamProductIds);
        try {
            // 扣除库存 保留活动库存
            storageActivityRpcService.activityCreate(
                    new ActivityCreateDTO()
                            .setActivityType(OrderType.TEAM)
                            .setActivityId(activityId)
                            .setShopId(shopId)
                            .setSkus(
                                    teamProducts.stream()
                                            .map(teamProduct -> new ActivitySkuDTO()
                                                    .setProductId(teamProduct.getProductId())
                                                    .setSkuId(teamProduct.getSkuId())
                                                    .setStock(teamProduct.getStock().intValue())
                                            ).collect(Collectors.toList())
                            )
                            .setStartTime(activity.getStartTime())
                            .setEndTime(activity.getEndTime())
            );
        } catch (Exception ex) {
            //如果发现活动冲突 删除定时任务
            jobService.remove(closeActivityJobId);
            throw ex;
        }
        //如果此时活动未开启 则发送定时任务开启活动
        LocalDateTime openTime = activity.openTime();
        //居活动开始时间小于3分钟 直接开启活动
        if (activity.canOpen(LocalDateTime.now())) {
            teamService.openTeamActivity(activity, teamProducts);
            return;
        }
        //否则发送定时任务开启活动 打开定时任务
        this.addJob(true, shopId, activityId, openTime, teamProductIds);
    }

    /**
     * 保存活动数据至数据库
     *
     * @param shopId       店铺id
     * @param teamActivity 活动数据
     * @return 保存后的数据结构
     */
    private Tuple2<TeamActivity, List<TeamProduct>> saveActivity(Long shopId, TeamDTO teamActivity) {
        //保存活动
        TeamActivity teamActivityEntity = new TeamActivity()
//                .setViolated(Boolean.FALSE)
                .setStatus(TeamStatus.OK)
                .setShopId(shopId)
                .setName(teamActivity.getName())
                .setStartTime(teamActivity.getStartTime())
                .setEndTime(teamActivity.getEndTime())
                .setEffectTimeout(teamActivity.getEffectTimeout())
                .setMode(teamActivity.getMode())
                .setUsers(teamActivity.getUsers())
                .setSimulate(teamActivity.getSimulate())
                .setHuddle(teamActivity.getHuddle())
//                .setPreheat(teamActivity.getPreheat())
//                .setPreheatHours(teamActivity.getPreheatHours())
                .setStackable(
                        teamActivity.getStackable().setPayTimeout(Duration.ofMinutes(teamActivity.getPayTimeout())));
        boolean success = teamActivityService.save(teamActivityEntity);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
        Long activityId = teamActivityEntity.getId();
        // 保存活动商品
        List<TeamProduct> teamProducts = new ArrayList<>();
        //查询商品信息
        Map<ShopProductKey, Product> productBatchMap = goodsRpcService.getProductBatch(
                teamActivity.getProducts().stream()
                        .map(product -> new ShopProductKey().setShopId(shopId).setProductId(product.getProductId()))
                        .collect(Collectors.toSet())
        );

        teamActivity.getProducts().forEach(
                product -> product.getSkus().forEach(
                        sku -> {
                            Product productInfo =
                                    productBatchMap.get(new ShopProductKey().setShopId(shopId)
                                            .setProductId(product.getProductId()));
                            if (productInfo == null || ProductStatus.SELL_ON != productInfo.getStatus()) {
                                throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
                            }
                            teamProducts.add(
                                    new TeamProduct()
                                            .setShopId(shopId)
                                            .setActivityId(activityId)
                                            .setProductId(product.getProductId())
                                            .setProductName(productInfo.getName())
                                            .setProductImage(productInfo.getPic())
                                            .setSkuId(sku.getSkuId())
                                            .setStock(sku.getStock())
                                            .setPrices(sku.getPrices()));
                        }
                )
        );
        success = teamProductService.saveBatch(teamProducts);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
        return Tuple.of(teamActivityEntity, teamProducts);
    }


    /**
     * 添加关闭活动定时任务
     *
     * @param shopId      店铺id
     * @param activityId  活动id
     * @param triggerTime 触发任务时间
     */
    private int addJob(boolean isOpen, Long shopId, Long activityId, LocalDateTime triggerTime, Set<Long> teamProductIds) {
        return jobService.add(
                new XxlJobInfo()
                        .setJobDesc(isOpen ? "开启活动定时任务" : "关闭活动定时任务")
                        .setAuthor("system")
                        .setScheduleType(ScheduleTypeEnum.CRON.name())
                        .setScheduleConf(CronHelper.toCron(triggerTime))
                        .setExecutorHandler(isOpen ? TeamConst.TEAM_OPEN_JOB_HANDLER : TeamConst.TEAM_CLOSE_JOB_HANDLER)
                        .setExecutorParam(JSON.toJSONString(new TeamKey().setShopId(shopId).setActivityId(activityId).setProductId(teamProductIds)))
        );
    }

    @Override
    public IPage<TeamPageVO> activityPage(Option<Long> shopIdOpt, TeamPageDTO page) {
        IPage<TeamPageVO> result = teamActivityService.activityPage(page, shopIdOpt.getOrNull());
        List<TeamPageVO> records = result.getRecords();
        if (records.isEmpty()) {
            return result;
        }
        //records转换成对应的key
        Set<TeamKey> teamKeys =
                records.stream()
                        .map(record -> new TeamKey().setActivityId(record.getId()).setShopId(record.getShopId()))
                        .collect(Collectors.toSet());
        //查询商品数量
        Map<TeamKey, Integer> teamKeyProductNumMap = teamProductService.teamProductCounts(teamKeys)
                .stream()
                .collect(
                        Collectors.toMap(
                                count -> new TeamKey().setActivityId(count.getActivityId())
                                        .setShopId(count.getShopId()),
                                TeamProductCount::getProductNum
                        )
                );
        //查询活动商品数
        Map<TeamKey, Integer> teamKeyTeamOrderCountMap = teamOrderService.teamOrderCounts(teamKeys)
                .stream()
                .collect(
                        Collectors.toMap(
                                count -> new TeamKey().setActivityId(count.getActivityId())
                                        .setShopId(count.getShopId()),
                                TeamOrderCount::getOrders
                        )
                );
        //只查询支付订单数
        records.forEach(
                record -> {
                    TeamKey teamKey = new TeamKey().setActivityId(record.getId()).setShopId(record.getShopId());
                    record.setProductNum(teamKeyProductNumMap.getOrDefault(teamKey, CommonPool.NUMBER_ZERO))
                            .setOrders(teamKeyTeamOrderCountMap.getOrDefault(teamKey, CommonPool.NUMBER_ZERO));
                }
        );
        if (!shopIdOpt.isEmpty()) {
            return result;
        }
        //非店铺内部查询则查询店铺名称
        Map<Long, String> shopNameMap = shopRpcService.getShopInfoByShopIdList(
                        records.stream()
                                .map(TeamPageVO::getShopId)
                                .collect(Collectors.toSet())
                )
                .stream()
                .collect(Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getName));
        records.forEach(record -> record.setShopName(shopNameMap.getOrDefault(record.getShopId(), "unknown")));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivityBatch(Set<TeamKey> teamKeys) {
        //查询条件构造器
        LambdaQueryChainWrapper<TeamActivity> queryWrapper = teamActivityService.lambdaQuery();
        for (TeamKey teamKey : teamKeys) {
            queryWrapper.or().eq(TeamActivity::getShopId, teamKey.getShopId()).eq(TeamActivity::getId,
                    teamKey.getActivityId());
        }
        //批量查询
        List<TeamActivity> activities = queryWrapper.list();
        if (CollUtil.isEmpty(activities)) {
            return;
        }
        ClientType clientType = ISystem.clientTypeOpt().getOrNull();
        //平台删除：只有处于【已结束、违规下架、已下架】的活动才能删除
        if (ClientType.PLATFORM_CONSOLE.equals(clientType)) {
            activities = activities.stream().filter(activity -> {
                if (activity.finished() || activity.illegalSellOff() || activity.shopSellOff()) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(activities)) {
                throw TeamErrorEnums.CAN_NOT_DELETE.exception();
            }
        }
        if (!ClientType.PLATFORM_CONSOLE.equals(clientType)) {
            //非平台删除
            Long shopId = ISystem.shopIdMust();

            activities = activities.stream().filter(activity -> {
                if (!shopId.equals(activity.getShopId())) {
                    return Boolean.FALSE;
                }
                //删除：只有处于【未开始、已结束、违规下架、已下架】的活动才能删除
                if (activity.notStarted() || activity.finished() ||
                        activity.shopSellOff() || activity.illegalSellOff()) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(activities)) {
                throw TeamErrorEnums.CAN_NOT_REMOVE.exception();
            }
        }
        // 删除活动
        List<Long> activityIds = activities.stream().map(TeamActivity::getId).collect(Collectors.toList());
        teamActivityService.lambdaUpdate()
                .set(TeamActivity::getDeleted, Boolean.TRUE)
//                .set(ClientType.PLATFORM_CONSOLE.equals(clientType), TeamActivity::getPlatformDeleteFlag, Boolean.TRUE)
//                .set(!ClientType.PLATFORM_CONSOLE.equals(clientType), TeamActivity::getShopDeleteFlag, Boolean.TRUE)
                .in(TeamActivity::getId, activityIds)
                .update();

        if (ClientType.PLATFORM_CONSOLE.equals(clientType)) {
            //如果是平台端删除 并不真正删除 只是主表平台删除打上一个标记
            return;
        }

        LambdaQueryChainWrapper<TeamProduct> productQueryWrapper = teamProductService.lambdaQuery();
        for (TeamActivity teamActivity : activities) {
            productQueryWrapper.or().eq(TeamProduct::getShopId, teamActivity.getShopId()).eq(TeamProduct::getActivityId,
                    teamActivity.getId());
        }
        // 删除商品缓存
        //1. 查询商品
        //2. 删除商品缓存
        List<TeamProduct> products = productQueryWrapper.list();
        Map<TeamKey, TeamActivity> keyActivityMap = activities.stream()
                .collect(
                        Collectors.toMap(
                                activity -> new TeamKey().setActivityId(activity.getId())
                                        .setShopId(activity.getShopId()),
                                v -> v
                        )
                );
        Map<TeamKey, List<TeamProduct>> keyProductsMap = products.stream()
                .collect(
                        Collectors.groupingBy(
                                product -> new TeamKey().setActivityId(product.getActivityId())
                                        .setShopId(product.getShopId())
                        )
                );
        Map<Long, Set<String>> productCacheKeyMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        keyProductsMap.forEach(
                (key, value) -> {
                    TeamActivity teamActivity = keyActivityMap.get(key);
                    long score = TeamUtil.getScoreByTime(teamActivity.getEndTime());
                    Set<Long> productIds = value.stream().map(TeamProduct::getProductId).collect(Collectors.toSet());
                    productIds.forEach(
                            productId -> {
                                Set<String> keys = productCacheKeyMap.computeIfAbsent(score, k -> new HashSet<>());
                                keys.add(TeamUtil.getTeamProductCacheKey(teamActivity.getShopId(), productId));
                            }
                    );
                }
        );
        //删除活动商品 缓存
        teamService.removeProductsActivityCache(productCacheKeyMap);
        //rpc删除活动库存
        storageActivityRpcService.activityClose(
                teamKeys.stream().map(
                        teamKey -> new ActivityCloseKey()
                                .setActivityType(OrderType.TEAM)
                                .setActivityId(teamKey.getActivityId())
                                .setShopId(teamKey.getShopId())
                ).collect(Collectors.toSet())
        );
    }

    @Override
    public TeamDTO getActivity(Long shopId, Long activityId) {
        if (shopId == null) {
            throw new GlobalException("shopId must be not null");
        }

        TeamActivity teamActivity = teamActivityService.lambdaQuery()

                .eq(TeamActivity::getShopId, shopId)
                .eq(BaseEntity::getId, activityId)
                .oneOpt()
                .orElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST));

        return new TeamDTO()
                .setName(teamActivity.getName())
                .setStartTime(teamActivity.getStartTime())
                .setEndTime(teamActivity.getEndTime())
                .setEffectTimeout(teamActivity.getEffectTimeout())
                .setMode(teamActivity.getMode())
                .setUsers(teamActivity.getUsers())
                .setPayTimeout((int) teamActivity.getStackable().getPayTimeout().toMinutes())
                .setSimulate(teamActivity.getSimulate())
                .setHuddle(teamActivity.getHuddle())

                .setStackable(teamActivity.getStackable())
                .setProducts(
                        teamProductService.lambdaQuery()
                                .eq(TeamProduct::getShopId, shopId)
                                .eq(TeamProduct::getActivityId, activityId)
                                .list()
                                .stream()
                                .collect(
                                        Collectors.groupingBy(
                                                TeamProduct::getProductId,
                                                Collectors.mapping(
                                                        teamProduct -> new TeamSkuDTO().setSkuId(teamProduct.getSkuId())
                                                                .setStock(teamProduct.getStock())
                                                                .setPrices(teamProduct.getPrices()),
                                                        Collectors.toList()
                                                )
                                        )
                                ).entrySet()
                                .stream().map(
                                        entry -> new TeamProductDTO()
                                                .setProductId(entry.getKey())
                                                .setSkus(entry.getValue())
                                ).toList()
                );
    }

    /**
     * 店铺下架
     *
     * @param teamActivityId 活动id
     */
    @Override
    public void shopShelfOff(Long teamActivityId) {
        TeamActivity teamActivity = teamActivityService.getBaseMapper().selectById(teamActivityId);
        if (Objects.isNull(teamActivity)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        //只有处于【进行中】的活动才能执行【下架】操作
        if (!teamActivity.processing()) {
            throw TeamErrorEnums.TEAM_STATE_ERROR.exception();
        }
        teamActivityService.lambdaUpdate()
                .eq(TeamActivity::getShopId, teamActivity.getShopId())
                .eq(TeamActivity::getId, teamActivity.getId())
                //店铺下架
                .set(TeamActivity::getStatus, TeamStatus.SHOP_OFF_SHELF)
                .update();
        // 关闭活动
        teamService.closeTeamActivity(teamActivity);
    }

    /**
     * 查询违规下架原因
     *
     * @param teamActivityId 活动id
     * @return 违规下架原因
     */
    @Override
    public String queryIllegalReason(Long teamActivityId) {
        TeamActivity teamActivity = teamActivityService.getBaseMapper().selectById(teamActivityId);
        if (Objects.isNull(teamActivity)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (!teamActivity.illegalSellOff()) {
            throw TeamErrorEnums.TEAM_STATE_ERROR.exception();
        }
        return teamActivity.getViolationReason();
    }

    @Override
    public void saveRule(ActivityConfigDTO activityConfigDTO) {
        activityConfigDTO.setType(ActivityConfigType.TEAM_ACTIVITY_RULE);
        activityConfigRpcService.saveOrUpdateConfig(activityConfigDTO);
    }

    @Override
    public String queryRule() {
        RemoteResult<ActivityConfigDTO> activityConfigDTORemoteResult = activityConfigRpcService.queryActivityConfigByType(
                ActivityConfigType.TEAM_ACTIVITY_RULE);
        if (activityConfigDTORemoteResult.isSuccess() && Objects.nonNull(activityConfigDTORemoteResult.getData())) {
            return activityConfigDTORemoteResult.getData().getContent();
        }
        return null;
    }

    @Override
    public void violateActivity(ViolationDTO violationDTO) {
        //查询活动
        TeamActivity activity = teamActivityService.lambdaQuery()
                .eq(TeamActivity::getId, violationDTO.getTeamActivityId())
                .oneOpt()
                .orElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST));
        //未开始 进行中的才可以进行违规下架
        if (!(activity.notStarted() || activity.processing())) {
            throw TeamErrorEnums.TEAM_STATE_ERROR.exception();
        }
        //设置为违规活动
        teamActivityService.lambdaUpdate()
                .eq(TeamActivity::getShopId, violationDTO.getShopId())
                .eq(TeamActivity::getId, violationDTO.getTeamActivityId())
                //违规下架
                .set(TeamActivity::getViolationReason, violationDTO.getViolationReason())
                .set(TeamActivity::getStatus, TeamStatus.VIOLATION)
                .update();
        // 关闭活动
        teamService.closeTeamActivity(activity);

    }

    @Override
    public IPage<TeamOrderPageVO> orderPage(TeamOrderPageDTO query) {
        return teamOrderService.orderPage(query);
    }

    @Override
    public TeamOrderSummaryVO getOrderSummary(TeamSummaryDTO summary) {
        return Option.of(teamOrderService.getOrderSummary(summary))
                .peek(
                        orderSummary -> {
                            ActivityShopProductKey key =
                                    new ActivityShopProductKey().setProductId(orderSummary.getProductId());
                            key.setShopId(orderSummary.getShopId()).setActivityId(orderSummary.getActivityId());
                            Set<TeamProductInfoCount> productsInfo = teamProductService.productsInfo(Set.of(key));
                            TeamProductInfoCount productInfo = productsInfo.iterator().next();
                            orderSummary.setProductName(productInfo.getProductName())
                                    .setProductImage(productInfo.getProductImage());
                        }
                )
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST));
    }

    @Override
    public IPage<TeamOrderUserPageVO> orderUserPage(TeamOrderUserPageDTO query) {
        IPage<TeamOrder> result = teamOrderService.lambdaQuery()
                .select(
                        TeamOrder::getOrderNo,
                        TeamOrder::getUserId,
                        TeamOrder::getAvatar,
                        TeamOrder::getNickname,
                        TeamOrder::getCommander,
                        TeamOrder::getPrice,
                        TeamOrder::getAmount,
                        TeamOrder::getCreateTime
                )
                .eq(query.getShopId() != null, TeamOrder::getShopId, query.getShopId())
                .eq(StrUtil.isNotEmpty(query.getTeamNo()), TeamOrder::getTeamNo, query.getTeamNo())
                .page(query);
        List<TeamOrder> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return new Page<>(query.getCurrent(), query.getSize());
        }
        IPage<TeamOrderUserPageVO> page = new Page<>(query.getCurrent(), query.getSize());
        page.setTotal(result.getTotal());
        page.setRecords(
                records.stream()
                        .map(
                                teamOrder -> new TeamOrderUserPageVO()
                                        .setOrderNo(teamOrder.getOrderNo())
                                        .setUserId(teamOrder.getUserId())
                                        .setAvatar(teamOrder.getAvatar())
                                        .setNickname(teamOrder.getNickname())
                                        .setCommander(teamOrder.getCommander())
                                        .setPrice(teamOrder.getPrice())
                                        .setAmount(teamOrder.getAmount())
                                        .setCreateTime(teamOrder.getCreateTime())
                        ).collect(Collectors.toList())
        );
        return page;
    }

    @Override
    public boolean teamStatusValid(Long userId, String teamNo) {
        return BooleanUtil.isTrue(
                RedisUtil.getRedisTemplate().hasKey(
                        RedisUtil.key(TeamConst.USER_TEAM_SUCCESS_CACHE_KEY, teamNo, userId)
                )
        );
    }


}
