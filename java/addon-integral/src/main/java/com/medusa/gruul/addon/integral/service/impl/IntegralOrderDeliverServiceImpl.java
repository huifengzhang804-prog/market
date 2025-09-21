package com.medusa.gruul.addon.integral.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaOrderShippingService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaOrderShippingServiceImpl;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.dto.IntegralCompletionDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderDeliveryDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderRabbit;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrderPayment;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderPaymentService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.addon.integral.service.IntegralOrderDeliverService;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.enums.StatementRabbit;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.common.model.message.OverviewStatementDTO;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.model.wechat.logistics.DeliveryMode;
import com.medusa.gruul.order.api.model.wechat.logistics.LogisticsType;
import com.medusa.gruul.order.api.model.wechat.vo.DeliveryVO;
import com.medusa.gruul.order.api.model.wechat.vo.MiniAppDeliveryVO;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.medusa.gruul.common.model.constant.CommonPool.NUMBER_ZERO;

/**
 * @author shishuqian
 * date 2023/2/6
 * time 9:59
 **/

@Service
@RequiredArgsConstructor
public class IntegralOrderDeliverServiceImpl implements IntegralOrderDeliverService {

    private static final String EXPRESS_COMPANY_URL = "https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/get_delivery_list?access_token=";
    private final IIntegralOrderService iIntegralOrderService;
    private final IIntegralOrderPaymentService integralOrderPaymentService;
    private final RabbitTemplate rabbitTemplate;
    private final WxMaService wxMaService;
    private final WechatProperties wechatProperties;

    @Override
    public List<IntegralOrder> undeliverBatch() {
        return iIntegralOrderService.unDeliverBatch(IntegralOrderStatus.PAID);
    }


    @Override
    public IntegralOrder undeliver(String orderNo) {
        return iIntegralOrderService.undeliver(orderNo, IntegralOrderStatus.PAID);
    }


    @Override
    public void complete(Boolean isSystem, String orderNo) {

        IntegralOrder integralOrder = this.iIntegralOrderService.lambdaQuery()
                .select(IntegralOrder::getId, IntegralOrder::getNo, IntegralOrder::getStatus, IntegralOrder::getBuyerNickname, IntegralOrder::getBuyerId, IntegralOrder::getImage)
                .eq(IntegralOrder::getNo, orderNo)
                .one();

        if (integralOrder == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单号：" + orderNo + " 的订单信息不存在");
        }

        if (!isSystem && integralOrder.getStatus() != IntegralOrderStatus.ON_DELIVERY) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单号：" + orderNo + " 的状态已改变");
        }

        boolean update = this.iIntegralOrderService.lambdaUpdate()
                .eq(IntegralOrder::getNo, orderNo)
                .set(IntegralOrder::getStatus, IntegralOrderStatus.ACCOMPLISH)
                .set(IntegralOrder::getAccomplishTime, LocalDateTime.now())
                .update();

