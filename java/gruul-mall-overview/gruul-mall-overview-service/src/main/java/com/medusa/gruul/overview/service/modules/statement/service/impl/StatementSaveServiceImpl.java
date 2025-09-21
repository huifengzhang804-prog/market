package com.medusa.gruul.overview.service.modules.statement.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.model.base.ShopOrderKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.enums.StatementRabbit;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.common.model.message.OverviewStatementDTO;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.global.model.enums.ReceiverType;
import com.medusa.gruul.global.model.o.MessageKey;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.OrderDiscountItem;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.overview.api.model.OverViewStatementResp;
import com.medusa.gruul.overview.api.model.StatementCreateDTO;
import com.medusa.gruul.overview.api.model.TradeDetailModel;
import com.medusa.gruul.overview.service.addon.OverviewStatementSupporter;
import com.medusa.gruul.overview.service.model.OverviewConstant;
import com.medusa.gruul.overview.service.model.constants.OverviewStatementConstants;
import com.medusa.gruul.overview.service.modules.base.service.OverviewBaseService;
import com.medusa.gruul.overview.service.modules.base.service.ShopBalanceManagerService;
import com.medusa.gruul.overview.service.modules.statement.service.StatementSaveService;
import com.medusa.gruul.overview.service.mp.statement.service.IOverviewStatementService;
import com.medusa.gruul.payment.api.enums.SharingStatus;
import com.medusa.gruul.payment.api.model.param.ProfitSharingParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingUnfreezeParam;
import com.medusa.gruul.payment.api.model.param.ReceiverKey;
import com.medusa.gruul.payment.api.model.param.SharingResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author WuDi
 * date 2022/10/18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatementSaveServiceImpl implements StatementSaveService {


    private final ShopRpcService shopRpcService;
    private final PaymentRpcService paymentRpcService;
    private final OverviewBaseService overviewBaseService;
    private final IOverviewStatementService statementService;
    private final OverviewStatementSupporter overviewStatementSupporter;
    private final ShopBalanceManagerService shopBalanceManagerService;

    private final RabbitTemplate rabbitTemplate;

    /**
     * 保存对账单
     *
     * @param overviewStatement 对账单DTO
     */
    @Override
    public void saveStatement(String msgId, OverviewStatementDTO overviewStatement) {
        this.messageIdem(
                msgId,
                () -> {
                    Long shopId = overviewStatement.getShopId();
                    overviewBaseService.checkOrSaveShop(shopId);
                    OverviewStatement statement = new OverviewStatement()
                            .setMsgId(msgId)
                            .setShopId(shopId)
                            .setTradeType(overviewStatement.getTransactionType())
                            .setTradeNo(overviewStatement.getTransactionSerialNumber())
                            .setTradeTime(overviewStatement.getTransactionTime())
                            .setOrderNo(overviewStatement.getOrderNo())
                            .setTradeDetail(new TradeDetailModel())
                            .setUserId(overviewStatement.getUserId())
                            .setChangeType(overviewStatement.getChangeType())
                            .setAmount(ObjectUtil.defaultIfNull(overviewStatement.getAccount(), (long) CommonPool.NUMBER_ZERO))
                            .setShared(Boolean.FALSE);
                    boolean success = statementService.save(statement);
                    if (!success) {
                        log.error("对账单保存失败：{}", statement.toString());
                    }
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createStatement(String msgId, StatementCreateDTO statementCreate) {
        this.messageIdem(
                msgId,
                () -> {
                    Long shopId = statementCreate.getShopId();
                    overviewBaseService.checkOrSaveShop(shopId);
                    //扣除店铺待提现余额
                    Long amount = statementCreate.getAmount();
                    shopBalanceManagerService.undrawnBalanceDecrement(shopId, amount);
                    //
                    OverviewStatement statement = new OverviewStatement()
                            .setMsgId(msgId)
                            .setShopId(shopId)
                            .setTradeType(statementCreate.getTradeType())
                            .setTradeNo(statementCreate.getTradeNo())
                            .setTradeTime(statementCreate.getTradeTime())
                            .setOrderNo(statementCreate.getOrderNo())
                            .setTradeDetail(new TradeDetailModel())
                            .setUserId(statementCreate.getUserId())
                            .setChangeType(statementCreate.getChangeType())
                            .setAmount(amount)
                            .setShared(Boolean.FALSE);
                    boolean success = statementService.save(statement);
                    if (!success) {
                        log.error("对账单保存失败：{}", statement.toString());
                    }

                }
        );
    }


    /**
     * 订单完成
     *
     * @param orderCompleted 订单完成数据
     * @param msgId          mq消息id 用于幂等性校验
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = OverviewConstant.OVERVIEW_STATEMENT_NEWLY, key = "#msgId")
    public void orderAccomplish(OrderCompletedDTO orderCompleted, String msgId) {
        boolean exists = statementService.lambdaQuery().eq(OverviewStatement::getMsgId, msgId).exists();
        if (exists) {
            return;
        }
        Long userId = orderCompleted.getUserId();
        Long shopId = orderCompleted.getShopId();
        LocalDateTime tradeTime = LocalDateTime.now();
        TradeDetailModel detail = new TradeDetailModel()
                .setPackageId(orderCompleted.getShopOrderItems().get(CommonPool.NUMBER_ZERO).getPackageId());

        Set<Long> shopInfoIds = orderCompleted.getShopOrderItems()
                .stream()
                //代销商品才需要处理供应商分润
                .filter(item -> SellType.CONSIGNMENT == item.getSellType() && item.getSupplierId() != null)
                .map(ShopOrderItem::getSupplierId)
                .collect(Collectors.toSet());
        shopInfoIds.add(shopId);
        // 获取包含供应商信息的shopInfoVO
        List<ShopInfoVO> shopInfos = shopRpcService.getShopInfoByShopIdList(shopInfoIds);
        Map<Long, ShopInfoVO> shopInfoMap = shopInfos
                .stream()
                .collect(Collectors.toMap(
                        ShopInfoVO::getId, Function.identity()
                ));

        /*处理订单优惠项 (该处优惠项只针对平台 (店铺 or 供应商))
         */
        List<OverviewStatement> statements = new ArrayList<>(orderDiscountsStatements(msgId, shopInfoMap.get(shopId), orderCompleted, detail, tradeTime));

        AtomicLong platformDiscountAmountReference = new AtomicLong(OverviewStatementConstants.DEFAULT_AMOUNT);
        AtomicLong freightPriceReference = new AtomicLong(OverviewStatementConstants.DEFAULT_AMOUNT);
        AtomicLong consignmentProductTotalMoneyReference = new AtomicLong(OverviewStatementConstants.DEFAULT_AMOUNT);
        // 金额处理
        updateAmountReference(orderCompleted, platformDiscountAmountReference, freightPriceReference, consignmentProductTotalMoneyReference);


        Transaction transaction = orderCompleted.transaction();
        //是否可分账
        boolean profitSharing = BooleanUtil.isTrue(transaction.getProfitSharing());
        //分销账单
        Tuple2<List<OverviewStatement>, List<ProfitSharingParam.Receiver>> distributorStatementsTuple = distributorStatements(profitSharing, msgId, orderCompleted);
        statements.addAll(distributorStatementsTuple._1());


        //处理系统服务费
        Tuple2<List<OverviewStatement>, List<ProfitSharingParam.Receiver>> serviceChargeStatementsTuple = serviceCharge(
                profitSharing, msgId, shopInfoMap, orderCompleted, detail, tradeTime,
                distributorStatementsTuple._1().stream().map(OverviewStatement::getAmount).reduce(0L, Long::sum)
                , platformDiscountAmountReference.get(), freightPriceReference.get()
        );

        statements.addAll(serviceChargeStatementsTuple._1());
        List<OverviewStatement> overviewStatements = serviceChargeStatementsTuple._1();

        /* 服务费映射
         */
        Map<Long, OverviewStatement> serviceChargeStatementMap = overviewStatements.stream()
                .filter(overviewStatement -> overviewStatement.getShopId() != 0
                        && overviewStatement.getTradeType() == TransactionType.SYSTEM_SERVICE)
                .collect(Collectors.toMap(
                        OverviewStatement::getShopId,
                        Function.identity()
                ));

        /* 订单交易额处理
         */
        //计需要支付给分销的总额
        Long distributeAmount = distributorStatementsTuple._1().stream().map(OverviewStatement::getAmount).reduce(0L, Long::sum);
        orderGMVDispose(shopInfoMap, orderCompleted, shopId, freightPriceReference,
                msgId, transaction, tradeTime, detail, userId, profitSharing, statements, serviceChargeStatementMap,
                platformDiscountAmountReference, consignmentProductTotalMoneyReference,distributeAmount);


        log.warn("对账单数据".concat(JSON.toJSONString(statements)));
        //保存数据
        statementService.saveBatch(statements);
        rabbitTemplate.convertAndSend(
                StatementRabbit.PLATFORM_SERVICE_CHARGE.exchange(),
                StatementRabbit.PLATFORM_SERVICE_CHARGE.routingKey(),
                orderCompleted.setExtra(
                        getServiceChange(serviceChargeStatementsTuple, orderCompleted)
                )
        );
        //如果未开启分账 则调整店铺余额
        if (!profitSharing) {
            return;
        }
        //执行分账
        List<ProfitSharingParam.Receiver> receivers = new ArrayList<>(distributorStatementsTuple._2());
        receivers.addAll(serviceChargeStatementsTuple._2());
        this.todoSharing(msgId, orderCompleted, receivers);

    }


    /**
     * 订单交易额处理
     *
     * @param shopInfoMap                           店铺vo Map
     * @param orderCompleted                        订单完成数据
     * @param shopId                                店铺id
     * @param freightPriceReference                 运费
     * @param msgId                                 msgId
     * @param transaction                           交易流水信息
     * @param tradeTime                             交易时间
     * @param detail                                交易详情
     * @param userId                                用户id
     * @param profitSharing                         是否分账
     * @param statements                            对账单List
     * @param serviceChargeStatementMap             系统服务费Map
     * @param platformDiscountAmountReference       平台抵扣金额
     * @param consignmentProductTotalMoneyReference 代销商品金额
     */
    private void orderGMVDispose(Map<Long, ShopInfoVO> shopInfoMap, OrderCompletedDTO orderCompleted, Long shopId, AtomicLong freightPriceReference,
                                 String msgId, Transaction transaction, LocalDateTime tradeTime, TradeDetailModel detail, Long userId, Boolean profitSharing,
                                 List<OverviewStatement> statements, Map<Long, OverviewStatement> serviceChargeStatementMap, AtomicLong platformDiscountAmountReference,
                                 AtomicLong consignmentProductTotalMoneyReference, Long distributeAmount) {
        // 处理店铺和供应商订单交易额
        for (Map.Entry<Long, ShopInfoVO> entry : shopInfoMap.entrySet()) {
            Long currentShopId = entry.getKey();
            ShopInfoVO shopInfo = entry.getValue();
            long supplierConsignmentProductMoney = orderCompleted.getShopOrderItems()
                    .stream()
                    .filter(shopOrderItem -> shopOrderItem.getSellType() == SellType.CONSIGNMENT && shopOrderItem.getSupplierId().equals(currentShopId))
                    .mapToLong(shopOrderItem -> shopOrderItem.getExtra().getSkuPracticalSalePrice() * shopOrderItem.getNum())
                    .sum();

            // 如果完成的订单是代销订单 店铺应该生成俩笔明细  用户 -> 店铺 收入  店铺 -> 供应商 支出
            long shopAmount = (currentShopId.equals(shopId)) ? orderCompleted.getTotalAmount() - freightPriceReference.get()- distributeAmount: supplierConsignmentProductMoney;
            OverviewStatement statement = new OverviewStatement()
                    .setMsgId(msgId)
                    .setShopId(currentShopId)
                    .setTradeType(TransactionType.ORDER_PAID)
                    .setTradeNo(transaction.getTransactionId())
                    .setTradeTime(tradeTime)
                    .setOrderNo(orderCompleted.getOrderNo())
                    .setTradeDetail(detail)
                    .setUserId(userId)
                    .setChangeType(ChangeType.INCREASE)
                    .setAmount(shopAmount)
                    .setShared(profitSharing)
                    .setSharingTarget(ReceiverType.SHOP)
                    .setShopName(shopInfo.getName());
            statements.add(statement);
            if (distributeAmount>0) {
                OverviewStatement distributeStatement = new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(currentShopId)
                        .setTradeType(TransactionType.DISTRIBUTE)
                        .setTradeNo(transaction.getTransactionId())
                        .setTradeTime(tradeTime)
                        .setOrderNo(orderCompleted.getOrderNo())
                        .setTradeDetail(detail)
                        .setUserId(userId)
                        .setChangeType(ChangeType.REDUCE)
                        .setAmount(distributeAmount)
                        .setShared(Boolean.FALSE)
                        .setSharingTarget(ReceiverType.SHOP)
                        .setShopName(shopInfo.getName());
                statements.add(distributeStatement);

            }

            if ((!currentShopId.equals(shopId)) && supplierConsignmentProductMoney > 0) {
                // 当生成了供应商对账单 对应生成店铺支出
                OverviewStatement consignmentDisburseStatement = new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(shopId)
                        .setTradeType(TransactionType.CONSIGNMENT_DISBURSE)
                        .setTradeNo(transaction.getTransactionId())
                        .setTradeTime(tradeTime)
                        .setOrderNo(orderCompleted.getOrderNo())
                        .setTradeDetail(detail)
                        .setUserId(userId)
                        .setChangeType(ChangeType.REDUCE)
                        .setAmount(supplierConsignmentProductMoney)
                        .setShared(profitSharing)
                        .setSharingTarget(ReceiverType.SHOP)
                        .setShopName(shopInfoMap.get(shopId).getName());
                statements.add(consignmentDisburseStatement);
            }
            long serviceChargeAmount = serviceChargeStatementMap.getOrDefault(currentShopId, new OverviewStatement().setAmount(0L)).getAmount();
            shopBalanceManagerService.orderCompleted(currentShopId, currentShopId.equals(shopId) ?
                    (shopAmount + platformDiscountAmountReference.get() - serviceChargeAmount - consignmentProductTotalMoneyReference.get()-distributeAmount) :
                    shopAmount - serviceChargeAmount);
        }

    }

    private JSONObject getServiceChange(Tuple2<List<OverviewStatement>, List<ProfitSharingParam.Receiver>> serviceChargeStatementsTuple, OrderCompletedDTO orderCompleted) {
        Map<String, Object> map = new HashMap<>(CommonPool.NUMBER_ONE);
        List<OverviewStatement> statements = serviceChargeStatementsTuple._1;

        Long serviceChargeAmount = 0L;
        if (CollUtil.isNotEmpty(statements)) {
            Optional<OverviewStatement> matchingStatement = statements.stream()
                    .filter(statement -> statement.getShopId().equals(orderCompleted.getShopId())
                            && statement.getTradeType() == TransactionType.SYSTEM_SERVICE)
                    .findFirst();
            if (matchingStatement.isPresent()) {
                serviceChargeAmount = matchingStatement.get().getAmount();
            }
        }

        map.put("serviceCharge", serviceChargeAmount);
        return Option.of(orderCompleted.getExtra())
                .peek(extra -> extra.putAll(map))
                .getOrElse(() -> {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.putAll(map);
                            return jsonObject;
                        }
                );
    }

    /**
     * 分销数据对账单与分账接受者
     *
     * @param profitSharing  是否分账账单
     * @param msgId          消息id
     * @param orderCompleted 订单完成数据
     * @return 分销数据对账单
     */
    private Tuple2<List<OverviewStatement>, List<ProfitSharingParam.Receiver>> distributorStatements(boolean profitSharing, String msgId, OrderCompletedDTO orderCompleted) {
        OverViewStatementResp statementResp = overviewStatementSupporter.createStatements(orderCompleted);
        if (statementResp == null) {
            return Tuple.of(Collections.emptyList(), Collections.emptyList());
        }
        //分账接收方
        List<ProfitSharingParam.Receiver> receivers = new ArrayList<>();
        List<OverviewStatement> distributorStatements = new ArrayList<>();
        String orderNo = orderCompleted.getOrderNo();
        Long shopId = orderCompleted.getShopId();
        statementResp.getUserIdAmountMap()
                .forEach(
                        (userId, amount) -> {
                            if (amount <= 0) {
                                return;
                            }
                            if (profitSharing) {
                                receivers.add(
                                        new ProfitSharingParam.Receiver()
                                                .setType(ReceiverType.DISTRIBUTOR)
                                                .setAccountId(userId)
                                                .setAmount(amount)
                                                .setDescription("分销佣金提现")
                                );
                            }

                            distributorStatements.add(
                                    new OverviewStatement()
                                            .setMsgId(msgId)
                                            .setShopId(shopId)
                                            .setTradeType(statementResp.getType())
                                            .setTradeTime(LocalDateTime.now())
                                            .setOrderNo(orderNo)
                                            .setTradeDetail(new TradeDetailModel())
                                            .setUserId(userId)
                                            .setChangeType(ChangeType.REDUCE)
                                            .setAmount(amount)
                                            .setShared(profitSharing)
                                            .setSharingTarget(ReceiverType.DISTRIBUTOR)
                            );
                        }
                );
        return Tuple.of(distributorStatements, receivers);
    }

    /**
     * 分账处理
     */
    private void todoSharing(String sharingNo, OrderCompletedDTO orderCompleted, List<ProfitSharingParam.Receiver> receivers) {
        String orderNo = orderCompleted.getOrderNo();
        Long shopId = orderCompleted.getShopId();
        //是否进行解冻
        boolean unfreeze = BooleanUtil.isFalse(orderCompleted.getHaveUncompletedItem());
        //包含分账接收方 则进行分账
        if (CollUtil.isNotEmpty(receivers)) {
            paymentRpcService.profitSharing(
                    new ProfitSharingParam()
                            .setSharingNo(sharingNo)
                            .setShopOrderKey(new ShopOrderKey().setOrderNo(orderNo).setShopId(shopId))
                            //没有其他未完成订单则直接解冻
                            .setUnfreeze(unfreeze)
                            .setReceivers(receivers)
                            .setNotifyKey(
                                    new MessageKey().setExchange(OverviewRabbit.SHARING_STATUS_SYNC.exchange()).setRoutingKey(OverviewRabbit.SHARING_STATUS_SYNC.routingKey())
                            )

            );
            return;
        }
        //如果平台服务费为0 并且没有其他未完成订单  则直接解冻
        if (unfreeze) {
            paymentRpcService.profitSharingUnfreeze(
                    new ProfitSharingUnfreezeParam()
                            .setSharingNo(sharingNo)
                            .setShopOrderKey(new ShopOrderKey().setOrderNo(orderNo).setShopId(shopId))
            );
        }
    }

    @Override
    public void statementFreightSave(OrderPackageKeyDTO orderPackageKey, String msgId) {
        Long shopId = orderPackageKey.getShopId();
        // 获取店铺分润信息
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        //已支付运费
        String orderNo = orderPackageKey.getOrderNo();
        Long userId = orderPackageKey.getBuyerId();
        Transaction transaction = orderPackageKey.transaction();
        TradeDetailModel tradeDetailModel = new TradeDetailModel().setPackageId(orderPackageKey.getPackageId());
        LocalDateTime tradeTime = LocalDateTime.now();
        boolean enableSharing = BooleanUtil.isTrue(transaction.getProfitSharing());
        //没有优惠运费 则 增加店铺订单运费 对账单和收入
        Long discountFreight = orderPackageKey.getDiscountFreight();
        Long paidFreight = orderPackageKey.getPaidFreight();
        if (discountFreight <= CommonPool.NUMBER_ZERO) {
            statementService.save(
                    new OverviewStatement()
                            .setMsgId(msgId)
                            .setShopId(shopId)
                            .setTradeType(TransactionType.ORDER_FREIGHT)
                            .setTradeNo(transaction.getTransactionId())
                            .setTradeTime(tradeTime)
                            .setOrderNo(orderNo)
                            .setTradeDetail(tradeDetailModel)
                            .setUserId(userId)
                            .setChangeType(ChangeType.INCREASE)
                            .setAmount(paidFreight)
                            .setShopName(shopInfo.getName())
                            .setShared(Boolean.FALSE)
            );
            shopBalanceManagerService.orderCompleted(shopId, enableSharing ? 0L : paidFreight);
            return;
        }
        statementService.saveBatch(
                List.of(
                        new OverviewStatement()
                                .setMsgId(msgId)
                                .setShopId(0L)
                                .setTradeType(TransactionType.MEMBER_LOGISTICS_DISCOUNT)
                                .setTradeNo(transaction.getTransactionId())
                                .setTradeTime(tradeTime)
                                .setOrderNo(orderNo)
                                .setTradeDetail(tradeDetailModel)
                                .setUserId(userId)
                                .setChangeType(ChangeType.REDUCE)
                                .setAmount(discountFreight)
                                .setShopName(shopInfo.getName())
                                .setShared(Boolean.FALSE),
                        new OverviewStatement()
                                .setMsgId(msgId)
                                .setShopId(shopId)
                                .setTradeType(TransactionType.MEMBER_LOGISTICS_DISCOUNT)
                                .setTradeNo(transaction.getTransactionId())
                                .setTradeTime(tradeTime)
                                .setOrderNo(orderNo)
                                .setTradeDetail(tradeDetailModel)
                                .setUserId(userId)
                                .setChangeType(ChangeType.INCREASE)
                                .setAmount(discountFreight)
                                .setShopName(shopInfo.getName())
                                .setShared(Boolean.FALSE)

                )
        );
        //店铺余额变动
        shopBalanceManagerService.orderCompleted(shopId, enableSharing ? discountFreight : (discountFreight + paidFreight));
    }

    @Override
    public void orderAfsClosed(String msgId, OrderInfo orderClosed) {
        if (OrderCloseType.AFS != orderClosed.getCloseType()) {
            return;
        }
        if (BooleanUtil.isTrue(orderClosed.getHaveUncompletedItem())) {
            return;
        }
        if (BooleanUtil.isFalse(orderClosed.transaction().getProfitSharing())) {
            return;
        }
        //最后一个并且是分账订单 直接解冻
        paymentRpcService.profitSharingUnfreeze(
                new ProfitSharingUnfreezeParam()
                        .setSharingNo(msgId)
                        .setShopOrderKey(new ShopOrderKey().setOrderNo(orderClosed.getOrderNo()).setShopId(orderClosed.getShopId()))
        );
    }

    @Override
    public void sharingStatusSync(SharingResult result) {
        if (SharingStatus.ERROR == result.getStatus()) {
            statementService.lambdaUpdate()
                    .eq(OverviewStatement::getMsgId, result.getSharingNo())
                    .eq(OverviewStatement::getShared, Boolean.TRUE)
                    .isNotNull(OverviewStatement::getSharingTarget)
                    .setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("sharing", JSON.toJSONString(result.getError()))))
                    .update();
            return;
        }
        if (CollUtil.isEmpty(result.getReceiverResultMap())) {
            return;
        }
        List<OverviewStatement> statements = statementService.lambdaQuery()
                .eq(OverviewStatement::getMsgId, result.getSharingNo())
                .eq(OverviewStatement::getShared, Boolean.TRUE)
                .isNotNull(OverviewStatement::getSharingTarget)
                .list();
        if (CollUtil.isEmpty(statements)) {
            return;
        }
        statements.forEach(statement -> {
            ReceiverType receiverType = statement.getSharingTarget();
            SharingResult.ReceiverResult receiverResult = result.getReceiverResultMap()
                    .get(new ReceiverKey().setReceiverType(receiverType).setAccountId(accountId(statement)));
            if (receiverResult == null) {
                return;
            }
            JSONObject extra = statement.getExtra();
            if (extra == null) {
                extra = new JSONObject();
            }
            statement.setExtra(extra.set("sharing", receiverResult));
        });
        statementService.updateBatchById(statements);
    }

    private Long accountId(OverviewStatement statement) {
        return switch (statement.getSharingTarget()) {
            case DISTRIBUTOR -> statement.getUserId();
            case SHOP -> statement.getShopId();
            default -> null;
        };
    }


    private void messageIdem(String msgId, Runnable task) {
        if (statementService.lambdaQuery().eq(OverviewStatement::getMsgId, msgId).exists()) {
            return;
        }
        task.run();
    }


    private void updateAmountReference(OrderCompletedDTO orderCompleted, AtomicLong platformDiscountAmountReference, AtomicLong freightPriceReference, AtomicLong consignmentProductTotalMoneyReference) {
        // 平台优惠金额
        long platformDiscountAmount = orderCompleted.getOrderDiscounts()
                .stream()
                .filter(orderDiscount -> orderDiscount.getSourceType().isPlatform())
                .flatMap(orderDiscount -> orderDiscount.getDiscountItems().stream())
                .mapToLong(OrderDiscountItem::getDiscountAmount).sum();

        // 运费

        long freightPrice = orderCompleted.getShopOrderItems()
                .stream()
                .mapToLong(ShopOrderItem::getFreightPrice)
                .sum();

        // 代销商品总价格
        long consignmentProductTotalMoney = orderCompleted.getShopOrderItems()
                .stream()
                .filter(shopOrderItem -> shopOrderItem.getSellType() == SellType.CONSIGNMENT)
                .mapToLong(shopOrderItem -> shopOrderItem.getExtra().getSkuPracticalSalePrice() * shopOrderItem.getNum())
                .sum();

        platformDiscountAmountReference.accumulateAndGet(platformDiscountAmount, Long::sum);
        freightPriceReference.accumulateAndGet(freightPrice, Long::sum);
        consignmentProductTotalMoneyReference.accumulateAndGet(consignmentProductTotalMoney, Long::sum);
    }


    /**
     * 处理订单优惠项
     */
    private List<OverviewStatement> orderDiscountsStatements(String msgId, ShopInfoVO shopInfo, OrderCompletedDTO orderCompleted, TradeDetailModel tradeDetailModel, LocalDateTime tradeTime) {
        List<OrderDiscount> orderDiscounts = orderCompleted.getOrderDiscounts();
        if (CollUtil.isEmpty(orderDiscounts)) {
            return List.of();
        }
        String orderNo = orderCompleted.getOrderNo();
        Long shopId = orderCompleted.getShopId();
        Long userId = orderCompleted.getUserId();
        String shopName = shopInfo == null ? StrUtil.EMPTY : shopInfo.getName();
        String transactionId = orderCompleted.transaction().getTransactionId();

        return orderDiscounts.stream()
                .filter(orderDiscount -> orderDiscount.getSourceType().isPlatform())
                .flatMap(
                        orderDiscount -> {
                            long discountAmount = orderDiscount.getDiscountItems().stream().mapToLong(OrderDiscountItem::getDiscountAmount).sum();
                            if (discountAmount <= 0) {
                                return Stream.empty();
                            }
                            TransactionType transactionType = switch (orderDiscount.getSourceType()) {
                                case PLATFORM_COUPON -> TransactionType.PLATFORM_DISCOUNT_COUPON;
                                case MEMBER_DEDUCTION -> TransactionType.MEMBER_DISCOUNT;
                                default ->
                                    // 处理其他情况的默认值
                                        TransactionType.REBATE_DEDUCTION;
                            };
                            return Stream.of(
                                    //平台对账单
                                    new OverviewStatement()
                                            .setMsgId(msgId)
                                            .setShopId(0L)
                                            .setTradeType(transactionType)
                                            .setTradeNo(transactionId)
                                            .setTradeTime(tradeTime)
                                            .setOrderNo(orderNo)
                                            .setTradeDetail(tradeDetailModel)
                                            .setUserId(userId)
                                            .setChangeType(ChangeType.REDUCE)
                                            .setAmount(discountAmount)
                                            .setShopName(shopName)
                                            .setShared(Boolean.FALSE),
                                    //店铺对账单
                                    new OverviewStatement()
                                            .setMsgId(msgId)
                                            .setShopId(shopId)
                                            .setTradeType(transactionType)
                                            .setTradeNo(transactionId)
                                            .setTradeTime(tradeTime)
                                            .setOrderNo(orderNo)
                                            .setTradeDetail(tradeDetailModel)
                                            .setUserId(userId)
                                            .setChangeType(ChangeType.INCREASE)
                                            .setAmount(discountAmount)
                                            .setShopName(shopName)
                                            .setShared(Boolean.FALSE)

                            );
                        }
                ).collect(Collectors.toList());

    }


    /**
     * 处理系统服务费
     */
    private Tuple2<List<OverviewStatement>, List<ProfitSharingParam.Receiver>> serviceCharge(
            boolean profitSharing, String msgId, Map<Long, ShopInfoVO> shopInfoMap,
            OrderCompletedDTO orderCompleted, TradeDetailModel tradeDetailModel,
            LocalDateTime tradeTime, Long distributorAmount, long platformDiscountAmount, long freightPrice
    ) {
        // 订单成交价
        Long totalAmount = orderCompleted.getTotalAmount();
        Long shopId = orderCompleted.getShopId();
        //扣除分销佣金后小于1元 不处理
        if (totalAmount - distributorAmount + platformDiscountAmount < CommonPool.UNIT_CONVERSION_TEN_THOUSAND) {
            log.debug("订单成交价小于1元，不进行平台抽佣，当前订单成交价为 " + orderCompleted.getTotalAmount());
            return Tuple.of(List.of(), List.of());
        }
        List<OverviewStatement> overviewStatements = new ArrayList<>();

        // 获取订单中的代销商品 原金额
        long consignmentProductMoney = orderCompleted.getShopOrderItems()
                .stream()
                .filter(shopOrderItem -> shopOrderItem.getSellType() == SellType.CONSIGNMENT)
                .mapToLong(shopOrderItem -> shopOrderItem.getExtra().getSkuPracticalSalePrice() * shopOrderItem.getNum() + shopOrderItem.getFixPrice())
                .reduce(0L, Long::sum);

        //记录平台服务费
        long serviceCharge;


        String transactionId = orderCompleted.transaction().getTransactionId();
        String orderNo = orderCompleted.getOrderNo();
        Long userId = orderCompleted.getUserId();

        for (Map.Entry<Long, ShopInfoVO> shopInfoEntry : shopInfoMap.entrySet()) {

            // 店铺抽佣
            if (shopInfoEntry.getKey().equals(shopId)) {
                ShopInfoVO shopInfo = shopInfoMap.get(shopId);
                ExtractionType extraction = shopInfo.getExtractionType();
                /*店铺的销售额抽佣
                 */
                if (ExtractionType.ORDER_SALES_EXTRACTION == extraction) {
                    Integer drawPercentage = shopInfo.getDrawPercentage();
                    // 订单成交价 - 分销佣金  - 代销商品金额 + 平台优惠
                    serviceCharge = new BigDecimal((totalAmount - freightPrice - distributorAmount - consignmentProductMoney + platformDiscountAmount) * drawPercentage)
                            .divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_HUNDRED), -2, RoundingMode.DOWN)
                            .longValue();
                } else {
                    //类目抽佣
                    AtomicLong remainDistributorAmount = new AtomicLong(distributorAmount);
                    Optional<Long> chargeOpt = orderCompleted.getShopOrderItems()
                            .stream()
                            .map(
                                    shopOrderItem -> {
                                        Integer ratio = shopOrderItem.getExtra().getProfitRate();
                                        if (ratio == null) {
                                            return 0L;
                                        }
                                        long currentTotalAmount = (shopOrderItem.getDealPrice() * shopOrderItem.getNum() + shopOrderItem.getFixPrice());
                                        if (shopOrderItem.getSellType() == SellType.CONSIGNMENT) {
                                            // 代销商品计算抽佣要扣除 代销商品金额
                                            currentTotalAmount = (shopOrderItem.getDealPrice() - shopOrderItem.getExtra().getSkuPracticalSalePrice()) * shopOrderItem.getNum() + shopOrderItem.getFixPrice();
                                        }
                                        long remain = remainDistributorAmount.get();
                                        long delta = Math.min(currentTotalAmount, remain);
                                        remainDistributorAmount.addAndGet(-delta);
                                        currentTotalAmount -= delta;
                                        if (currentTotalAmount <= 0) {
                                            return 0L;
                                        }
                                        return new BigDecimal(currentTotalAmount * ratio)
                                                .divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_HUNDRED), -CommonPool.NUMBER_TWO, RoundingMode.DOWN)
                                                .longValue();
                                    }
                            ).reduce(Long::sum);
                    serviceCharge = chargeOpt.orElse(0L);
                }
                JSONObject extra = new JSONObject();
                extra.set("totalProductServiceCharge", serviceCharge);
                //服务费收取的店铺id

                if (serviceCharge > 0L) {
                    //平台对账单
                    overviewStatements.add(new OverviewStatement()
                            .setMsgId(msgId)
                            .setShopId(0L)
                            .setTradeType(TransactionType.SYSTEM_SERVICE)
                            .setTradeNo(transactionId)
                            .setTradeTime(tradeTime)
                            .setOrderNo(orderNo)
                            .setTradeDetail(tradeDetailModel)
                            .setUserId(userId)
                            .setChangeType(ChangeType.INCREASE)
                            .setAmount(serviceCharge)
                            .setTargetShopId(shopInfoEntry.getValue().getId())
                            .setShopName(shopInfoEntry.getValue().getName())
                            .setExtra(extra)
                            .setShared(profitSharing)
                            .setSharingTarget(ReceiverType.PLATFORM));
                    //店铺对账单
                    overviewStatements.add(
                            new OverviewStatement()
                                    .setMsgId(msgId)
                                    .setShopId(shopInfoEntry.getKey())
                                    .setTradeType(TransactionType.SYSTEM_SERVICE)
                                    .setTradeNo(transactionId)
                                    .setTradeTime(tradeTime)
                                    .setOrderNo(orderNo)
                                    .setTradeDetail(tradeDetailModel)
                                    .setUserId(userId)
                                    .setChangeType(ChangeType.REDUCE)
                                    .setAmount(serviceCharge)
                                    .setShopName(shopInfoEntry.getValue().getName())
                                    .setExtra(extra)
                                    .setShared(profitSharing)
                                    .setSharingTarget(ReceiverType.PLATFORM));
                }
            } else {
                // 供应商抽佣
                ShopInfoVO supplierInfo = shopInfoEntry.getValue();
                Long supplierId = supplierInfo.getId();
                ExtractionType extractionType = supplierInfo.getExtractionType();
                long supplierConsignmentProductMoney = orderCompleted.getShopOrderItems().stream()
                        .filter(shopOrderItem -> supplierId.equals(shopOrderItem.getSupplierId()))
                        .mapToLong(shopOrderItem -> shopOrderItem.getExtra().getSkuPracticalSalePrice() * shopOrderItem.getNum())
                        .reduce(0L, Long::sum);

                if (ExtractionType.ORDER_SALES_EXTRACTION == extractionType) {
                    Integer drawPercentage = supplierInfo.getDrawPercentage();
                    serviceCharge = new BigDecimal(supplierConsignmentProductMoney * drawPercentage)
                            .divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_HUNDRED), -2, RoundingMode.DOWN)
                            .longValue();
                } else {
                    //类目抽佣
                    Optional<Long> chargeOpt = orderCompleted.getShopOrderItems()
                            .stream()
                            .filter(shopOrderItem -> supplierId.equals(shopOrderItem.getSupplierId()))
                            .map(
                                    shopOrderItem -> {
                                        Integer supplierProfitRate = shopOrderItem.getExtra().getSupplierProfitRate();
                                        if (supplierProfitRate == null) {
                                            return 0L;
                                        }
                                        long currentTotalAmount = (shopOrderItem.getExtra().getSkuPracticalSalePrice() * shopOrderItem.getNum());
                                        if (currentTotalAmount <= 0) {
                                            return 0L;
                                        }
                                        return new BigDecimal(currentTotalAmount * supplierProfitRate)
                                                .divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_HUNDRED), -CommonPool.NUMBER_TWO, RoundingMode.DOWN)
                                                .longValue();
                                    }
                            ).reduce(Long::sum);
                    serviceCharge = chargeOpt.orElse(0L);
                }
                JSONObject extra = new JSONObject().set("totalProductServiceCharge", serviceCharge);
                if (serviceCharge > 0L) {
                    //平台对账单
                    overviewStatements.add(new OverviewStatement()
                            .setMsgId(msgId)
                            .setShopId(0L)
                            .setTradeType(TransactionType.SYSTEM_SERVICE)
                            .setTradeNo(transactionId)
                            .setTradeTime(tradeTime)
                            .setOrderNo(orderNo)
                            .setTradeDetail(tradeDetailModel)
                            .setUserId(userId)
                            .setChangeType(ChangeType.INCREASE)
                            .setAmount(serviceCharge)
                            .setTargetShopId(shopInfoEntry.getValue().getId())
                            .setShopName(shopInfoEntry.getValue().getName())
                            .setExtra(extra)
                            .setShared(profitSharing)
                            .setSharingTarget(ReceiverType.PLATFORM));
                    //供应商对账单
                    overviewStatements.add(
                            new OverviewStatement()
                                    .setMsgId(msgId)
                                    .setShopId(shopInfoEntry.getKey())
                                    .setTradeType(TransactionType.SYSTEM_SERVICE)
                                    .setTradeNo(transactionId)
                                    .setTradeTime(tradeTime)
                                    .setOrderNo(orderNo)
                                    .setTradeDetail(tradeDetailModel)
                                    .setUserId(userId)
                                    .setChangeType(ChangeType.REDUCE)
                                    .setAmount(serviceCharge)
                                    .setShopName(shopInfoEntry.getValue().getName())
                                    .setExtra(extra)
                                    .setShared(profitSharing)
                                    .setSharingTarget(ReceiverType.PLATFORM));
                }
            }

        }
        return Tuple.of(CollUtil.isEmpty(overviewStatements) ? List.of() : overviewStatements, List.of());
    }

}


