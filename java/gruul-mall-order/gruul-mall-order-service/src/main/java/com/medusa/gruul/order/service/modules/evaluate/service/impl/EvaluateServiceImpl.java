package com.medusa.gruul.order.service.modules.evaluate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.service.model.dto.*;
import com.medusa.gruul.order.service.model.enums.EvaluateQueryType;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.ProductEvaluateOverviewVO;
import com.medusa.gruul.order.service.modules.afs.service.OrderAfsService;
import com.medusa.gruul.order.service.modules.evaluate.model.EvaluateDetailKey;
import com.medusa.gruul.order.service.modules.evaluate.service.EvaluateService;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/9/5
 */
@Service
@RequiredArgsConstructor
public class EvaluateServiceImpl implements EvaluateService {

    private final IOrderService orderService;
    private final UaaRpcService uaaRpcService;
    private final OrderAfsService orderAfsService;
    private final IOrderDiscountService discountService;
    private final OrderRabbitService orderRabbitService;
    private final IOrderPaymentService orderPaymentService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderEvaluateService orderEvaluateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = OrderConstant.ORDER_PACKAGE_LOCK, key = "#orderEvaluate.orderNo+':'+#orderEvaluate.shopId")
    public void evaluate(boolean isSystem, Long userId, OrderEvaluateDTO orderEvaluate) {
        String orderNo = orderEvaluate.getOrderNo();
        Long shopId = orderEvaluate.getShopId();
        Map<ProductSkuKey, List<OrderEvaluateItemDTO>> evaluateItemMap = orderEvaluate.getItems()
                .stream()
                .collect(Collectors.groupingBy(OrderEvaluateItemDTO::getKey));
        Set<ProductSkuKey> productSkuKeys = evaluateItemMap.keySet();

        //检查订单
        Order order = orderService.lambdaQuery()
                .select(Order::getStatus, Order::getDistributionMode, Order::getExtra, Order::getCreateTime)
                .eq(Order::getNo, orderNo)
                .eq(Order::getBuyerId, userId)
                .one();
        if (order == null || order.getStatus().isClosed()) {
            if (isSystem) {
                return;
            }
            OrderError.ORDER_NOT_EXIST.trueThrow(order == null);
            throw OrderError.ORDER_CLOSED.exception();
        }
        //检查订单项
        List<ShopOrderItem> items = evaluateItems(orderNo, shopId, productSkuKeys);
        if (CollUtil.isEmpty(items)) {
            if (isSystem) {
                return;
            }
            throw OrderError.ORDER_NOT_EXIST.exception();
        }
        //根据商品 id 分组
        Map<ProductSkuKey, List<ShopOrderItem>> productIdItemsMap = items.stream()
                .collect(
                        Collectors.groupingBy(item -> new ProductSkuKey().setProductId(item.getProductId()).setSkuId(item.getSkuId()))
                );
        Tuple2<List<OrderEvaluate>, List<ShopOrderItem>> orderEvaluatesTuples = this.generateEvaluateAndSave(
                isSystem, userId,
                orderNo, shopId,
                productIdItemsMap,
                evaluateItemMap,
                order.getCreateTime()
        );
        PackageStatus status = isSystem ? PackageStatus.SYSTEM_COMMENTED_COMPLETED : PackageStatus.BUYER_COMMENTED_COMPLETED;
        List<OrderEvaluate> orderEvaluates = orderEvaluatesTuples._1();
        List<ShopOrderItem> evaluatedItems = orderEvaluatesTuples._2();
        //更新订单商品项状态为已评价状态
        this.updateEvaluateItems(orderNo, shopId, evaluatedItems, status);
        //关闭可关闭售后
        orderAfsService.closeAfsIfClosable(isSystem, evaluatedItems, (isSystem ? "系统" : "买家") + "评价关闭售后");
        // 获取订单包裹优惠项
        List<OrderDiscount> orderDiscounts = TenantShop.disable(() -> discountService.orderDiscounts(
                        new OrderDetailQueryDTO()
                                .setOrderNo(orderNo)
                                .setShopId(shopId)
                                .setUsePackage(Boolean.FALSE)
                                .setShopOrderItemIds(
                                        evaluatedItems.stream()
                                                .map(BaseEntity::getId)
                                                .toList()
                                )
                )
        );
        //订单完成发送mq
        IManualTransaction.afterCommit(
                () -> {
                    OrderPayment orderPayment = orderPaymentService.lambdaQuery()
                            .select(OrderPayment::getTransactions)
                            .eq(OrderPayment::getOrderNo, orderNo)
                            .one();
                    orderRabbitService.sendOrderEvaluate(
                            new OrderCompletedDTO()
                                    .setDistributionMode(order.getDistributionMode())
                                    .setUserId(userId)
                                    .setOrderNo(orderNo)
                                    .setShopId(shopId)
                                    .setExtra(order.getExtra())
                                    .setOrderEvaluates(orderEvaluates)
                                    .setTotalAmount(this.getTotalDealAmountOfItems(evaluatedItems))
                                    .setShopOrderItems(evaluatedItems)
                                    .setOrderDiscounts(orderDiscounts)
                                    .setTransaction(orderPayment.transaction(shopId))
                                    .setHaveUncompletedItem(
                                            TenantShop.disable(
                                                    () -> shopOrderItemService.lambdaQuery()
                                                            .eq(ShopOrderItem::getOrderNo, orderNo)
                                                            .eq(ShopOrderItem::getShopId, shopId)
                                                            .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                                                            .notIn(ShopOrderItem::getPackageStatus, PackageStatus.SYSTEM_COMMENTED_COMPLETED, PackageStatus.BUYER_COMMENTED_COMPLETED)
                                                            .exists()
                                            )
                                    )
                    );
                }
        );
    }


    private void updateEvaluateItems(String orderNo, Long shopId, List<ShopOrderItem> evaluatedItems, PackageStatus packageStatus) {
        boolean success = TenantShop.disable(() -> {
            LambdaUpdateChainWrapper<ShopOrderItem> updateWrapper = shopOrderItemService.lambdaUpdate()
                    .set(ShopOrderItem::getAfsStatus, AfsStatus.NONE)
                    .set(ShopOrderItem::getPackageStatus, packageStatus)
                    .set(ShopOrderItem::getUpdateTime, LocalDateTime.now())
                    .eq(ShopOrderItem::getOrderNo, orderNo)
                    .eq(ShopOrderItem::getShopId, shopId)
                    .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                    .in(ShopOrderItem::getId, evaluatedItems.stream().map(BaseEntity::getId).collect(Collectors.toSet()));
            return updateWrapper.update();
        });
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
    }

    private List<ShopOrderItem> evaluateItems(String orderNo, Long shopId, Set<ProductSkuKey> productSkuKeys) {
        LambdaQueryChainWrapper<ShopOrderItem> itemQueryWrapper = shopOrderItemService.lambdaQuery()
                .eq(ShopOrderItem::getOrderNo, orderNo)
                .eq(ShopOrderItem::getShopId, shopId)
                .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                .apply(
                        SqlHelper.inSql(
                                List.of("product_id", "sku_id"),
                                productSkuKeys,
                                List.of(
                                        ProductSkuKey::getProductId,
                                        ProductSkuKey::getSkuId
                                )
                        )
                );
        return TenantShop.disable(() -> itemQueryWrapper.list());
    }


    private Long getTotalDealAmountOfItems(List<ShopOrderItem> shopOrderItems) {
        if (CollUtil.isEmpty(shopOrderItems)) {
            return 0L;
        }
        return shopOrderItems.stream()
                .map(item -> item.getNum() * item.getDealPrice() + item.getFreightPrice() + item.getFixPrice())
                .reduce(0L, Long::sum);

    }

    /**
     * 生成评价数据并保存
     *
     * @param isSystem        是否是系统评价
     * @param userId          用户id
     * @param orderNo         订单号
     * @param shopId          店铺id
     * @param itemMap         商品项
     * @param evaluateMap     评价项
     * @param orderCreateTime 订单创建时间
     * @return 1 评价数据 2 评价的商品项
     */
    private Tuple2<List<OrderEvaluate>, List<ShopOrderItem>> generateEvaluateAndSave(boolean isSystem, Long userId, String orderNo, Long shopId, Map<ProductSkuKey, List<ShopOrderItem>> itemMap, Map<ProductSkuKey, List<OrderEvaluateItemDTO>> evaluateMap, LocalDateTime orderCreateTime) {
        UserInfoVO userInfo = uaaRpcService.getUserDataByUserId(userId)
                .getOrElseThrow(OrderError.USER_INFO_NOT_COMPLETE::exception);
        LocalDateTime now = LocalDateTime.now();
        //评价的商品项
        List<ShopOrderItem> evaluatedItems = new ArrayList<>();
        //生成评论数据
        List<OrderEvaluate> evaluates = evaluateMap.entrySet()
                .stream()
                .flatMap(entry -> {
                    ProductSkuKey productSkuKey = entry.getKey();
                    Map<String, List<ShopOrderItem>> specsItemsMap = itemMap.get(productSkuKey)
                            .stream()
                            .collect(Collectors.groupingBy(item -> OrderEvaluateItemDTO.specsToString(item.getSpecs())));
                    return entry.getValue()
                            .stream()
                            .map(
                                    evaluateItem -> {
                                        List<ShopOrderItem> shopOrderItems = specsItemsMap.get(OrderEvaluateItemDTO.specsToString(evaluateItem.getSpecs()));
                                        //检查是否存在
                                        if (CollUtil.isEmpty(shopOrderItems)) {
                                            if (isSystem) {
                                                return null;
                                            }
                                            throw OrderError.ORDER_NOT_EXIST.exception();
                                        }
                                        //检查是否都处于待评价状态
                                        OrderError.ORDER_CANNOT_COMMENT.trueThrow(shopOrderItems.stream().anyMatch(item -> !item.getPackageStatus().isCanComment()));
                                        //检查售后状态是否可关闭shopOrderItems = {ArrayList@19932}  size = 3
                                        OrderError.AFS_CANNOT_CLOSE.trueThrow(shopOrderItems.stream().anyMatch(item -> !item.getAfsStatus().isCanChangePackageStatus()));
                                        evaluatedItems.addAll(shopOrderItems);
                                        ShopOrderItem item = shopOrderItems.get(0);
                                        OrderEvaluate evaluate = new OrderEvaluate()
                                                .setOrderNo(orderNo)
                                                .setUserId(userId)
                                                .setNickname(ISecurity.generateNickName(userInfo.getNickname(), userId).getOrNull())
                                                .setAvatar(userInfo.getAvatar())
                                                .setShopId(shopId)
                                                .setProductId(productSkuKey.getProductId())
                                                .setProductType(item.getProductType())
                                                .setSellType(item.getSellType())
                                                .setName(item.getProductName())
                                                .setSkuId(productSkuKey.getSkuId())
                                                .setImage(item.getImage())
                                                .setSpecs(item.getSpecs())
                                                .setRate(evaluateItem.getRate())
                                                .setOrderCreateTime(orderCreateTime)
                                                .setComment(StrUtil.blankToDefault(evaluateItem.getComment(), null))
                                                .setMedias(CollUtil.defaultIfEmpty(evaluateItem.getMedias(), List.of()))
                                                .setIsExcellent(Boolean.FALSE);
                                        evaluate.setCreateTime(now);
                                        return evaluate;
                                    }
                            ).filter(Objects::nonNull);

                }).toList();
        orderEvaluateService.saveBatch(evaluates);
        return Tuple.of(evaluates, evaluatedItems);
    }

    @Override
    public IPage<OrderEvaluate> trueEvaluatePage(EvaluateQueryDTO evaluateQuery) {
        boolean isUser = ISecurity.anyRole(Roles.USER);
        SecureUser<?> user = ISecurity.userMust();
        EvaluateQueryType type = evaluateQuery.getType();
        return TenantShop.disable(
                () -> orderEvaluateService.lambdaQuery()
                        .eq(isUser, OrderEvaluate::getUserId, user.getId())
                        .eq(!isUser, OrderEvaluate::getShopId, user.getShopId())
                        .apply(type == EvaluateQueryType.IMAGE, "JSON_LENGTH(medias) != 0")
                        .isNotNull(type == EvaluateQueryType.CONTENT, OrderEvaluate::getComment)
                        .like(StrUtil.isNotBlank(evaluateQuery.getName()), OrderEvaluate::getName, evaluateQuery.getName())
                        .like(StrUtil.isNotBlank(evaluateQuery.getNickname()), OrderEvaluate::getNickname, evaluateQuery.getNickname())
                        .eq(evaluateQuery.getRate() != null, OrderEvaluate::getRate, evaluateQuery.getRate())
                        .ge(evaluateQuery.getStartTime() != null, BaseEntity::getCreateTime, Option.of(evaluateQuery.getStartTime()).map(LocalDate::atStartOfDay).getOrNull())
                        .le(evaluateQuery.getEndTime() != null, BaseEntity::getCreateTime, Option.of(evaluateQuery.getEndTime()).map(endTime -> endTime.atTime(LocalTime.MAX)).getOrNull())
                        .like(StrUtil.isNotBlank(evaluateQuery.getKeywords()), OrderEvaluate::getName, evaluateQuery.getKeywords())
                        .or(StrUtil.isNotBlank(evaluateQuery.getKeywords()), consumer -> consumer
                                .like(OrderEvaluate::getOrderNo, evaluateQuery.getKeywords())
                                .eq(isUser, OrderEvaluate::getUserId, user.getId())
                                .eq(!isUser, OrderEvaluate::getShopId, user.getShopId())
                                .isNotNull(type != null, type == EvaluateQueryType.CONTENT ? (OrderEvaluate::getComment) : (OrderEvaluate::getMedias))
                                .like(StrUtil.isNotBlank(evaluateQuery.getName()), OrderEvaluate::getName, evaluateQuery.getName())
                                .like(StrUtil.isNotBlank(evaluateQuery.getNickname()), OrderEvaluate::getNickname, evaluateQuery.getNickname())
                                .eq(evaluateQuery.getRate() != null, OrderEvaluate::getRate, evaluateQuery.getRate())
                                .ge(evaluateQuery.getStartTime() != null, BaseEntity::getCreateTime, Option.of(evaluateQuery.getStartTime()).map(LocalDate::atStartOfDay).getOrNull())
                                .le(evaluateQuery.getEndTime() != null, BaseEntity::getCreateTime, Option.of(evaluateQuery.getEndTime()).map(endTime -> endTime.atTime(LocalTime.MAX)).getOrNull())
                        )
                        .orderByDesc(BaseEntity::getCreateTime)
                        .page(evaluateQuery)

        );
    }

    @Override
    public ProductEvaluateOverviewVO productEvaluateOverview(ProductEvaluateQueryDTO productEvaluateQuery) {
        ISecurity.match()
                .ifUser(secureUser -> productEvaluateQuery.setUserId(secureUser.getId()));
        Long userId = productEvaluateQuery.getUserId();
        ProductEvaluateOverviewVO overview = TenantShop.disable(() -> orderEvaluateService.productEvaluateOverview(productEvaluateQuery));
        if (0 != overview.getTotalCount()) {
            overview.setEvaluate(
                    TenantShop.disable(
                            () -> orderEvaluateService.lambdaQuery()
                                    .eq(OrderEvaluate::getShopId, productEvaluateQuery.getShopId())
                                    .eq(OrderEvaluate::getProductId, productEvaluateQuery.getProductId())
                                    .and(
                                            wrapper -> wrapper.eq(OrderEvaluate::getIsExcellent, Boolean.TRUE)
                                                    .or(
                                                            userId != null, orWrapper -> orWrapper.eq(OrderEvaluate::getUserId, userId)
                                                    )
                                    )
                                    .orderByAsc(OrderEvaluate::getCreateTime)
                                    .last(SqlHelper.SQL_LIMIT_1)
                                    .one()
                    )
            );
        }
        return overview;
    }

    @Override
    public IPage<OrderEvaluate> productEvaluatePage(ProductEvaluateQueryDTO productEvaluateQuery) {
        productEvaluateQuery.setUserId(null);
        ISecurity.match()
                .ifUser(secureUser -> productEvaluateQuery.setUserId(secureUser.getId()));
        Long userId = productEvaluateQuery.getUserId();
        EvaluateQueryType type = productEvaluateQuery.getType();

        return TenantShop.disable(
                () -> orderEvaluateService.lambdaQuery()
                        .eq(OrderEvaluate::getShopId, productEvaluateQuery.getShopId())
                        .eq(OrderEvaluate::getProductId, productEvaluateQuery.getProductId())
                        .isNotNull(type != null, type == EvaluateQueryType.CONTENT ? (OrderEvaluate::getComment) : (OrderEvaluate::getMedias))
                        .apply(type == EvaluateQueryType.IMAGE, " JSON_LENGTH (medias) > 0")
                        .and(
                                wrapper -> wrapper.eq(OrderEvaluate::getIsExcellent, Boolean.TRUE)
                                        .or(
                                                userId != null, orWrapper -> orWrapper.eq(OrderEvaluate::getUserId, userId)
                                        )
                        )
                        .orderByAsc(OrderEvaluate::getCreateTime)
                        .page(productEvaluateQuery)
        );
    }

    @Override
    public void setEvaluateExcellent(List<Long> evaluateIds, Boolean isExcellent) {
        orderEvaluateService.lambdaUpdate()
                .set(OrderEvaluate::getIsExcellent, isExcellent)
                .in(OrderEvaluate::getId, evaluateIds)
                .eq(OrderEvaluate::getShopId, ISecurity.userMust().getShopId())
                .update();
    }

    @Override
    public void replyEvaluate(Long evaluateId, String reply) {
        Long shopId = ISecurity.userMust().getShopId();
        boolean exists = orderEvaluateService.lambdaQuery()
                .eq(OrderEvaluate::getId, evaluateId)
                .eq(OrderEvaluate::getShopId, shopId)
                .exists();
        if (!exists) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        orderEvaluateService.lambdaUpdate()
                .set(OrderEvaluate::getShopReply, reply)
                .set(OrderEvaluate::getReplyTime, LocalDateTime.now())
                .eq(OrderEvaluate::getId, evaluateId)
                .eq(OrderEvaluate::getShopId, shopId)
                .update();
    }

    @Override
    public OrderEvaluate getOrderEvaluate(String orderNo, EvaluateDetailKey key) {
        OrderEvaluate evaluate = TenantShop.disable(
                () -> {
                    LambdaQueryChainWrapper<OrderEvaluate> queryChainWrapper = orderEvaluateService.lambdaQuery()
                            .eq(OrderEvaluate::getOrderNo, orderNo)
                            .eq(OrderEvaluate::getShopId, key.getShopId())
                            .eq(OrderEvaluate::getProductId, key.getProductId())
                            .eq(OrderEvaluate::getSkuId, key.getSkuId());
                    if (key.getSpecs() != null) {
                        queryChainWrapper.apply("specs = CAST({0} AS JSON)", JSON.toJSONString(key.getSpecs()));
                    }
                    return queryChainWrapper
                            .last(SqlHelper.SQL_LIMIT_1)
                            .one();
                }
        );
        return Option.of(evaluate)
                .getOrElse(OrderEvaluate::new);

    }
}
