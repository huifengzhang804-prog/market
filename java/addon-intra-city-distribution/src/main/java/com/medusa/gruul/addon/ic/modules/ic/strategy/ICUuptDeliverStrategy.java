package com.medusa.gruul.addon.ic.modules.ic.strategy;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.ic.model.ICError;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOpen;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOrderTimes;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopOrderDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.ic.util.ICHelper;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.OrderSource;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PayType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PushType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.SpecialType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.AddOrderParam;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.OrderKey;
import com.medusa.gruul.addon.ic.modules.uupt.model.UuptConstant;
import com.medusa.gruul.addon.ic.modules.uupt.util.UuptHelper;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.base.BaseEntity;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * uu跑腿配送策略
 *
 * @author 张治保
 * @since 2024/8/30
 */
@Slf4j
@RequiredArgsConstructor
public class ICUuptDeliverStrategy implements IStrategy<ICDeliveryType, ICOrder, Void> {

    private final IUuptApiFactory uuptApiFactory;
    private final ICShopOrderDao shopOrderDao;
    private final GlobalAppProperties globalAppProperties;
    private volatile String callbackUrl;

    @Override
    public Void execute(ICDeliveryType type, ICOrder param) {
        Long shopId = param.getShopId();
        //获取UU 跑腿 openid
        String openId = UuptHelper.getOpenid(shopId);
        if (StrUtil.isEmpty(openId)) {
            throw ICError.UUPT_UNACTIVATED.exception();
        }
        String orderNo = param.getOrderNo();
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getStatus, ICShopOrder::getPickupCode, ICShopOrder::getReceiver, ICShopOrder::getOpen)
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (order == null) {
            throw ICError.UUPT_ORDER_PRICE_FIRST.exception();
        }
        ICShopOrderStatus status = order.getStatus();
        if (ICShopOrderStatus.DELIVERED == status) {
            throw ICError.IC_ORDER_FINISHED.exception();
        }
        if (ICShopOrderStatus.ERROR == status) {
            throw ICError.UUPT_ORDER_PRICE_FIRST.exception();
        }
        if (ICShopOrderStatus.PRICE_PADDING != status) {
            throw ICError.IC_ORDER_PROCESSING.exception();
        }
        ICOpen open = order.getOpen();
        String uuptPriceToken;
        if (open == null || LocalDateTime.now().isAfter(open.getPriceTokenExpireTime()) || StrUtil.isEmpty(uuptPriceToken = open.getUuptPriceToken())) {
            throw ICError.UUPT_ORDER_PRICE_FIRST.exception();
        }
        UserAddressDTO receiver = order.getReceiver();
        if (receiver == null) {
            throw ICError.USER_ADDRESS_ISNULL.exception();
        }
        AddOrderParam addOrderParam = new AddOrderParam()
                .setPriceToken(uuptPriceToken)
                .setReceiverPhone(receiver.getMobile())
                .setReceiverRealPhone(DesensitizedUtil.mobilePhone(receiver.getMobile()))
//                .setNote(ShopOrderFormModel.remarkFirst(param.getRemark()))
                .setCallbackUrl(getCallbackUrl())
                .setPushType(uuptApiFactory.config().isTest() ? PushType.TEST_ORDER : PushType.OPEN_ORDER)
                .setSpecialType(BooleanUtil.isTrue(param.getWarmBox()) ? SpecialType.NEED_WARM : SpecialType.NOT_NEED_WARM)
                .setPayType(PayType.BALANCE_PAY)
                .setOrderSource(OrderSource.OTHER);
        String formattedPickupCode = ICHelper.pickupCodeFormat(param.getPickupCode());
        if (formattedPickupCode != null) {
            addOrderParam.setShortOrderNum(formattedPickupCode);
        }
        //发布 uupt订单
        UuptResponse<OrderKey> response = uuptApiFactory.order()
                .addOrder(openId, addOrderParam);
        log.debug("uupt 发布订单，返回结果：{}", response);

        if (!response.isSuccess()) {
            if (response.getCode().equals(UuptConstant.UUPT_BALANCE_NOT_ENOUGH)) {
                throw ICError.UUPT_BALANCE_NOT_ENOUGH.dataEx(orderNo);
            }
            throw ICError.UUPT_RESP_ERROR.dataEx(response);
        }
        shopOrderDao.lambdaUpdate()
                .set(ICShopOrder::getType, ICDeliveryType.UUPT)
                .set(ICShopOrder::getPickupCode, formattedPickupCode)
                .set(ICShopOrder::getStatus, ICShopOrderStatus.PENDING)
                .set(ICShopOrder::getTimes, JSON.toJSONString(new ICOrderTimes().setShippingTime(LocalDateTime.now())))
                .set(ICShopOrder::getRemark, JSON.toJSONString(param.getRemark()))
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .update();
        return VOID;
    }

    private String getCallbackUrl() {
        if (callbackUrl != null) {
            return callbackUrl;
        }
        synchronized (this) {
            if (callbackUrl != null) {
                return callbackUrl;
            }
            String baseUrl = globalAppProperties.getBaseUrl();

            return callbackUrl = (baseUrl.endsWith(StrPool.SLASH) ? baseUrl : baseUrl + StrPool.SLASH)
                    + globalAppProperties.getName() + StrPool.SLASH
                    + "/ic/shop/order/uupt/callback";

        }
    }
}
