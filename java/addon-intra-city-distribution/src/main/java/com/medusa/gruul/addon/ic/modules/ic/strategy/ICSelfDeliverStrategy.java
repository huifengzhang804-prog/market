package com.medusa.gruul.addon.ic.modules.ic.strategy;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.ic.model.ICError;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOrderTimes;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopOrderDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.ic.util.ICHelper;
import com.medusa.gruul.addon.ic.modules.uupt.util.UuptHelper;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商家配送策略
 *
 * @author 张治保
 * @since 2024/8/30
 */
@RequiredArgsConstructor
public class ICSelfDeliverStrategy implements IStrategy<ICDeliveryType, ICOrder, Void> {

    private final ICShopOrderDao shopOrderDao;

    @Override
    public Void execute(ICDeliveryType type, ICOrder param) {
        Long shopId = param.getShopId();
        String orderNo = param.getOrderNo();
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(BaseEntity::getId, ICShopOrder::getStatus, ICShopOrder::getOpen)
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        //如果已经处于进行中 则时二次提交直接返回
        boolean orderExists = order != null;
        String formattedPickupCode = ICHelper.pickupCodeFormat(param.getPickupCode());
        if (orderExists) {
            ICShopOrderStatus status = order.getStatus();
            if (ICShopOrderStatus.DELIVERED == status) {
                throw ICError.IC_ORDER_FINISHED.exception();
            }
            //如果当前是运费价格计算状态  直接更新这条数据即可
            //PRICE_PADDING 状态代表进行了开放平台的价格计算 直接更新掉这个数据即可，其它情况直接新增
            if (ICShopOrderStatus.PRICE_PADDING == order.getStatus()) {
                shopOrderDao.lambdaUpdate()
                        .set(ICShopOrder::getType, ICDeliveryType.SELF)
                        .set(ICShopOrder::getPickupCode, formattedPickupCode)
                        .set(ICShopOrder::getStatus, ICShopOrderStatus.PENDING)
                        .set(ICShopOrder::getTimes, JSON.toJSONString(new ICOrderTimes().setShippingTime(LocalDateTime.now())))
                        .set(ICShopOrder::getReceiver, JSON.toJSONString(param.getReceiver()))
                        .set(ICShopOrder::getSkus, JSON.toJSONString(param.getSkus()))
                        .set(ICShopOrder::getRemark, JSON.toJSONString(param.getRemark()))
                        .eq(ICShopOrder::getOrderNo, orderNo)
                        .eq(ICShopOrder::getShopId, shopId)
                        .eq(BaseEntity::getId, order.getId())
                        .update();
                return VOID;
            }
            if (ICShopOrderStatus.ERROR != status) {
                throw ICError.IC_ORDER_PROCESSING.exception();
            }
        }
        //其它状态直接保存新数据
        shopOrderDao.save(
                new ICShopOrder()
                        .setShopId(shopId)
                        .setType(ICDeliveryType.SELF)
                        .setIcNo(UuptHelper.icNo())
                        .setOrderNo(orderNo)
                        .setPickupCode(formattedPickupCode)
                        .setStatus(ICShopOrderStatus.PENDING)
                        .setTimes(new ICOrderTimes().setShippingTime(LocalDateTime.now()))
                        .setReceiver(param.getReceiver())
                        .setSkus(param.getSkus())
                        .setRemark(JSON.toJSONString(param.getRemark()))
        );
        return VOID;
    }
}
