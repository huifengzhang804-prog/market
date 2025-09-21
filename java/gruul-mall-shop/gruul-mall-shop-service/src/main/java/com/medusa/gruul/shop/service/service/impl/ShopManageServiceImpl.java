package com.medusa.gruul.shop.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.model.OrderEvaluateCountDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.model.ShopBalanceVO;
import com.medusa.gruul.overview.api.rpc.OverviewRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.entity.ShopBankAccount;
import com.medusa.gruul.shop.api.entity.ShopVisitor;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.enums.OperaReason;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.shop.api.helper.ShopGeo;
import com.medusa.gruul.shop.api.model.dto.ShopAdminMapDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.dto.SigningCategoryDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.service.model.ShopConstants;
import com.medusa.gruul.shop.service.model.dto.ShopDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryNoPageDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryPageDTO;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import com.medusa.gruul.shop.service.model.vo.ShopListVO;
import com.medusa.gruul.shop.service.model.vo.ShopStatusQuantityVO;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import com.medusa.gruul.shop.service.model.vo.SupplierStatisticsVO;
import com.medusa.gruul.shop.service.mp.entity.ShopRegisterInfo;
import com.medusa.gruul.shop.service.mp.service.IShopBankAccountService;
import com.medusa.gruul.shop.service.mp.service.IShopRegisterInfoService;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import com.medusa.gruul.shop.service.mp.service.IShopVisitorService;
import com.medusa.gruul.shop.service.service.ShopManageService;
import com.medusa.gruul.shop.service.service.addon.ShopAddonSupporter;
import com.medusa.gruul.shop.service.util.ShopBanAccountConvetor;
import com.medusa.gruul.shop.service.util.ShopConvetor;
import com.medusa.gruul.shop.service.util.ShopRegisterInfoConvert;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/4/15
 */
@Service
@RequiredArgsConstructor
public class ShopManageServiceImpl implements ShopManageService {

    private final Executor globalExecutor;
    private final IShopService shopService;
    private final RabbitTemplate rabbitTemplate;
    private final OrderRpcService orderRpcService;
    private final OverviewRpcService overviewRpcService;
    private final IShopVisitorService shopVisitorService;
    private final IShopRegisterInfoService shopRegisterInfoService;
    private final IShopBankAccountService shopBankAccountService;
    private final ShopAddonSupporter shopAddonSupporter;
    private final UaaRpcService uaaRpcService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final GoodsRpcService goodsRpcService;
    private final StorageRpcService storageRpcService;


