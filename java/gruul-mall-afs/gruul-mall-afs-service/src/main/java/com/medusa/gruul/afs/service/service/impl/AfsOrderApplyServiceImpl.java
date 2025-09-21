package com.medusa.gruul.afs.service.service.impl;

import com.medusa.gruul.afs.api.constant.AfsConst;
import com.medusa.gruul.afs.api.enums.AfsRabbit;
import com.medusa.gruul.afs.api.enums.AfsReason;
import com.medusa.gruul.afs.api.enums.AfsType;
import com.medusa.gruul.afs.service.model.dto.AfsApplyDTO;
import com.medusa.gruul.afs.service.model.dto.AfsAuditDTO;
import com.medusa.gruul.afs.service.model.dto.AfsMqMessageDTO;
import com.medusa.gruul.afs.service.model.enums.AfsError;
import com.medusa.gruul.afs.service.mp.entity.*;
import com.medusa.gruul.afs.service.mp.service.IAfsHistoryService;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderItemService;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderReceiverService;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderService;
import com.medusa.gruul.afs.service.properties.AfsConfigurationProperties;
import com.medusa.gruul.afs.service.service.AfsOrderApplyService;
import com.medusa.gruul.afs.service.service.AfsService;
import com.medusa.gruul.afs.service.util.AfsUtil;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderReceiver;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.ChangeAfsStatusDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.vo.CurrentMemberVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 售后服务
 * </p>
 *
 * @author 张治保
 * date 2022/8/3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AfsOrderApplyServiceImpl implements AfsOrderApplyService {
    private final AfsService afsService;
    private final UserRpcService userRpcService;
    private final RabbitTemplate rabbitTemplate;
    private final OrderRpcService orderRpcService;
    private final IAfsOrderService afsOrderService;
    private final IAfsHistoryService afsHistoryService;
    private final IAfsOrderItemService afsOrderItemService;
    private final IAfsOrderReceiverService afsOrderReceiverService;
    private final AfsConfigurationProperties afsConfigurationProperties;
    private final ShopRpcService shopRpcService;

    /**
     * 获取退款金额
     */
    private static Long getRefundAmount(Long maxRefundAmount, AfsApplyDTO createAfs) {
        Long refundAmount = createAfs.getRefundAmount();
        AfsType type = createAfs.getType();
        if (AfsType.REFUND == type) {
            return maxRefundAmount;
        }
        AfsError.REFUND_AMOUNT_CANNOT_GREATER_THAN_PAID_AMOUNT.trueThrow(refundAmount > maxRefundAmount);
        return refundAmount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = AfsConst.AFS_APPLY_LOCK, key = "#createAfs.orderNo+':'+#createAfs.shopId+':'+#createAfs.itemId")
    public void apply(AfsApplyDTO createAfs) {
        Long buyerId = ISecurity.userMust().getId();
        String mobile = ISecurity.userMust().getMobile();
        String orderNo = createAfs.getOrderNo();
        Long shopId = createAfs.getShopId();
        Long itemId = createAfs.getItemId();

        //查询用户权益 ：：放前边是为了rpc早点报错（如果rpc存在错误），以防操作数据库：：
        CurrentMemberVO currentMemberVO = userRpcService.getTopMemberCardInfo(buyerId).getCurrentMemberVO();

        boolean hasQuicknessAfs = currentMemberVO != null && currentMemberVO.getRelevancyRights() != null && currentMemberVO.getRelevancyRights().containsKey(RightsType.QUICKNESS_AFS);
        /* 检查最后一条售后信息
         */
        AfsStatus currentItemLastAfsStatus = getCurrentItemLastAfsStatus(orderNo, shopId, itemId, buyerId);
        AfsError.ITEM_CANNOT_AFS_STATUS.falseThrow(currentItemLastAfsStatus.isCanReRequest());

        /* 统计当前订单售后数量
         */
        long count = TenantShop.disable(() -> afsOrderService.lambdaQuery().eq(AfsOrder::getOrderNo, orderNo).count());
        AfsType afsType = createAfs.getType();
        /* 修改订单商品售后状态
         */
        ShopOrderItem shopOrderItem = orderRpcService.getShopOrderItem(orderNo, shopId, itemId)
                .getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
        AfsUtil.checkShopOrderItemForAfs(buyerId, shopOrderItem);

        /* 生成售后订单
         */
        AfsOrder afsOrder = createAfsOrder((int) count, shopOrderItem, createAfs, mobile, hasQuicknessAfs);
        String afsNo = afsOrder.getNo();
        /* 生成 售后订单商品
         */
        createAfsOrderItem(afsNo, shopOrderItem);
        /* 生成收回地址备份
         */
        createAfsOrderReceiver(afsNo, shopOrderItem.getShopOrder());
        /* 生成协商历史
         */
        AfsStatus afsStatus = afsType.getAfsStatus();
        AfsHistory afsHistory = new AfsHistory()
                .setAfsNo(afsNo)
                .setAfsStatus(afsStatus)
                .setPackageStatus(shopOrderItem.getPackageStatus())
                .setRemark(createAfs.getRemark())
                .setEvidences(createAfs.getEvidences());
        afsHistoryService.save(afsHistory);
        //修改订单商品售后状态 orderNo, shopId, itemId, afsType.getAfsStatus()
        orderRpcService.updateShopOrderItemAfsStatus(
                new ChangeAfsStatusDTO().setOrderNo(orderNo)
                        .setShopId(shopId)
                        .setItemId(itemId)
                        .setAfsNo(afsNo)
                        .setStatus(afsStatus)
        );

        //事物提交后执行
        IManualTransaction.afterCommit(
                //发送超时审核mq
                () -> {

                    //极速售后自动同意申请
                    if (hasQuicknessAfs) {
                        afsService.afsRequestAgreeReject(afsNo,
                                new AfsAuditDTO().setAgree(Boolean.TRUE).setCurrentStatus(afsType.getAfsStatus()).setReceiver(null).setRemark("已享受极速售后权益"),
                                Boolean.TRUE
                        );
                        return;
                    }
                    rabbitTemplate.convertAndSend(
                            AfsRabbit.AFS_AUTO_AGREE_REQUEST.exchange(),
                            AfsRabbit.AFS_AUTO_AGREE_REQUEST.routingKey(),
                            new AfsMqMessageDTO().setAfsNo(afsNo).setCurrentStatus(afsStatus),
                            message -> {
                                message.getMessageProperties().setHeader(MessageProperties.X_DELAY, afsOrder.getKeyNodeTimeout().getRequestAgreeTimeoutMills());
                                return message;
                            }
                    );
                }
        );


    }

    /**
     * 检查申请售后时 最后一条商品售后状态是否允许重新申请
     */
    private AfsStatus getCurrentItemLastAfsStatus(String orderNo, Long shopId, Long itemId, Long buyerId) {
        AfsOrder lastAfsOrder = ISystem.shopId(
                shopId,
                () -> afsOrderService.lambdaQuery()
                        .eq(AfsOrder::getOrderNo, orderNo)
                        .eq(AfsOrder::getShopOrderItemId, itemId)
                        .eq(AfsOrder::getBuyerId, buyerId)
                        .orderByDesc(AfsOrder::getCreateTime)
                        .last(SqlHelper.SQL_LIMIT_1)
                        .one()
        );
        return Option.of(lastAfsOrder).map(AfsOrder::getStatus).getOrElse(() -> AfsStatus.NONE);
    }

    /**
     * 创建售后订单 并返回订单号
     */
    private AfsOrder createAfsOrder(int preCount, ShopOrderItem shopOrderItem, AfsApplyDTO createAfs,
                                    String mobile, boolean hasQuicknessAfs) {
        AfsReason reason = createAfs.getReason();
        AfsType afsType = createAfs.getType();
        PackageStatus packageStatus = shopOrderItem.getPackageStatus();
        AfsError.TYPE_REASON_NOT_MATCH.trueThrow(AfsType.REFUND == afsType && !reason.isOnlyRefund());
        boolean isUndelivered = PackageStatus.WAITING_FOR_DELIVER == packageStatus;
        AfsError.NOT_DELIVERED_TYPE_ERROR.trueThrow(isUndelivered && AfsType.RETURN_REFUND == afsType);
        String orderNo = shopOrderItem.getOrderNo();
        ShopOrder shopOrder = shopOrderItem.getShopOrder();

        Order mainOrder = shopOrder.getOrder();
        Long buyerId = mainOrder.getBuyerId();
        Long itemId = shopOrderItem.getId();
        long maxRefundAmount = shopOrderItem.getDealPrice() * shopOrderItem.getNum()
                + shopOrderItem.getFixPrice()
                + (isUndelivered ? shopOrderItem.getFreightPrice() : 0);
        String afsNo = AfsUtil.no(orderNo, afsType, preCount + 1);
        AfsConfigurationProperties.AfsTimeout afsTimeout = afsConfigurationProperties.getAfsTimeout();
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopOrderItem.getShopId());

        AfsOrder order = new AfsOrder()
                .setNo(afsNo)
                .setType(afsType)
                .setStatus(afsType.getAfsStatus())
                .setPackageId(shopOrderItem.getPackageId())
                .setPackageStatus(packageStatus)
                .setReason(reason)
                .setBuyerId(buyerId)
                .setBuyerNickname(ISecurity.generateNickName(mainOrder.getBuyerNickname(), buyerId).getOrNull())
                .setBuyerPhone(mobile)
                .setOrderNo(orderNo)
                .setSupplierId(shopOrderItem.getSellType() == SellType.CONSIGNMENT ? shopOrderItem.getSupplierId() : null)
                .setShopId(shopOrderItem.getShopId())
                .setShopName(shopOrder.getShopName())
                .setShopLogo(shopOrder.getShopLogo())
                .setShopType(shopInfo.getShopType())
                .setShopOrderItemId(itemId)
                .setExtra(shopOrder.getOrder().getExtra())
                .setRefundAmount(getRefundAmount(maxRefundAmount, createAfs))
                .setExplain(createAfs.getRemark())
                .setKeyNodeTimeout(
                        new AfsKeyNodeTimeout()
                                .setRequestAgreeTimeout(mainOrder.getTimeout().getAfsAuditTimeout())
                                .setReturnedTimeout(afsTimeout.getReturnedTimeout())
                                .setConfirmReturnedTimeout(afsTimeout.getConfirmReturnedTimeout())
                );
        if (hasQuicknessAfs) {
            //是否有急速售后的权益
            order.setQuicknessAfs(Boolean.TRUE);
        }
        TenantShop.disable(() -> afsOrderService.save(order));
        return order;
    }

    private void createAfsOrderReceiver(String afsNo, ShopOrder shopOrder) {
        OrderReceiver receiver = Option.of(shopOrder.getOrderReceiver())
                .getOrElse(() -> shopOrder.getOrder().getOrderReceiver());
        if (receiver == null) {
            return;
        }
        afsOrderReceiverService.save(
                new AfsOrderReceiver()
                        .setAfsNo(afsNo)
                        .setName(receiver.getName())
                        .setMobile(receiver.getMobile())
                        .setArea(receiver.getArea())
                        .setAddress(receiver.getAddress())
        );
    }

    /**
     * 创建售后商品详情
     *
     * @param afsNo         售后工单号
     * @param shopOrderItem 商品详情
     */
    private void createAfsOrderItem(String afsNo, ShopOrderItem shopOrderItem) {
        AfsOrderItem afsOrderItem = new AfsOrderItem()
                .setAfsNo(afsNo)
                .setSellType(shopOrderItem.getSellType())
                .setProductId(shopOrderItem.getProductId())
                .setProductName(shopOrderItem.getProductName())
                .setProductType(shopOrderItem.getProductType())
                .setSkuId(shopOrderItem.getSkuId())
                .setSpecs(shopOrderItem.getSpecs())
                .setImage(shopOrderItem.getImage())
                .setNum(shopOrderItem.getNum())
                .setServices(shopOrderItem.getServices())
                .setSalePrice(shopOrderItem.getSalePrice())
                .setDealPrice(shopOrderItem.getDealPrice());
        afsOrderItemService.save(afsOrderItem);
    }

}
