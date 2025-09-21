package com.medusa.gruul.overview.service.modules.base.service.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.overview.api.entity.DealRanking;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.api.enums.OverviewDealType;
import com.medusa.gruul.overview.api.model.PurchaseOrderReq;
import com.medusa.gruul.overview.api.model.ShopBalanceVO;
import com.medusa.gruul.overview.api.model.TradeDetailModel;
import com.medusa.gruul.overview.api.rpc.OverviewRpcService;
import com.medusa.gruul.overview.service.model.enums.OverviewError;
import com.medusa.gruul.overview.service.modules.base.service.ShopBalanceManagerService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawAccountService;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;
import com.medusa.gruul.overview.service.mp.base.service.IOverviewShopBalanceService;
import com.medusa.gruul.overview.service.mp.operate.service.IDealRankingService;
import com.medusa.gruul.overview.service.mp.statement.service.IOverviewStatementService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.medusa.gruul.overview.service.model.constants.OverviewStatementConstants.*;

/**
 * @author 张治保
 * date 2022/11/30
 */
@Service
@DubboService
@RequiredArgsConstructor
public class OverviewRpcServiceImpl implements OverviewRpcService {

    private final IOverviewShopBalanceService overviewShopBalanceService;
    private final ShopRpcService shopRpcService;
    private final IOverviewStatementService overviewStatementService;
    private final ShopBalanceManagerService shopBalanceManagerService;
    private final IDealRankingService dealRankingService;
    private final WithdrawAccountService withdrawAccountService;