    @Override
    public IPage<ShopVO> pageShop(ShopQueryPageDTO page) {
        if (CollUtil.isEmpty(page.getShopModes())) {
            page.setShopModes(Set.of(ShopMode.COMMON, ShopMode.O2O));
        }
        IPage<ShopVO> shopPage = TenantShop.disable(() -> shopService.pageShop(page));
        List<ShopVO> records = shopPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return shopPage;
        }
        AtomicReference<Map<Long, OrderEvaluateCountDTO>> orderEvaluateCountMapRef = new AtomicReference<>();
        AtomicReference<Map<Long, ShopBalanceVO>> shopBalanceMapRef = new AtomicReference<>();
        Set<Long> shopIds = records.stream().map(ShopVO::getId).collect(Collectors.toSet());
        //根据用户id查询用户手机号
        Set<Long> userIds = records.stream().map(ShopVO::getUserId).collect(Collectors.toSet());
        AtomicReference<Map<Long, UserInfoVO>> userInfoMap = new AtomicReference<>();
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        //评价
                        () -> orderEvaluateCountMapRef.set(orderRpcService.getOrderEvaluateCount(shopIds)),
                        //余额
                        () -> shopBalanceMapRef.set(overviewRpcService.getShopBalanceMap(shopIds)),
                        //店铺管理员手机号
                        () -> userInfoMap.set(CollUtil.isEmpty(userIds) ? new HashMap<>() : uaaRpcService.getUserDataBatchByUserIds(userIds))
                )
        );
        Map<Long, OrderEvaluateCountDTO> orderEvaluateCountMap = orderEvaluateCountMapRef.get();
        Map<Long, ShopBalanceVO> shopBalanceMap = shopBalanceMapRef.get();
        //TODO查询出来，转map set值
        records.forEach(
                shop -> {
                    Long shopId = shop.getId();
                    //评分
                    if (ShopStatus.UNDER_REVIEW.equals(shop.getStatus())) {
                        //审核中的店铺默认0分
                        shop.setScore(CommonPool.NUMBER_ZERO + "");
                    } else {
                        shop.setScore(Option.of(orderEvaluateCountMap.get(shopId)).map(OrderEvaluateCountDTO::score).getOrElse(OrderEvaluateCountDTO::defaultScore));
                    }
                    //余额
                    shop.setShopBalance(Option.of(shopBalanceMap.get(shopId)).getOrElse(ShopBalanceVO::defaultBalance));
                    //店铺管理员手机号
                    shop.setUserMobile(MapUtil.isEmpty(userInfoMap.get()) ? null : userInfoMap.get().get(shop.getUserId()).getMobile());
                }
        );
        return shopPage;
    }


    @Override
    public IPage<ShopListVO> pageShopList(ShopQueryPageDTO page) {
        if (CollUtil.isEmpty(page.getShopModes())) {
            page.setShopModes(Set.of(ShopMode.COMMON, ShopMode.O2O));
        }
        IPage<ShopListVO> voiPage = shopService.pageShopList(page);
        List<ShopListVO> records = voiPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return voiPage;
        }
        AtomicReference<Map<Long, OrderEvaluateCountDTO>> orderEvaluateCountMapRef = new AtomicReference<>();
        AtomicReference<Map<Long, Long>> shopGoodStockCountMapRef = new AtomicReference<>();
        AtomicReference<Map<Long, ShopBalanceVO>> shopBalanceMapRef = new AtomicReference<>();
        Set<Long> shopIds = records.stream().map(ShopListVO::getId).collect(Collectors.toSet());
        //根据用户id查询用户手机号
        Set<Long> userIds = records.stream().map(ShopListVO::getUserId).collect(Collectors.toSet());
        AtomicReference<Map<Long, UserInfoVO>> userInfoMap = new AtomicReference<>();
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        () -> {
                            //装修时查询店铺下在售商品且有库存的数量
                            if (page.getForDecoration()) {
                                Map<Long, List<Long>> shopProductIds = goodsRpcService.queryShopOnSaleProductIds(shopIds);
                                if (CollUtil.isNotEmpty(shopProductIds)) {
                                    Map<Long, Long> shopGoodStockCountMap = storageRpcService.queryShopHasOnSaleProducts(shopProductIds);
                                    shopGoodStockCountMapRef.set(shopGoodStockCountMap);
                                }
                            }

                        },
                        //评价
                        () -> orderEvaluateCountMapRef.set(orderRpcService.getOrderEvaluateCount(shopIds)),
                        //余额
                        () -> shopBalanceMapRef.set(overviewRpcService.getShopBalanceMap(shopIds)),
                        //店铺管理员手机号
                        () -> userInfoMap.set(CollUtil.isEmpty(userIds) ? new HashMap<>() : uaaRpcService.getUserDataBatchByUserIds(userIds))
                )
        );
        Map<Long, OrderEvaluateCountDTO> orderEvaluateCountMap = orderEvaluateCountMapRef.get();
        Map<Long, ShopBalanceVO> shopBalanceMap = shopBalanceMapRef.get();
        records.forEach(
                shop -> {
                    Long shopId = shop.getId();
                    Map<Long, Long> shopGoodStockCountMap = shopGoodStockCountMapRef.get();
                    if (Objects.nonNull(shopGoodStockCountMap)) {
                        Long l = shopGoodStockCountMap.get(shopId);
                        if (Objects.nonNull(l) && l > 0) {
                            shop.setHasOnSaleAndStock(Boolean.TRUE);
                        }
                    }

                    //评分
                    if (ShopStatus.UNDER_REVIEW.equals(shop.getStatus())) {
                        //审核中的店铺默认0分
                        shop.setScore(CommonPool.NUMBER_ZERO + "");
                    } else {
                        shop.setScore(Option.of(orderEvaluateCountMap.get(shopId)).map(OrderEvaluateCountDTO::score).getOrElse(OrderEvaluateCountDTO::defaultScore));
                    }
                    //余额
                    shop.setShopBalance(Option.of(shopBalanceMap.get(shopId)).getOrElse(ShopBalanceVO::defaultBalance));
                    //店铺管理员手机号
                    shop.setUserMobile(MapUtil.isEmpty(userInfoMap.get()) ? null : userInfoMap.get().get(shop.getUserId()).getMobile());
                    shop.setUserName(MapUtil.isEmpty(userInfoMap.get()) ? null : userInfoMap.get().get(shop.getUserId()).getNickname());
                }
        );
        return voiPage;
    }


    @Override
    public void reviewAuditShop(Long shopId) {
        Shop shop = shopService.lambdaQuery()
                .eq(BaseEntity::getId, shopId)
                .eq(Shop::getStatus, ShopStatus.REJECT)
                .one();
        if (shop == null) {
            throw ShopError.SHOP_NOT_EXIST.exception();
        }
        shop.setStatus(ShopStatus.UNDER_REVIEW);
        shopService.updateById(shop);

    }

    /**
     * 获取店铺状态(待审核)数量
     *
     * @param page 检索条件
     * @return 店铺待审核数量
     */
    @Override
    public Long getShopStatusCount(ShopQueryNoPageDTO page) {
        if (CollUtil.isEmpty(page.getShopModes())) {
            page.setShopModes(Set.of(ShopMode.COMMON, ShopMode.O2O));
        }
        return TenantShop.disable(() -> shopService.getShopStatusCount(page));
    }

    @Override
    public String queryRejectReason(Long shopId) {

        return TenantShop.disable(() -> shopService.queryRejectReason(shopId));
    }

    @Override
    public ShopVO getShopDetail(Long shopId) {
        Shop one = TenantShop.disable(() -> shopService.lambdaQuery().eq(Shop::getId, shopId).one());

        if (Objects.isNull(one)) {
            throw ShopError.SHOP_NOT_EXIST.exception();
        }
        ShopVO shopVo = ShopConvetor.INSTANCE.toShopVo(one);

        ShopBankAccount shopBankAccount = TenantShop.disable(() -> shopBankAccountService.lambdaQuery()
                .eq(ShopBankAccount::getShopId, shopId).one());
        shopVo.setBankAccount(ShopBanAccountConvetor.INSTANCE.toShopBankAccountVO(shopBankAccount));
        ShopRegisterInfo shopRegisterInfo = TenantShop.disable(() -> shopRegisterInfoService.lambdaQuery()
                .eq(ShopRegisterInfo::getShopId, shopId).one());
        shopVo.setRegisterInfo(ShopRegisterInfoConvert.INSTANCE.toShopRegisterInfoVo(shopRegisterInfo));


        return shopVo;
    }

    @Override
    public void updateShopOnShelfGoodsCount(Long shopId) {
        Integer productCount = goodsRpcService.queryHasStockAndOnSaleProductCount(shopId);
        shopService.lambdaUpdate()
                .set(Shop::getOnSaleGoodsCount, productCount)
                .eq(BaseEntity::getId, shopId)
                .update();

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(ShopConstants.SHOP_UPDATE_LOCK)
    public void newShop(ShopDTO shop) {
        boolean match = ISecurity.matcher().any(SecureUser::getRoles, Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN).match();
        Shop shopInfo = shop.saveShop(false, match, shopService, null, ISecurity.userMust().getId(), uaaRpcService);
        Long shopId = shopInfo.getId();
        //多租户 数据
        ISystem.shopId(
                shopId,
                () -> {
                    shop.saveShopRegisterInfo(false, shopRegisterInfoService);
                    shop.saveShopBankAccount(false, shopBankAccountService);
                }

        );
        if (shop.getExtractionType() == ExtractionType.ORDER_SALES_EXTRACTION && CollUtil.isNotEmpty(shop.getSigningCategory())) {
            List<SigningCategoryDTO> signingCategoryList = shop.getSigningCategory()
                    .stream()
                    .peek(
                            signingCategory ->
                                    signingCategory.setCustomDeductionRatio(Long.valueOf(shop.getDrawPercentage())))
                    .toList();
            shop.setSigningCategory(signingCategoryList);
        }
        // 编辑签约类目
        Optional.ofNullable(shop.getSigningCategory())
                .filter(category -> match)
                .filter(signingCategory -> BooleanUtil.isFalse(shopAddonSupporter.editSingingCategory(signingCategory, shopId, shopInfo.getShopMode())))
                .ifPresent(signingCategory -> {
                    throw ShopError.SHOP_CATEGORY_UPDATE_FAIL.exception();
                });
        boolean isOkStatus = shopInfo.getStatus() == ShopStatus.NORMAL;
        //非供应商且正常状态的店铺 缓存店铺geo 定位
        if (isOkStatus && ShopMode.SUPPLIER != shopInfo.getShopMode()) {
            ShopGeo.add(
                    RedisUtil.getRedisTemplate(),
                    shopId,
                    shop.getLocation()
            );
        }
        globalExecutor.execute(
                () -> {
                    rabbitTemplate.convertAndSend(
                            ShopRabbit.SHOP_ADMIN_CHANGE.exchange(),
                            ShopRabbit.SHOP_ADMIN_CHANGE.routingKey(),
                            new ShopAdminMapDTO()
                                    .setShopMode(shopInfo.getShopMode())
                                    .setShopId(shopInfo.getId())
                                    .setUserId(shop.getUserId())
                                    .setEnable(isOkStatus)
                    );
                    rabbitTemplate.convertAndSend(
                            ShopRabbit.SHOP_UPDATE.exchange(),
                            ShopRabbit.SHOP_UPDATE.routingKey(),
                            ShopInfoVO.fromShop(shopInfo)
                    );
                }
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = ShopConstants.SHOP_UPDATE_LOCK, key = "#shopId")
    public void editShop(Long shopId, ShopDTO shop) {
        boolean match = ISecurity.matcher().any(SecureUser::getRoles, Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN).match();
        Shop shopEntity = RedisUtil.doubleDeletion(
                () -> shop.saveShop(true, match, shopService, shopId, ISecurity.userMust().getId(), uaaRpcService),
                ShopConstants.SHOP_BASE_INFO,
                shopId
        );
        //多租户数据
        ISystem.shopId(
                shopId,
                () -> {
                    shop.saveShopRegisterInfo(true, shopRegisterInfoService);
                    shop.saveShopBankAccount(true, shopBankAccountService);
                }
        );

        if (shop.getExtractionType() == ExtractionType.ORDER_SALES_EXTRACTION && CollUtil.isNotEmpty(shop.getSigningCategory())) {
            List<SigningCategoryDTO> signingCategoryList = shop.getSigningCategory()
                    .stream()
                    .peek(
                            signingCategory ->
                                    signingCategory.setCustomDeductionRatio(Long.valueOf(shop.getDrawPercentage())))
                    .toList();
            shop.setSigningCategory(signingCategoryList);
        }
        // 编辑签约类目
        Optional.ofNullable(shop.getSigningCategory())
                .filter(category -> match)
                .filter(signingCategory -> {
                    Boolean result = shopAddonSupporter.editSingingCategory(signingCategory, shopId, shopEntity.getShopMode());
                    return BooleanUtil.isFalse(result);
                })
                .ifPresent(signingCategory -> {
                    throw SystemCode.DATA_UPDATE_FAILED.exception();
                });
        if (ShopStatus.NORMAL == shopEntity.getStatus() && ShopMode.SUPPLIER != shopEntity.getShopMode()) {
            ShopGeo.add(
                    RedisUtil.getRedisTemplate(),
                    shopId,
                    shopEntity.getLocation()
            );
        }
        globalExecutor.execute(
                () -> {
                    rabbitTemplate.convertAndSend(
                            ShopRabbit.SHOP_ADMIN_CHANGE.exchange(),
                            ShopRabbit.SHOP_ADMIN_CHANGE.routingKey(),
                            new ShopAdminMapDTO().setShopMode(shop.getShopMode()).setShopId(shopId).setUserId(shop.getUserId())
                    );
                    rabbitTemplate.convertAndSend(
                            ShopRabbit.SHOP_UPDATE.exchange(),
                            ShopRabbit.SHOP_UPDATE.routingKey(),
                            ShopInfoVO.fromShop(shopEntity)
                    );
                    pigeonChatStatisticsRpcService.removeIMShopInfoCache(shopId);

                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = ShopConstants.SHOP_UPDATE_LOCK, batchParamName = "#shopIds", key = "#item")
    public void deleteShop(Set<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        List<Long> hasUnCompleteOrderShopIds = orderRpcService.queryShopUnCompleteOrderNum(shopIds);

        //要删除的店铺中存在 未完成的订单
        ShopError.SHOP_EXIST_UNCOMPLETE_ORDER.falseThrow(CollectionUtils.isEmpty(hasUnCompleteOrderShopIds));
        Map<Long, ShopBalanceVO> shopBalanceMap = overviewRpcService.getShopBalanceMap(shopIds);

        Set<String> keys = shopIds.stream()
                .map(
                        shopId -> {
                            ShopBalanceVO balance = shopBalanceMap.get(shopId);
                            //是否有余额不能删除
                            boolean dontAllowDelete = balance != null && (0 != balance.getUncompleted() || balance.getUndrawn() != 0);
                            if (dontAllowDelete) {
                                throw ShopError.SHOP_EXIST_BALANCE_NOT_DEL.dataEx(new ShopProductSkuKey().setShopId(shopId));
                            }
                            return RedisUtil.key(ShopConstants.SHOP_BASE_INFO, shopId);

                        }
                ).collect(Collectors.toSet());
        boolean success = RedisUtil.doubleDeletion(
                () -> shopService.removeBatchByIds(shopIds),
                () -> RedisUtil.delete(keys)
        );
        SystemCode.DATA_DELETE_FAILED.falseThrow(success);
        ShopGeo.batchRemove(RedisUtil.getRedisTemplate(), shopIds);
        rabbitTemplate.convertAndSend(
                ShopRabbit.SHOP_ENABLE_DISABLE.exchange(),
                ShopRabbit.SHOP_ENABLE_DISABLE.routingKey(),
                new ShopsEnableDisableDTO()
                        .setEnable(Boolean.FALSE)
                        .setReason(OperaReason.DELETED)
                        .setShopIds(shopIds)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = ShopConstants.SHOP_UPDATE_LOCK, batchParamName = "#shopIds", key = "#item")
    public void enableDisableShop(Boolean isEnable, Set<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        Set<String> cacheKeys = shopIds.stream()
                .map(shopId -> RedisUtil.key(ShopConstants.SHOP_BASE_INFO, shopId))
                .collect(Collectors.toSet());
        boolean success = RedisUtil.doubleDeletion(
                () -> shopService.lambdaUpdate()
                        .set(Shop::getStatus, isEnable ? ShopStatus.NORMAL : ShopStatus.FORBIDDEN)
                        .in(Shop::getId, shopIds)
                        .update(),
                () -> RedisUtil.delete(cacheKeys)
        );
        SystemCode.DATA_DELETE_FAILED.falseThrow(success);
        //删除或保存店铺 geo 信息
        if (isEnable) {
            List<Shop> nonSupplierShops = shopService.lambdaQuery()
                    .select(BaseEntity::getId, Shop::getLocation)
                    .in(BaseEntity::getId, shopIds)
                    .ne(Shop::getShopMode, ShopMode.SUPPLIER)
                    .list();
            if (!nonSupplierShops.isEmpty()) {
                ShopGeo.batchAdd(
                        RedisUtil.getRedisTemplate(),
                        nonSupplierShops.stream()
                                .collect(
                                        Collectors.toMap(
                                                BaseEntity::getId,
                                                Shop::getLocation
                                        )
                                )
                );
            }
        } else {
            ShopGeo.batchRemove(RedisUtil.getRedisTemplate(), shopIds);
        }
        //启用/禁用 店铺
        rabbitTemplate.convertAndSend(
                ShopRabbit.SHOP_ENABLE_DISABLE.exchange(),
                ShopRabbit.SHOP_ENABLE_DISABLE.routingKey(),
                new ShopsEnableDisableDTO()
                        .setEnable(isEnable)
                        .setReason(OperaReason.UPDATE)
                        .setShopIds(shopIds)
        );
    }

    @Override
    public void shopAudit(Long shopId, boolean pass, String reasonForRejection) {
        RedisUtil.doubleDeletion(
                () -> {
                    boolean success = shopService.lambdaUpdate()
                            .set(Shop::getStatus, pass ? ShopStatus.NORMAL : ShopStatus.REJECT)
                            .setSql(String.format("""
                                            extra = JSON_SET(
                                                    COALESCE(extra, '{}'),
                                                    '$.reasonForRejection', '%s',
                                                    '$.auditTime', '%s',
                                                    '$.operatorUserId','%s',
                                                    '$.operatorName','%s',
                                                    '$.operatorPhone','%s'
                                                )
                                            """, reasonForRejection, DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"),
                                    ISecurity.userMust().getId(), ISecurity.userMust().getNickname(),
                                    ISecurity.userMust().getMobile()
                            ))

                            .eq(Shop::getId, shopId)
                            .update();
                    SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
                },
                ShopConstants.SHOP_BASE_INFO,
                shopId
        );
        //如果通过 则 增加该店铺的 geo 位置
        if (pass) {
            Shop shop = shopService.lambdaQuery()
                    .select(Shop::getLocation)
                    .eq(BaseEntity::getId, shopId)
                    .ne(Shop::getShopMode, ShopMode.SUPPLIER)
                    .one();
            if (shop != null) {
                ShopGeo.add(
                        RedisUtil.getRedisTemplate(),
                        shopId, shop.getLocation()
                );
            }
        }
        globalExecutor.execute(
                () -> rabbitTemplate.convertAndSend(
                        ShopRabbit.SHOP_ENABLE_DISABLE.exchange(),
                        ShopRabbit.SHOP_ENABLE_DISABLE.routingKey(),
                        new ShopsEnableDisableDTO()
                                .setEnable(pass)
                                .setReason(OperaReason.UPDATE)
                                .setShopIds(Collections.singleton(shopId))
                )
        );


    }

    /**
     * 获取当日新增店铺数量
     *
     * @return 当日新增店铺数量
     */
    @Override
    public Long getTodayAddShopQuantity() {
        return shopService.getTodayAddShopQuantity();
    }

    /**
     * 获取店铺数量 group by status
     *
     * @return 店铺数量
     */
    @Override
    public Map<ShopStatus, Long> getShopQuantity() {
        List<ShopStatusQuantityVO> shopQuantity = shopService.getShopQuantity();
        return shopQuantity.stream().collect(Collectors.toMap(ShopStatusQuantityVO::getStatus, ShopStatusQuantityVO::getQuantity));
    }

    /**
     * 添加用户访问
     */
    @Override
    public void addShopVisitor() {
        Long userId = ISecurity.userMust().getId();
        LocalDate nowDate = LocalDate.now();
        ShopVisitor shopVisitor = shopVisitorService.lambdaQuery()
                .eq(ShopVisitor::getUserId, userId)
                .eq(ShopVisitor::getDate, nowDate).one();
        if (BeanUtil.isEmpty(shopVisitor)) {
            shopVisitor = new ShopVisitor();
            shopVisitor.setDate(nowDate).setUserId(userId).setUv(Long.valueOf(CommonPool.NUMBER_ONE));
            shopVisitorService.save(shopVisitor);
            return;
        }
        shopVisitor.setUv(shopVisitor.getUv() + CommonPool.NUMBER_ONE);
        shopVisitorService.updateById(shopVisitor);
    }

    /**
     * 获取供应商数量 by status
     *
     * @return 店铺数量
     */
    @Override
    public List<SupplierStatisticsVO> getSupplierQuantity() {
        return shopService.getSupplierQuantity();
    }

    @Override
    public Long getShopVisitorNum() {
        return shopVisitorService.lambdaQuery().eq(ShopVisitor::getDate, LocalDate.now()).count();
    }

}