        if (!update) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单号：" + orderNo + " 确认收获失败");
        }
        IntegralOrderPayment integralOrderPayment = integralOrderPaymentService.lambdaQuery()
                .select(IntegralOrderPayment::getSn, IntegralOrderPayment::getPayAmount)
                .eq(IntegralOrderPayment::getOrderNo, integralOrder.getNo())
                .one();
        // 生成对账单
        rabbitTemplate.convertAndSend(
                StatementRabbit.OVERVIEW_STATEMENT.exchange(),
                StatementRabbit.OVERVIEW_STATEMENT.routingKey(),
                new OverviewStatementDTO()
                        .setOrderNo(integralOrder.getNo())
                        .setTransactionSerialNumber(integralOrderPayment.getSn())
                        .setUserNickname(integralOrder.getBuyerNickname())
                        .setUserAvatar(integralOrder.getImage())
                        .setUserId(integralOrder.getBuyerId())
                        .setShopId(0L)
                        .setTransactionType(TransactionType.INTEGRAL_GOODS_DEAL)
                        .setAccount(integralOrderPayment.getPayAmount())
                        .setChangeType(ChangeType.INCREASE)
                        .setTransactionTime(LocalDateTime.now())
        );


    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deliver(List<IntegralOrderDeliveryDTO> integralOrderDeliveryDTOList) throws WxErrorException {


        for (IntegralOrderDeliveryDTO dto : integralOrderDeliveryDTOList) {
            IntegralOrder one = iIntegralOrderService.getIntegralOrderDetailInfo(dto.getIntegralOrderNo());

            if (one == null) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单号：" + dto.getIntegralOrderNo() + " 的订单信息不存在");
            }

            if (one.getStatus() != IntegralOrderStatus.PAID) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单号：" + dto.getIntegralOrderNo() + " 的状态已改变");
            }
            dto.validParam();
            boolean update = this.iIntegralOrderService.lambdaUpdate()
                    .eq(IntegralOrder::getNo, dto.getIntegralOrderNo())
                    .set(IntegralOrder::getStatus, IntegralOrderStatus.ON_DELIVERY)
                    .set(IntegralOrder::getIntegralOrderDeliverType, dto.getIntegralOrderDeliverType())
                    .set(IntegralOrder::getExpressName, dto.getExpressName())
                    .set(IntegralOrder::getExpressCompanyName, dto.getExpressCompanyName())
                    .set(IntegralOrder::getExpressNo, dto.getExpressNo())
                    .set(IntegralOrder::getDeliveryTime, LocalDateTime.now())
                    .update();
            if (!update) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单号：" + dto.getIntegralOrderNo() + " 的订单发货失败");
            }
            rabbitTemplate.convertAndSend(
                    IntegralOrderRabbit.INTEGRAL_ORDER_SEND_COMPLETION.exchange(),
                    IntegralOrderRabbit.INTEGRAL_ORDER_SEND_COMPLETION.routingKey(),
                    new IntegralCompletionDTO()
                            .setIntegralOrderNo(dto.getIntegralOrderNo()),
                    message -> {
                        message.getMessageProperties().setHeader(MessageProperties.X_DELAY,
                                FastJson2.convert(
                                        RedisUtil.getCacheObject(IntegralConstant.INTEGRAL_TIME_OUT),
                                        Long.class
                                )
                        );
                        return message;
                    }
            );
            //小程序发货上传 未开启，不发mq信息发货录入
            if ((!wechatProperties.getMiniAppDeliver()) || one.getAffiliationPlatform() != Platform.WECHAT_MINI_APP) {
                return;
            }
            WxMaOrderShippingService wxMaOrderShippingService = new WxMaOrderShippingServiceImpl(wxMaService);
            WxMaOrderShippingIsTradeManagedResponse tradeManagedResponse = wxMaOrderShippingService.isTradeManaged(wechatProperties.getAppId());
            Boolean tradeManaged = tradeManagedResponse.getTradeManaged();
            IntegralOrderPayment integralOrderPayment = integralOrderPaymentService.lambdaQuery()
                    .eq(IntegralOrderPayment::getOrderNo, one.getNo())
                    .eq(IntegralOrderPayment::getSecPayType, PayType.WECHAT)
                    .isNotNull(IntegralOrderPayment::getOpenId)
                    .one();
            if (integralOrderPayment != null && tradeManaged) {

                // 小程序开通发货信息管理服务 发货信息录入
                WxMaOrderShippingInfoUploadRequest wxMaOrderShippingInfoUploadRequest = new WxMaOrderShippingInfoUploadRequest();
                //上传物流信息的订单
                OrderKeyBean orderKeyBean = new OrderKeyBean();
                orderKeyBean.setOrderNumberType(CommonPool.NUMBER_TWO);
                orderKeyBean.setOutTradeNo(dto.getIntegralOrderNo());
                orderKeyBean.setTransactionId(integralOrderPayment.getSn());
                List<ShippingListBean> shippingList = new ArrayList<>();
                ShippingListBean shipping = new ShippingListBean();
                ContactBean contactBean = new ContactBean();
                contactBean.setReceiverContact(one.getIntegralOrderReceiver().getMobile());
                shipping.setContact(contactBean);
                shipping.setItemDesc(one.getProductName());
                shipping.setTrackingNo(dto.getExpressNo());
                shipping.setExpressCompany(
                        Optional.ofNullable(getExpressCompany().get(dto.getExpressName()))
                                .map(map -> map.get(NUMBER_ZERO))
                                .map(DeliveryVO::getDeliveryId)
                                .orElse(null)
                );
                shippingList.add(shipping);
                wxMaOrderShippingInfoUploadRequest.setPayer(new PayerBean(integralOrderPayment.getOpenId()));
                wxMaOrderShippingInfoUploadRequest.setShippingList(shippingList);
                wxMaOrderShippingInfoUploadRequest.setOrderKey(orderKeyBean);
                wxMaOrderShippingInfoUploadRequest.setLogisticsType(dto.getIntegralOrderDeliverType().getValue().equals(LogisticsType.EXPRESS.getValue()) ? LogisticsType.EXPRESS.getValue() : LogisticsType.VIRTUAL_PRODUCT.getValue());
                wxMaOrderShippingInfoUploadRequest.setDeliveryMode(DeliveryMode.UNIFIED_DELIVERY.getValue());
                wxMaOrderShippingInfoUploadRequest.setUploadTime(ZonedDateTime.now().format(DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT.getDateTimeFormatter()));
                wxMaOrderShippingService.upload(wxMaOrderShippingInfoUploadRequest);
            }
        }
    }


    /**
     * 获取微信快递公司
     *
     * @return 快递公司
     */
    public Map<String, List<DeliveryVO>> getExpressCompany() {
        String deliveryArray = RedisUtil.getCache(
                () -> RedisUtil.getCacheObject(IntegralConstant.INTEGRAL_ORDER_MINI_DELIVERY),
                () -> {
                    try {
                        String url = EXPRESS_COMPANY_URL.concat(wxMaService.getAccessToken());
                        String responseJson = HttpUtil.post(url, "{}");
                        MiniAppDeliveryVO result = FastJson2.convert(responseJson, MiniAppDeliveryVO.class);
                        if (result.getCode() != 0) {
                            throw new GlobalException();
                        } else {
                            return JSONArray.from(result.getDeliveryList()).toString();
                        }
                    } catch (Exception e) {
                        throw new GlobalException();
                    }
                },
                Duration.ofHours(CommonPool.NUMBER_EIGHT),
                IntegralConstant.INTEGRAL_ORDER_MINI_DELIVERY
        );

        List<DeliveryVO> deliveryVOList;
        if (StrUtil.isEmpty(deliveryArray) || CollUtil.isEmpty(deliveryVOList = JSONArray.parseArray(deliveryArray, DeliveryVO.class))) {
            // 物流运动获取失败
            return Map.of();
        }
        return deliveryVOList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(DeliveryVO::getDeliveryName));
    }
}