    @Override
    public Map<Long, ShopBalanceVO> getShopBalanceMap(Set<Long> shopIds) {
        return overviewShopBalanceService.lambdaQuery()
                .in(OverviewShopBalance::getShopId, shopIds)
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                OverviewShopBalance::getShopId,
                                balance -> new ShopBalanceVO()
                                        .setTotal(balance.getTotal())
                                        .setUndrawn(balance.getUndrawn())
                                        .setUncompleted(balance.getUncompleted())
                        )
                );
    }

    /**
     * 根据采购单生成对账单
     *
     * @param req 采购订单对象,参见{@link PurchaseOrderReq}
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateStatementOfPurchase(PurchaseOrderReq req) {

        Set<Long> shopIds = new HashSet<>();
        shopIds.add(req.getBusinessShopId());
        shopIds.add(req.getSupplierShopId());
        List<ShopInfoVO> shopInfoList = shopRpcService.getShopInfoByShopIdList(shopIds);
        Map<Long, ShopInfoVO> shopInfoMap = shopInfoList.stream().collect(Collectors.toMap(ShopInfoVO::getId, Function.identity()));

        // 检查店铺信息
        ShopInfoVO businessShop = shopInfoMap.get(req.getBusinessShopId());
        ShopInfoVO supplierShop = shopInfoMap.get(req.getSupplierShopId());
        OverviewError.SHOP_NOT_EXIST.trueThrow(businessShop == null);
        OverviewError.SHOP_NOT_EXIST.trueThrow(supplierShop == null);

        // 检查对账单信息
        List<OverviewStatement> statementsOfOrder = this.overviewStatementService
                .listOverviewStatementByOrderNo(req.getOrderNo());
        OverviewError.PURCHASE_ORDER_ALREADY_EXIST.trueThrow(CollUtil.isNotEmpty(statementsOfOrder));

        // 暂时使用UUID作为MessageID
        String msgId = IdUtil.getSnowflakeNextIdStr();
        LocalDateTime tradeTime = LocalDateTime.now();

        // 支付金额
        AtomicReference<Long> payout = new AtomicReference<>(DEFAULT_AMOUNT);


        // 平台端服务费收入
        AtomicReference<Long> serviceIncome = new AtomicReference<>(DEFAULT_AMOUNT);

        req.getItems().forEach(item -> {
            payout.updateAndGet(v -> v + item.getAmount());
            serviceIncome.updateAndGet(v -> v + item.getAmount() * item.getRate() / 100);
        });


        // 生成对账单
        TradeDetailModel defaultTradeDetail = new TradeDetailModel();

        // 商家对账单保存供应商信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("supplierId", supplierShop.getId());
        List<OverviewStatement> statements = Arrays.asList(
                // 商家端采购商品支出
                new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(req.getBusinessShopId())
                        .setShopName(businessShop.getName())
                        .setTradeType(TransactionType.PURCHASE_ORDER)
                        .setAmount(payout.get())
                        .setChangeType(ChangeType.REDUCE)
                        .setOrderNo(req.getOrderNo())
                        .setTradeDetail(defaultTradeDetail)
                        .setShared(Boolean.FALSE)
                        .setTradeTime(tradeTime)
                        .setExtra(jsonObject)
                        .setTradeNo(msgId),
                // 商家端采购运费支出
                new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(req.getBusinessShopId())
                        .setShopName(businessShop.getName())
                        .setTradeType(TransactionType.PURCHASE_ORDER_FREIGHT)
                        .setAmount(req.getFreight())
                        .setChangeType(ChangeType.REDUCE)
                        .setOrderNo(req.getOrderNo())
                        .setTradeDetail(defaultTradeDetail)
                        .setShared(Boolean.FALSE)
                        .setTradeTime(tradeTime)
                        .setExtra(jsonObject)
                        .setTradeNo(msgId),

                // 供应商端采购商品收入
                new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(supplierShop.getId())
                        .setShopName(supplierShop.getName())
                        .setTradeType(TransactionType.PURCHASE_ORDER)
                        .setAmount(payout.get())
                        .setChangeType(ChangeType.INCREASE)
                        .setOrderNo(req.getOrderNo())
                        .setTradeDetail(defaultTradeDetail)
                        .setShared(Boolean.FALSE)
                        .setTradeTime(tradeTime)
                        .setTradeNo(msgId),

                // 供应商端采购商品运费收入
                new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(supplierShop.getId())
                        .setShopName(supplierShop.getName())
                        .setTradeType(TransactionType.PURCHASE_ORDER_FREIGHT)
                        .setAmount(req.getFreight())
                        .setChangeType(ChangeType.INCREASE)
                        .setOrderNo(req.getOrderNo())
                        .setTradeDetail(defaultTradeDetail)
                        .setShared(Boolean.FALSE)
                        .setTradeTime(tradeTime)
                        .setTradeNo(msgId),

                // 供应商端平台服务费支出
                new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(supplierShop.getId())
                        .setShopName(supplierShop.getName())
                        .setTradeType(TransactionType.PURCHASE_ORDER_SERVICE_CHARGE)
                        .setAmount(serviceIncome.get())
                        .setChangeType(ChangeType.REDUCE)
                        .setOrderNo(req.getOrderNo())
                        .setShared(Boolean.FALSE)
                        .setTradeDetail(defaultTradeDetail)
                        .setTradeTime(tradeTime)
                        .setTradeNo(msgId),

                // 平台端服务费收入
                new OverviewStatement()
                        .setMsgId(msgId)
                        .setShopId(PLATFORM_DEFAULT_ID)
                        .setShopName(PLATFORM_DEFAULT_SHOP_NAME)
                        .setTradeType(TransactionType.PURCHASE_ORDER_SERVICE_CHARGE)
                        .setAmount(serviceIncome.get())
                        .setChangeType(ChangeType.INCREASE)
                        .setOrderNo(req.getOrderNo())
                        .setTradeDetail(defaultTradeDetail)
                        .setShared(Boolean.FALSE)
                        .setTradeTime(tradeTime)
                        .setTradeNo(msgId)

        );

        // 保存对账单
        overviewStatementService.saveBatch(statements);

        // 扣除商家余额
        OverviewShopBalance overviewShopBalance = shopBalanceManagerService.getOrCreateShopBalance(req.getBusinessShopId());
        overviewShopBalance.setUndrawn(payout.get() + req.getFreight());
        overviewShopBalanceService.decrementUndrawnOfShop(overviewShopBalance);

        // 给供应商增加余额
        this.shopBalanceManagerService.uncompletedBalanceIncrement(req.getSupplierShopId(), (payout.get() - serviceIncome.get()) + req.getFreight());

        // 给平台增加服务费
        this.shopBalanceManagerService.uncompletedBalanceIncrement(PLATFORM_DEFAULT_ID, serviceIncome.get());

        // deal ranking
        List<DealRanking> dealRankingList = new ArrayList<>();
        req.getItems().forEach(orderItem -> {
            DealRanking dealRanking = new DealRanking();
            Long productId = orderItem.getProductId();
            Integer num = orderItem.getNum();
            Long shopId = req.getSupplierShopId();
            dealRanking.setOverviewDealType(OverviewDealType.PRODUCT)
                    .setProductId(productId)
                    .setShopId(shopId)
                    .setRealTradeVolume(Long.valueOf(num))
                    .setRealTradingVolume(orderItem.getAmount())
                    .setDate(LocalDate.now());
            dealRankingList.add(dealRanking);
        });
        this.dealRankingService.saveBatch(dealRankingList);
    }

    /**
     * 退还采购订单
     *
     * @param orderNo 采购订单订单号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPurchaseOrder(Long supplierShopId, String orderNo) {

        // 获取对账单list
        List<OverviewStatement> statementsOfOrder = this.overviewStatementService
                .lambdaQuery()
                .eq(OverviewStatement::getOrderNo, orderNo)
                .eq(OverviewStatement::getDeleted, Boolean.FALSE)
                .list();
        OverviewError.PURCHASE_ORDER_NOT_EXIST.trueThrow(CollectionUtils.isEmpty(statementsOfOrder));

        List<OverviewStatement> waitingInsertList = new ArrayList<>();

        // 计算供应商应扣除金额
        Long deductAmountOfSupplier = -statementsOfOrder
                .stream()
                .filter(e -> e.getShopId().equals(supplierShopId) && e.getChangeType().equals(ChangeType.INCREASE))
                .mapToLong(OverviewStatement::getAmount).sum();

        // 计算平台应扣除金额
        Long deductAmountOfPlatform = -statementsOfOrder
                .stream()
                .filter(e -> e.getShopId().equals(PLATFORM_DEFAULT_ID) && e.getChangeType().equals(ChangeType.INCREASE))
                .mapToLong(OverviewStatement::getAmount).sum();

        // 计算应退还的店铺金额
        Long refundAmountOfShop = statementsOfOrder
                .stream()
                .filter(e -> !e.getShopId().equals(PLATFORM_DEFAULT_ID) && !e.getShopId().equals(supplierShopId) && e.getChangeType().equals(ChangeType.REDUCE))
                .mapToLong(OverviewStatement::getAmount).sum();
        Optional<OverviewStatement> businessShopStatOptional = statementsOfOrder
                .stream()
                .filter(statement -> !statement.getShopId().equals(PLATFORM_DEFAULT_ID) && !statement.getShopId().equals(supplierShopId)).findFirst();

        OverviewError.SHOP_NOT_EXIST.trueThrow(businessShopStatOptional.isEmpty());


        statementsOfOrder.forEach(item -> {
            item.setDeleted(Boolean.TRUE);
            waitingInsertList.add(reverserAttributes(item));
        });
        SystemCode.DATA_UPDATE_FAILED.falseThrow(this.overviewStatementService.saveBatch(waitingInsertList));
        SystemCode.DATA_UPDATE_FAILED.falseThrow(this.overviewStatementService.removeBatchByIds(statementsOfOrder));

        // 减少供应商金额
        this.shopBalanceManagerService.uncompletedBalanceIncrement(supplierShopId, deductAmountOfSupplier);

        // 减少平台服务费
        this.shopBalanceManagerService.uncompletedBalanceIncrement(PLATFORM_DEFAULT_ID, deductAmountOfPlatform);

        // 退还店铺金额
        this.shopBalanceManagerService.undrawnBalanceIncrement(Boolean.FALSE, businessShopStatOptional.get().getShopId(), refundAmountOfShop);

    }

    @Override
    public Map<DrawType, JSONObject> getAccountsByUserId(Long userId) {
        return withdrawAccountService.getAccountsByUserId(userId);
    }

    /**
     * 反向生成对账单对象,将{@link OverviewStatement#changeType}反转
     *
     * @param item 对账单对象 {@link OverviewStatement}
     * @return {@link OverviewStatement}
     */
    private OverviewStatement reverserAttributes(OverviewStatement item) {
        return new OverviewStatement()
                .setMsgId(item.getMsgId())
                .setShopId(item.getShopId())
                .setShopName(item.getShopName())
                .setTradeType(item.getTradeType())
                .setAmount(item.getAmount())
                .setOrderNo(item.getOrderNo())
                .setTradeDetail(item.getTradeDetail())
                .setShared(item.getShared())
                .setChangeType(item.getChangeType().equals(ChangeType.REDUCE) ? ChangeType.INCREASE : ChangeType.REDUCE);
    }

}
