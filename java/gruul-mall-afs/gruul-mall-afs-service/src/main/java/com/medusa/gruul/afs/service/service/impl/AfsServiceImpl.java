package com.medusa.gruul.afs.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.medusa.gruul.afs.api.constant.AfsConst;
import com.medusa.gruul.afs.api.enums.AfsRabbit;
import com.medusa.gruul.afs.api.enums.AfsType;
import com.medusa.gruul.afs.api.enums.RefundType;
import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import com.medusa.gruul.afs.service.model.bo.AfsUpdateBO;
import com.medusa.gruul.afs.service.model.dto.*;
import com.medusa.gruul.afs.service.model.enums.AfsError;
import com.medusa.gruul.afs.service.mp.entity.AfsHistory;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.entity.AfsPackage;
import com.medusa.gruul.afs.service.mp.service.IAfsHistoryService;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderService;
import com.medusa.gruul.afs.service.mp.service.IAfsPackageService;
import com.medusa.gruul.afs.service.service.AfsQueryService;
import com.medusa.gruul.afs.service.service.AfsService;
import com.medusa.gruul.afs.service.task.AfsOrderExportTask;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.global.model.filter.IFilter;
import com.medusa.gruul.global.model.filter.IFilterPipeline;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopLogisticsAddressVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/6
 */
@Service
@RequiredArgsConstructor
public class AfsServiceImpl implements AfsService {

    private final IAfsHistoryService afsHistoryService;
    private final IAfsPackageService afsPackageService;
    private final IAfsOrderService afsOrderService;
    private final PaymentRpcService paymentRpcService;

    private final ShopRpcService shopRpcService;

    private final IFilter<AfsUpdateBO> getAfsOrderFilter;
    private final RabbitTemplate rabbitTemplate;
    private final IFilter<AfsUpdateBO> getAndCheckShopOrderItemFilter;
    private final IFilter<AfsUpdateBO> updateAfsOrderStatusFilter;
    private final IFilter<AfsUpdateBO> updateShopOrderItemStatus;
    private final WechatProperties wechatProperties;
    private final OrderRpcService orderRpcService;

    private final DataExportRecordRpcService dataExportRecordRpcService;

    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final UaaRpcService uaaRpcService;

    private final Executor afsExecutor;

    @Resource
    private AfsQueryService afsQueryService;

