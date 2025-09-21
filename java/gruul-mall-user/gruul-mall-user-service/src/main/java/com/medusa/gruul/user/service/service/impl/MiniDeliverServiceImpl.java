package com.medusa.gruul.user.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaOrderShippingService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaOrderShippingServiceImpl;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.OrderKeyBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.PayerBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.ShippingListBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.WxMaOrderShippingInfoUploadRequest;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.date.DatePattern;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.order.api.model.wechat.logistics.DeliveryMode;
import com.medusa.gruul.order.api.model.wechat.logistics.LogisticsType;
import com.medusa.gruul.user.service.service.MiniDeliverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * 小程序发货服务实现层
 *
 * @author xiaoq
 * @Description MiniDeliverServiceImpl.java
 * @date 2024-01-10 17:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniDeliverServiceImpl implements MiniDeliverService {
    private final WxMaService wxMaService;

    private final WechatProperties wechatProperties;

    public void miniDeliver(String transactionId, String openId, String desc, Platform platform) {
        log.warn("transactionId{} ------ openId{} ---------desc{}--------platform{}", transactionId, openId, desc, platform);
        if (platform == Platform.WECHAT_MINI_APP && openId != null) {
            //小程序是否已开通发货信息管理服务
            WxMaOrderShippingService wxMaOrderShippingService = new WxMaOrderShippingServiceImpl(wxMaService);
            try {
                WxMaOrderShippingIsTradeManagedResponse tradeManagedResponse = wxMaOrderShippingService.isTradeManaged(wechatProperties.getAppId());
                // 发货信息录入
                if (tradeManagedResponse.getTradeManaged()) {
                    WxMaOrderShippingInfoUploadRequest wxMaOrderShippingInfo = new WxMaOrderShippingInfoUploadRequest();
                    //上传物流信息的订单
                    OrderKeyBean orderKeyBean = new OrderKeyBean();
                    orderKeyBean.setOrderNumberType(CommonPool.NUMBER_TWO);
                    orderKeyBean.setTransactionId(transactionId);
                    ShippingListBean shippingListBean = new ShippingListBean();
                    shippingListBean.setItemDesc(desc);
                    wxMaOrderShippingInfo.setOrderKey(orderKeyBean);
                    wxMaOrderShippingInfo.setShippingList(Collections.singletonList(shippingListBean));
                    wxMaOrderShippingInfo.setLogisticsType(LogisticsType.VIRTUAL_PRODUCT.getValue());
                    wxMaOrderShippingInfo.setDeliveryMode(DeliveryMode.UNIFIED_DELIVERY.getValue());
                    wxMaOrderShippingInfo.setUploadTime(ZonedDateTime.now().format(DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT.getDateTimeFormatter()));
                    wxMaOrderShippingInfo.setPayer(new PayerBean(openId));
                    //发货信息录入
                    wxMaOrderShippingService.upload(wxMaOrderShippingInfo);
                }
            } catch (WxErrorException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
