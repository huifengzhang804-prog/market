package com.medusa.gruul.overview.service.modules.withdraw.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.base.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.SortEnum;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import com.medusa.gruul.overview.api.enums.WithdrawType;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import com.medusa.gruul.overview.api.model.WithdrawOrderDTO;
import com.medusa.gruul.overview.service.model.OverviewConstant;
import com.medusa.gruul.overview.service.model.dto.PlatformWithdrawQueryDTO;
import com.medusa.gruul.overview.service.model.dto.ShopWithdrawDTO;
import com.medusa.gruul.overview.service.model.dto.ShopWithdrawQueryDTO;
import com.medusa.gruul.overview.service.model.dto.WithdrawAuditDTO;
import com.medusa.gruul.overview.service.model.dto.WithdrawQuery;
import com.medusa.gruul.overview.service.model.dto.WithdrawRemarkDTO;
import com.medusa.gruul.overview.service.model.enums.OverviewError;
import com.medusa.gruul.overview.service.model.param.WithdrawDistributorStatisticParam;
import com.medusa.gruul.overview.service.modules.base.service.OverviewBaseService;
import com.medusa.gruul.overview.service.modules.base.service.ShopBalanceManagerService;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;
import com.medusa.gruul.overview.service.mp.withdraw.service.IOverviewWithdrawService;
import com.medusa.gruul.overview.service.task.ShopSettlementExportTask;
import com.medusa.gruul.overview.service.task.SupplierSettlementExportTask;
import com.medusa.gruul.overview.service.task.WithdrawOrderExportTask;
import com.medusa.gruul.overview.service.util.OverviewUtil;
import com.medusa.gruul.payment.api.enums.TransferEnum;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.entity.ShopBankAccount;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张治保 date 2022/11/21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {

    private final RabbitTemplate rabbitTemplate;
    private final ShopRpcService shopRpcService;
    private final ShopBalanceManagerService shopBalanceManagerService;
    private final OverviewBaseService overviewBaseService;
    private final PaymentRpcService paymentRpcService;
    private final IOverviewWithdrawService withdrawOrderService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final Executor globalExecutor;


    private final UaaRpcService uaaRpcService;


    private final IDataExportRecordManageService dataExportRecordManageService;





    private WithdrawService withdrawService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void newWithdraw(Long shopId, ShopWithdrawDTO withdraw) {
        SecureUser<Object> user = ISecurity.userMust();
        Shop shop = shopRpcService.getShopAndShopBankInfo(shopId)
                .getOrElseThrow(OverviewError.SHOP_NOT_EXIST::exception);
        ShopBankAccount shopBankAccount = Option.of(shop.getBankAccount())
                .getOrElseThrow(OverviewError.SHOP_DRAW_NOT_EXIST::exception);
        //扣减 可提现余额
        Long amount = withdraw.getAmount();
        shopBalanceManagerService.undrawnBalanceDecrement(shopId, amount);
        //生成提现工单
        OwnerType ownerType = withdraw.getType() == null ? OwnerType.SHOP : withdraw.getType();
        withdrawService.createOrder(
                UUID.randomUUID().toString(),
                new WithdrawOrderDTO()
                        .setApplyUserId(user.getId())
//                        .setApplyUserName(user.getNickname())
                        .setApplyUserPhone(user.getMobile())
                        .setSourceType(WithdrawSourceType.SHOP)
                        .setOwnerType(ownerType)
                        .setOwnerId(shopId)
                        .setName(shop.getName())
                        .setAvatar(shop.getLogo())
                        .setContact(shop.getContractNumber())
                        .setDrawType(
                                new DrawTypeModel()
                                        .setType(WithdrawType.BANK_CARD)
                                        .setName(shopBankAccount.getPayee())
                                        .setBank(shopBankAccount.getOpenAccountBank())
                                        .setCardNo(shopBankAccount.getBankAccount())
                                        .setAmount(amount)
                        )
        );
    }

    @Override
    @Redisson(value = OverviewConstant.WITHDRAW_MSG_LOCK_KEY, key = "#msgId")
    public void createOrder(String msgId, WithdrawOrderDTO order) {
        if (withdrawOrderService.lambdaQuery().eq(OverviewWithdraw::getMsgId, msgId).exists()) {
            return;
        }
        OwnerType ownerType = order.getOwnerType();
        Long ownerId = order.getOwnerId();
        String name = order.getName();
        String avatar = order.getAvatar();
        OverviewWithdraw overviewWithdraw = new OverviewWithdraw()
                .setApplyUserId(order.getApplyUserId())
                .setApplyUserPhone(order.getApplyUserPhone())
                .setMsgId(msgId)
                .setSourceType(order.getSourceType())
                .setOffline(Boolean.FALSE)
                .setNo(RedisUtil.no(OverviewConstant.WITHDRAW_NO_PREFIX, OverviewConstant.WITHDRAW_NO_KEY))
                .setStatus(WithdrawOrderStatus.APPLYING)
                .setOwnerType(ownerType)
                .setOwnerId(ownerId)
                .setContract(order.getContact());
        overviewWithdraw.setDrawType(order.getDrawType());
        boolean success = withdrawOrderService.save(overviewWithdraw);
        if (!success) {
            log.error("保存提现工单失败");
        }
        switch (ownerType) {
            case SHOP:
            case SUPPLIER:
                overviewBaseService.checkOrSaveShop(ownerId, name, avatar);
                return;
            case REBATE:
            case DISTRIBUTOR:
                overviewBaseService.checkOrSaveConsumer(ownerId, name, avatar);
                return;
            default:
                break;
        }
    }


    @Override
    public IPage<OverviewWithdraw> orderPage(PlatformWithdrawQueryDTO query) {
        IPage<OverviewWithdraw> result = withdrawOrderService.platformOrderQuery(query);
        List<OverviewWithdraw> records = result.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return result;
        }
        for (OverviewWithdraw record : records) {
            if (OwnerType.REBATE.equals(record.getOwnerType())||OwnerType.DISTRIBUTOR.equals(record.getOwnerType())) {
                record.setContract(record.getApplyUserPhone());
            }
        }
        //审核用户ids
        Set<Long> auditUserIds = records.stream().map(OverviewWithdraw::getAuditUserId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(auditUserIds)) {
            Map<Long, UserInfoVO> userInfoVOMap = uaaRpcService.getUserDataBatchByUserIds(auditUserIds);

            records=records.stream().map(x->{
                UserInfoVO userDataVO = userInfoVOMap.get(x.getAuditUserId());
                if (Objects.nonNull(userDataVO)) {
                    x.setAuditUserName(StringUtils.isEmpty(userDataVO.getNickname())?
                            userDataVO.getUsername():userDataVO.getNickname());

                }
                return x;
            }).toList();
            result.setRecords(records);
        }
        return result;
    }

    @Override
    @Redisson(value = OverviewConstant.WITHDRAW_ORDER_UPDATE_LOCK_KEY, key = "#orderNo")
    @Transactional(rollbackFor = Exception.class)
    public void orderAuditByNo(String orderNo, WithdrawAuditDTO audit) {
        audit.validParam();
        OverviewWithdraw order = withdrawOrderService.lambdaQuery()
                .eq(OverviewWithdraw::getNo, orderNo)
                .eq(OverviewWithdraw::getStatus, WithdrawOrderStatus.APPLYING)
                .one();
        SystemCode.DATA_NOT_EXIST.trueThrow(order == null);
        //审核是否通过
        boolean pass = audit.getPass();
        if (pass) {
            audit.validParam(order.getDrawType().getType());
        }
        String reason = audit.getReason();
        Boolean offline = audit.getOffline();
        WithdrawOrderStatus targetStatus;
        //线上交易走支付转账
        if (pass && !offline) {
            targetStatus= WithdrawOrderStatus.SUCCESS;
            TransferResult transfer= paymentRpcService.transfer(OverviewUtil.withdraw2Transfer(order));
            //微信转账存在中间状态
            TransferEnum status = transfer.getStatus();
            if (Objects.nonNull(status)) {
                targetStatus=switch (status){
                    case WX_PROCESSING, ALI_PROCESSING -> WithdrawOrderStatus.PROCESSING;
                    case WX_SUCCESS, ALI_SUCCESS -> WithdrawOrderStatus.SUCCESS;
                    case WX_CLOSED, ALI_FAIL -> WithdrawOrderStatus.CLOSED;
                };
            }
            withdrawOrderService.lambdaUpdate()
                    .eq(OverviewWithdraw::getNo, orderNo)
                    .eq(OverviewWithdraw::getStatus, WithdrawOrderStatus.APPLYING)
                    .set(OverviewWithdraw::getStatus, targetStatus)
                    .set(OverviewWithdraw::getTradeNo, transfer.getTradNo())
                    .set(OverviewWithdraw::getOffline,Boolean.FALSE)
                    .set(OverviewWithdraw::getTradeTime, transfer.getTradeTime())
                    .set(OverviewWithdraw::getAuditTime, LocalDateTime.now())
                    .set(OverviewWithdraw::getAuditUserId,ISecurity.userMust().getId())
                    .set(OverviewWithdraw::getAuditUserPhone,ISecurity.userMust().getMobile())
                    .set(OverviewWithdraw::getUpdateTime, LocalDateTime.now())
                    .update();
        }else {
            //线下
            targetStatus=pass ? WithdrawOrderStatus.SUCCESS : WithdrawOrderStatus.FORBIDDEN;
            LocalDateTime now = LocalDateTime.now();
            boolean success = withdrawOrderService.lambdaUpdate()
                    .eq(OverviewWithdraw::getNo, orderNo)
                    .eq(OverviewWithdraw::getStatus, WithdrawOrderStatus.APPLYING)
                    .set(OverviewWithdraw::getStatus, targetStatus)
                    .set(OverviewWithdraw::getOffline, offline)
                    .set(!pass, OverviewWithdraw::getReason, reason)
                    .set(OverviewWithdraw::getUpdateTime, now)
                    .set(OverviewWithdraw::getTradeTime, now)
                    .set(OverviewWithdraw::getAuditTime, LocalDateTime.now())
                    .set(OverviewWithdraw::getAuditUserId,ISecurity.userMust().getId())
                    .set(OverviewWithdraw::getAuditUserPhone,ISecurity.userMust().getMobile())
                    .set(OverviewWithdraw::getPaymentVoucher,audit.getPaymentVoucher())
                    .update();
            SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        }
        OwnerType ownerType = order.getOwnerType();
        //店铺提现 被拒，退换店铺待提现余额
        if ((OwnerType.SHOP == ownerType || OwnerType.SUPPLIER == ownerType) && !pass) {
            shopBalanceManagerService.undrawnBalanceIncrement(Boolean.FALSE, order.getOwnerId(),
                    order.getDrawType().getAmount());
            return;
        }
        //广播 提现工单处理结果
        OverviewRabbit rabbit =
                pass ? OverviewRabbit.WITHDRAW_ORDER_SUCCESS : OverviewRabbit.WITHDRAW_ORDER_FORBIDDEN;
        rabbitTemplate.convertAndSend(
                rabbit.exchange(),
                rabbit.routingKey() + ownerType.name(),
                order.setStatus(targetStatus).setReason(reason)
        );
    }


    @Override
    public IPage<OverviewWithdraw> distributeWithdrawPage(Long userId, WithdrawQuery query) {
        WithdrawQuery page = withdrawOrderService.lambdaQuery()
                .eq(OverviewWithdraw::getSourceType, query.getWithdrawSourceType())
                .eq(OverviewWithdraw::getOwnerType, query.getOwnerType())
                .eq(OverviewWithdraw::getOwnerId, userId)
                .eq(query.getStatus() != null, OverviewWithdraw::getStatus, query.getStatus())
                .ge(query.getStartTime() != null, OverviewWithdraw::getCreateTime, query.getStartTime())
                .le(query.getEndTime() != null, OverviewWithdraw::getCreateTime, query.getEndTime())
                .orderByDesc(BaseEntity::getCreateTime)
                .page(query);
        page.setStatistic(
                withdrawOrderService.distributorStatistics(
                        new WithdrawDistributorStatisticParam()
                                .setOwnerId(userId)
                                .setOwnerType(query.getOwnerType())
                                .setWithdrawSourceType(query.getWithdrawSourceType())
                                .setStartTime(query.getStartTime())
                                .setEndTime(query.getEndTime())
                )
        );
        return page;
    }

    @Override
    public IPage<OverviewWithdraw> shopWithdrawPage(Long shopId, ShopWithdrawQueryDTO query) {
        boolean noCondition = StrUtil.isEmpty(query.getNo()) || StrUtil.isBlank(query.getNo());
        LambdaQueryChainWrapper<OverviewWithdraw> queryChainWrapper = withdrawOrderService.lambdaQuery()
            .in(CollectionUtil.isNotEmpty(query.getExportIds()), OverviewWithdraw::getId,
                query.getExportIds())
            .eq(OverviewWithdraw::getSourceType, WithdrawSourceType.SHOP)
            .eq(OverviewWithdraw::getOwnerType,
                query.getType() == null ? OwnerType.SHOP : query.getType())
            .eq(OverviewWithdraw::getOwnerId, shopId)
            .eq(Objects.nonNull(query.getStatus()), OverviewWithdraw::getStatus, query.getStatus())
            .ge(query.getStartDate() != null, OverviewWithdraw::getCreateTime,
                Option.of(query.getStartDate()).map(LocalDate::atStartOfDay).getOrNull())
            .le(query.getEndDate() != null, OverviewWithdraw::getCreateTime,
                Option.of(query.getEndDate()).map(date -> date.atTime(LocalTime.MAX)).getOrNull())
            .like(StringUtils.isNotEmpty(query.getApplyUserPhone()), OverviewWithdraw::getApplyUserPhone, query.getApplyUserPhone())
            .like(!noCondition, OverviewWithdraw::getNo, query.getNo());
      if (SortEnum.APPLY_TIME_ASC.equals(query.getSortEnum())) {
        queryChainWrapper.orderByAsc(OverviewWithdraw::getCreateTime);
      }
      if (SortEnum.APPLY_TIME_DESC.equals(query.getSortEnum())) {
        queryChainWrapper.orderByDesc(OverviewWithdraw::getCreateTime);
      }
        if (SortEnum.WITHDRAW_AMOUNT_ASC.equals(query.getSortEnum())) {

            queryChainWrapper.apply("order by JSON_EXTRACT(draw_type, '$.amount') ");
        }
        if (SortEnum.WITHDRAW_AMOUNT_DESC.equals(query.getSortEnum())) {
            queryChainWrapper.apply("order by JSON_EXTRACT(draw_type, '$.amount') desc ");
        }
        ShopWithdrawQueryDTO page = queryChainWrapper.page(query);
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return page;
        }
        Set<Long> applyUserIds = page.getRecords().stream().map(OverviewWithdraw::getApplyUserId).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userInfoVOMap = uaaRpcService.getUserDataBatchByUserIds(applyUserIds);
        page.getRecords().forEach(record -> {
            record.setApplyUserNickName(userInfoVOMap.getOrDefault(record.getApplyUserId(),new UserInfoVO()).getNickname());
        });
        return page;

    }

    @Override
    public void remarkBatch(WithdrawRemarkDTO withdrawRemark) {
        withdrawOrderService.lambdaUpdate()
                .in(OverviewWithdraw::getNo, withdrawRemark.getNos())
                .set(OverviewWithdraw::getRemark, withdrawRemark.getRemark())
                .update();
    }

    @Override
    public Long export(ShopWithdrawQueryDTO query) {
        SecureUser user = ISecurity.userMust();
        Long recodeId = dataExportRecordManageService.saveDataExportRecord(user.getId(),
                user.getShopId(),
                user.getMobile(),
                OwnerType.SUPPLIER.equals(query.getType())?ExportDataType.SUPPLIER_SETTLEMENT:ExportDataType.STORE_SETTLEMENT);
        Runnable task = null;
        if (OwnerType.SUPPLIER.equals(query.getType())) {
            task = new SupplierSettlementExportTask(withdrawService, user.getShopId(),
                    query, recodeId, dataExportRecordManageService, pigeonChatStatisticsRpcService,
                    shopBalanceManagerService,uaaRpcService);
        }
        if (OwnerType.SHOP.equals(query.getType())) {
            //兼容原来查询的方法
            query.setType(null);
            //店铺结算导出
            task = new ShopSettlementExportTask(pigeonChatStatisticsRpcService, recodeId,
                    dataExportRecordManageService,
                    shopBalanceManagerService,uaaRpcService, user.getShopId(), withdrawService, query);

        }
        globalExecutor.execute(task);

        return recodeId;
    }

    @Override
    public Long exportWithDrawOrder(PlatformWithdrawQueryDTO query) {
        SecureUser user = ISecurity.userMust();
        Long recodeId = dataExportRecordManageService.saveDataExportRecord(user.getId(),
                user.getShopId(),
                user.getMobile(), ExportDataType.WITHDRAWAL_ORDER);
        WithdrawOrderExportTask withdrawOrderExportTask = new WithdrawOrderExportTask(recodeId, this
                , dataExportRecordManageService, pigeonChatStatisticsRpcService, query);
        globalExecutor.execute(withdrawOrderExportTask);
        return recodeId;
    }

    @Override
    public void updateWithdrawOrderStatus(String orderNo, String status,String closeReason) {
        OverviewWithdraw one = withdrawOrderService.lambdaQuery()
                .eq(OverviewWithdraw::getNo, orderNo)
                .eq(OverviewWithdraw::getStatus,WithdrawOrderStatus.PROCESSING)
                .one();
        if (Objects.isNull(one)) {
            return;
        }
        //商家转账批次单据到终态后（批次完成或者批次关闭，对应批次状态batch_status的值为FINISHED和CLOSED）
        WithdrawOrderStatus updateStatus=switch (status){
            case "FINISHED"->WithdrawOrderStatus.SUCCESS;
            case "CLOSED"->WithdrawOrderStatus.CLOSED;
            default -> throw new IllegalStateException("未知类型: " + status);
        };
        withdrawOrderService.lambdaUpdate()
                .eq(OverviewWithdraw::getNo, orderNo)
                .eq(OverviewWithdraw::getStatus,WithdrawOrderStatus.PROCESSING)
                .set(OverviewWithdraw::getStatus,updateStatus)
                .set(WithdrawOrderStatus.CLOSED.equals(updateStatus),
                        OverviewWithdraw::getFailReason,closeReason)//关闭原因
                .set(OverviewWithdraw::getTradeTime,LocalDateTime.now())
                .update();


    }

    @Autowired
    public void setWithdrawService(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }
}