    @Override
    @Redisson(value = AfsConst.AFS_AGREE_LOCK, key = "#afsNo")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void afsRequestAgreeReject(String afsNo, AfsAuditDTO afsAudit, boolean isSystem) {
        Boolean isAgree = afsAudit.getAgree();
        IFilterPipeline.build(Option.of(new AfsUpdateBO().setAfsNo(afsNo).setIsSystem(isSystem)))
                //获取售后工单信息
                .addFilter(getAfsOrderFilter)
                //检查目标状态是否重复消费 状态
                .addFilter(context -> {
                    //商家管理员不可操作供应商售后
                    ISecurity.match().ifAnyShopAdmin(secureUser -> SecureCodes.PERMISSION_DENIED.falseThrow(
                            context.getData().getAfsOrder().getSupplierId() == null));
                    AfsStatus currentStatus = afsAudit.getCurrentStatus();
                    context.setBreakIt(
                            currentStatus != null && currentStatus != context.getData().getAfsOrder()
                                    .getStatus());
                })
                //检查售后工单状态
                .addFilter(context -> AfsError.NOT_SUPPORT_APPROVAL_STATUS.falseThrow(
                        context.getData().getAfsOrder().getStatus().isCanAgreeOrReject()))
                //获取订单商品项 并检查数据
                .addFilter(getAndCheckShopOrderItemFilter)
                //获取售后下一步状态
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    data.setNextStatus(AfsStatus.REFUND_REQUEST == data.getAfsOrder().getStatus() ?
                            (isAgree ? (isSystem ? AfsStatus.SYSTEM_REFUND_AGREE : AfsStatus.REFUND_AGREE)
                                    : AfsStatus.REFUND_REJECT) :
                            (isAgree ? (isSystem ? AfsStatus.SYSTEM_RETURN_REFUND_AGREE
                                    : AfsStatus.RETURN_REFUND_AGREE) : AfsStatus.RETURN_REFUND_REJECT));
                })
                //更新售后工单状态
                .addFilter(updateAfsOrderStatusFilter)
                //生成并保存售后历史
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    saveHistory(new AfsHistory().setAfsNo(afsNo).setAfsStatus(data.getNextStatus())
                            .setPackageStatus(data.getShopOrderItem().getPackageStatus())
                            .setRemark(afsAudit.getRemark()));
                })
                //如果是同意退款申请 则 调起退款
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    if (!data.getNextStatus().isCanRefunded()) {
                        return;
                    }
                    AfsOrder afsOrder = data.getAfsOrder();
                    paymentRpcService.refundRequest(new RefundRequestDTO()
                            .setOrderNum(afsOrder.getOrderNo())
                            .setShopId(afsOrder.getShopId())
                            .setAfsNum(afsNo)
                            .setExchange(AfsRabbit.AFS_REFUND_CALLBACK.exchange())
                            .setRouteKey(AfsRabbit.AFS_REFUND_CALLBACK.routingKey())
                            .setRefundFee(afsOrder.getRefundAmount())
                    );
                })
                //如果是同意退货退换 则 获取默认退货地址 保存退款地址数据
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    if (AfsStatus.SYSTEM_RETURN_REFUND_AGREE != data.getNextStatus()
                            && AfsStatus.RETURN_REFUND_AGREE != data.getNextStatus()) {
                        return;
                    }
                    AfsOrder afsOrder = data.getAfsOrder();
                    UserAddressDTO receiver = Option.of(afsAudit.getReceiver())
                            .getOrElse(() -> {
                                //查询默认退货地址
                                ShopLogisticsAddressVO receiveAddress = Option.of(
                                                shopRpcService.getSendOrReceiveAddress(afsOrder.getShopId(), Boolean.FALSE))
                                        .getOrElseThrow(AfsError.NOT_DEFAULT_RETURN_ADDRESS::exception);
                                return new UserAddressDTO()
                                        .setName(receiveAddress.getContactName())
                                        .setMobile(receiveAddress.getContactPhone())
                                        .setAddress(
                                                CollUtil.join(receiveAddress.getArea(), StrUtil.SPACE) + StrUtil.SPACE
                                                        + receiveAddress.getAddress()
                                        );
                            });
                    boolean success = afsPackageService.save(
                            new AfsPackage()
                                    .setAfsNo(afsNo)
                                    .setType(DeliverType.EXPRESS)
                                    .setReceiverName(receiver.getName())
                                    .setReceiverMobile(receiver.getMobile())
                                    .setReceiverAddress(receiver.getAddress())
                    );
                    SystemCode.DATA_ADD_FAILED.falseThrow(success);
                }).addFilter(updateShopOrderItemStatus)
                //发送自动关闭mq
                .addFilter(context -> {
                    AfsOrder afsOrder = context.getData().getAfsOrder();
                    if (!isAgree || AfsType.RETURN_REFUND == afsOrder.getType()) {
                        return;
                    }
                    rabbitTemplate.convertAndSend(
                            AfsRabbit.AFS_AUTO_CLOSE.exchange(),
                            AfsRabbit.AFS_AUTO_CLOSE.routingKey(),
                            new AfsMqMessageDTO().setAfsNo(afsNo)
                                    .setCurrentStatus(context.getData().getNextStatus()),
                            message -> {
                                message.getMessageProperties().setHeader(MessageProperties.X_DELAY,
                                        afsOrder.getKeyNodeTimeout().getReturnedTimeoutMills());
                                return message;
                            }

                    );
                })//发送微信发货信息录入
                .addFilter(context -> wxOrderShipping(afsNo, isAgree)).flush();
    }

    @Override
    @Redisson(value = AfsConst.AFS_AGREE_LOCK, key = "#afsNo")
    @Transactional(rollbackFor = Exception.class)
    public void refundedNotify(String afsNo, String refundNum) {
        IFilterPipeline.build(Option.of(
                        new AfsUpdateBO().setAfsNo(afsNo).setAfsTradeNo(refundNum).setIsSystem(Boolean.TRUE)))
                //获取售后工单
                .addFilter(getAfsOrderFilter)
                //检查当前售后状态
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    AfsStatus currentStatus = data.getAfsOrder().getStatus();
                    if (!currentStatus.isCanRefunded()) {
                        context.setBreakIt(Boolean.TRUE);
                        return;
                    }
                    //已退款 直接中断执行
                    if (currentStatus.isRefunded()) {
                        context.setBreakIt(Boolean.TRUE);
                        return;
                    }
                    data.setNextStatus((AfsStatus.REFUND_AGREE == currentStatus
                            || AfsStatus.SYSTEM_REFUND_AGREE == currentStatus) ? AfsStatus.REFUNDED
                            : AfsStatus.RETURNED_REFUNDED);
                })
                //获取订单商品项 并检查数据
                .addFilter(getAndCheckShopOrderItemFilter)
                //更新售后工单状态
                .addFilter(updateAfsOrderStatusFilter)
                //生成并保存售后历史
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    saveHistory(
                            new AfsHistory()
                                    .setAfsNo(afsNo)
                                    .setAfsStatus(data.getNextStatus())
                                    .setPackageStatus(data.getShopOrderItem().getPackageStatus())
                    );
                })
                //更新订单商品项状态
                .addFilter(updateShopOrderItemStatus)
                .flush();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afsBuyerReturned(String afsNo, RefundType type, BuyerReturnedDTO buyerReturned) {
        final AtomicReference<String> remarkHolder = new AtomicReference<>(null);
        if (type == RefundType.EXPRESS_REFUND) {
            buyerReturned.getExpressRefund().validParam();
            remarkHolder.set(buyerReturned.getExpressRefund().getRemark());
        } else {
            remarkHolder.set(buyerReturned.getGoStoreRefund().getExplain());
        }

        IFilterPipeline.build(Option.of(new AfsUpdateBO().setAfsNo(afsNo).setIsSystem(Boolean.FALSE)))
                //获取售后工单
                .addFilter(getAfsOrderFilter)
                //检查当前售后状态
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    AfsError.NOT_SUPPORT_RETURN_STATUS.trueThrow(
                            AfsStatus.RETURN_REFUND_AGREE != data.getAfsOrder().getStatus()
                                    && AfsStatus.SYSTEM_RETURN_REFUND_AGREE != data.getAfsOrder().getStatus());
                    data.setNextStatus(AfsStatus.RETURNED_REFUND);
                })
                //获取订单商品项 并检查数据
                .addFilter(getAndCheckShopOrderItemFilter)
                //更新售后工单状态
                .addFilter(updateAfsOrderStatusFilter)
                //生成并保存售后历史 并保存用户发货数据
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    saveHistory(new AfsHistory()
                            .setAfsNo(afsNo)
                            .setAfsStatus(data.getNextStatus())
                            .setPackageStatus(data.getShopOrderItem().getPackageStatus())
                            .setRemark(remarkHolder.get()));
                    boolean success = afsPackageService.lambdaUpdate()
                            .set(AfsPackage::getRefundType, type)
                            .set(AfsPackage::getBuyerReturnedInfo, JSON.toJSONString(buyerReturned))
                            .eq(AfsPackage::getAfsNo, afsNo)
                            .update();
                    SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
                })
                //更新订单商品项状态
                .addFilter(updateShopOrderItemStatus)
                //发送mq
                .addFilter(context ->
                        rabbitTemplate.convertAndSend(
                                AfsRabbit.AFS_AUTO_CONFIRM_RETURNED.exchange(),
                                AfsRabbit.AFS_AUTO_CONFIRM_RETURNED.routingKey(),
                                new AfsMqMessageDTO().setAfsNo(afsNo)
                                        .setCurrentStatus(AfsStatus.RETURNED_REFUND_CONFIRM),
                                message -> {
                                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY,
                                            context.getData().getAfsOrder().getKeyNodeTimeout()
                                                    .getConfirmReturnedTimeoutMills());
                                    return message;
                                }
                        )
                )
                //渲染
                .flush();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = AfsConst.AFS_AGREE_LOCK, key = "#afsNo")
    public void afsReturnedConfirmReject(String afsNo, AfsAuditDTO afsAudit, boolean isSystem) {
        Boolean agree = afsAudit.getAgree();
        IFilterPipeline.build(Option.of(new AfsUpdateBO().setAfsNo(afsNo).setIsSystem(isSystem)))
                //获取售后工单
                .addFilter(getAfsOrderFilter)
                //检查是否可以直接跳过
                .addFilter(context -> {
                    //商家管理员不可操作供应商售后
                    ISecurity.match().ifAnyShopAdmin(secureUser -> SecureCodes.PERMISSION_DENIED.falseThrow(context.getData().getAfsOrder().getSupplierId() == null));
                    AfsStatus currentStatus = afsAudit.getCurrentStatus();
                    context.setBreakIt(currentStatus != null && currentStatus != context.getData().getAfsOrder().getStatus());
                })
                //检查当前售后状态
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    AfsStatus status = data.getAfsOrder().getStatus();
                    AfsError.NOT_SUPPORT_REFUSE_RETURN_STATUS.falseThrow(AfsStatus.RETURNED_REFUND == status);
                    data.setNextStatus(agree ? (isSystem ? AfsStatus.SYSTEM_RETURN_REFUND_AGREE : AfsStatus.RETURNED_REFUND_CONFIRM) : AfsStatus.RETURNED_REFUND_REJECT);
                })
                //获取订单商品项 并检查数据
                .addFilter(getAndCheckShopOrderItemFilter)
                //更新售后工单状态
                .addFilter(updateAfsOrderStatusFilter)
                //生成并保存售后历史 若是商家确认收货 则 调起退款
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    saveHistory(new AfsHistory()
                            .setAfsNo(afsNo)
                            .setAfsStatus(data.getNextStatus())
                            .setPackageStatus(data.getShopOrderItem().getPackageStatus())
                            .setRemark(afsAudit.getRemark()));
                    if (agree) {
                        AfsOrder afsOrder = data.getAfsOrder();
                        paymentRpcService.refundRequest(
                                new RefundRequestDTO()
                                        .setOrderNum(afsOrder.getOrderNo())
                                        .setShopId(afsOrder.getShopId())
                                        .setAfsNum(afsNo)
                                        .setExchange(AfsRabbit.AFS_REFUND_CALLBACK.exchange())
                                        .setRouteKey(AfsRabbit.AFS_REFUND_CALLBACK.routingKey())
                                        .setRefundFee(afsOrder.getRefundAmount())
                        );
                    }
                })
                //更新订单商品项状态
                .addFilter(updateShopOrderItemStatus)
                //渲染
                .flush();
    }

    /**
     * 发起微信订单物流录入
     *
     * @param afsNo 售后工单
     * @param agree 是否同意
     */
    private void wxOrderShipping(String afsNo, Boolean agree) {
        if (!wechatProperties.getMiniAppDeliver() || !agree) {
            return;
        }
        //判断该笔订单下是否还有待发货不处于售后、正在售后的明细
        Boolean exists = orderRpcService.existsDeliverShopOrderItem(afsNo);
        if (!exists) {
            //发送微信小程序退货发货信息录入 MQ
            rabbitTemplate.convertAndSend(
                    OrderRabbit.MINI_APP_ORDER_RETURN_GOODS.exchange(),
                    OrderRabbit.MINI_APP_ORDER_RETURN_GOODS.routingKey(),
                    new ShopOrderItem().setAfsNo(afsNo)
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void afsClose(AfsCloseDTO afsClose) {
        AfsStatus currentStatus = afsClose.getCurrentStatus();
        String afsNo = afsClose.getAfsNo();
        Boolean isSystem = afsClose.getIsSystem();
        IFilterPipeline<AfsUpdateBO> pipeline = IFilterPipeline.build(Option.of(
                        new AfsUpdateBO()
                                .setAfsNo(afsNo)
                                .setIsSystem(isSystem)
                                .setNextStatus(isSystem ? AfsStatus.SYSTEM_CLOSED : AfsStatus.BUYER_CLOSED)
                ))
                //获取售后工单
                .addFilter(getAfsOrderFilter)
                //1.检查是否可以直接跳过 2.检查当前售后状态
                .addFilter(context -> {
                    AfsStatus afsOrderStatus = context.getData().getAfsOrder().getStatus();
                    boolean breakIt = currentStatus != null && currentStatus != afsOrderStatus;
                    context.setBreakIt(breakIt);
                    //不能跳过 但是售后不能关闭
                    AfsError.NOT_SUPPORT_CLOSE_STATUS.falseThrow(breakIt || afsOrderStatus.isCanClose());
                })
                //获取订单商品项 并检查数据
                .addFilter(getAndCheckShopOrderItemFilter)
                //更新售后工单状态
                .addFilter(updateAfsOrderStatusFilter)
                //生成并保存售后历史 若是商家确认收货 则 调起退款
                .addFilter(context -> {
                    AfsUpdateBO data = context.getData();
                    saveHistory(new AfsHistory()
                            .setAfsNo(afsNo)
                            .setAfsStatus(data.getNextStatus())
                            .setPackageStatus(data.getShopOrderItem().getPackageStatus())
                            .setRemark(afsClose.getReason())
                    );
                });
        //更新订单商品项状态
        if (BooleanUtil.isTrue(afsClose.getUpdateShopOrderItem())) {
            pipeline.addFilter(updateShopOrderItemStatus);
        }
        pipeline.flush();
    }


    private void saveHistory(AfsHistory history) {
        boolean success = afsHistoryService.save(history);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }

    @Override
    public void afsOrderBatchRemark(AfsRemarkDTO afsRemark) {
        LambdaUpdateChainWrapper<AfsOrder> updateWrapper = afsOrderService.lambdaUpdate();
        updateWrapper.in(AfsOrder::getNo, afsRemark.getNos())
                .set(AfsOrder::getRemark, afsRemark.getRemark());
        TenantShop.disable(
                () -> {
                    ISecurity.match()
                            .ifAnyShopAdmin(
                                    secureUser -> updateWrapper.eq(AfsOrder::getShopId, secureUser.getShopId()))
                            .ifAnySupplierAdmin(
                                    secureUser -> updateWrapper.eq(AfsOrder::getSupplierId, secureUser.getShopId()));
                    updateWrapper.update();
                }
        );
    }

    @Override
    public Long export(AfsPageDTO afsPage) {
        DataExportRecordDTO dto = new DataExportRecordDTO();
        dto.setExportUserId(ISecurity.userMust().getId())
                .setDataType(ExportDataType.AFTER_SALES_WORK_ORDER)
                .setShopId(ISystem.shopIdMust())
                .setUserPhone(ISecurity.userMust().getMobile());
        //RPC保存导出记录
        Long exportRecordId = dataExportRecordRpcService.saveExportRecord(dto);
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> afsPage.setSupplierId(secureUser.getShopId()))
                .ifAnyShopAdmin(secureUser -> afsPage.setShopId(secureUser.getShopId()))
                .ifUser(secureUser -> afsPage.setBuyerId(secureUser.getId()));
        AfsOrderExportTask task = AfsOrderExportTask.builder()
                .exportRecordId(exportRecordId)
                .afsPageDTO(afsPage)
                .uaaRpcService(uaaRpcService)
                .afsQueryService(afsQueryService)
                .afsPageDTO(afsPage)
                .dataExportRecordRpcService(dataExportRecordRpcService)
                .pigeonChatStatisticsRpcService(pigeonChatStatisticsRpcService)
                .build();
        afsExecutor.execute(task);
        return exportRecordId;
    }


}
