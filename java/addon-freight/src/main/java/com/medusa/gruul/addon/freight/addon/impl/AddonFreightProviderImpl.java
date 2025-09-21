package com.medusa.gruul.addon.freight.addon.impl;

import cn.hutool.json.JSONObject;
import com.kuaidi100.sdk.response.PrintBaseResp;
import com.medusa.gruul.addon.freight.addon.AddonFreightProvider;
import com.medusa.gruul.addon.freight.constant.FreightConstant;
import com.medusa.gruul.addon.freight.service.LogisticsExpressPrintService;
import com.medusa.gruul.addon.freight.service.LogisticsNodesService;
import com.medusa.gruul.addon.freight.service.LogisticsTemplateInfoService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ProviderImpl DubboService
 *
 * @author xiaoq
 * @Description AddonFreightProviderImpl.java
 * @date 2023-05-08 17:12
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonFreightProviderImpl implements AddonFreightProvider {

    private final LogisticsExpressPrintService logisticsExpressPrintService;
    private final LogisticsNodesService logisticsNodesService;


    private LogisticsTemplateInfoService logisticsTemplateInfoService;

    @Lazy
    @Autowired
    public void setLogisticsTemplateInfoService(LogisticsTemplateInfoService logisticsTemplateInfoService) {
        this.logisticsTemplateInfoService = logisticsTemplateInfoService;
    }

    /**
     * 运费计算
     *
     * @param platformFreight 运费计算param
     * @return <Map<key sopId+":"+物流模板id ,value 当前模板价格>>
     */
    @Override
    @Log("运费计算")
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "distributionCost", order = 0, filter = "EXPRESS")
    public Map<String, BigDecimal> freightCalculation(PlatformFreightParam platformFreight) {
        return logisticsTemplateInfoService.freightCalculation(platformFreight);
    }

    /**
     * 物流打印发货
     *
     * @param sendDeliveryDTO 打印数据
     * @return 物流订单号
     */
    @Override
    @Log("打印并发货")
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "printDeliverGoods")
    public String printDeliverGoods(SendDeliveryDTO sendDeliveryDTO) {
        PrintBaseResp<JSONObject> response = logisticsExpressPrintService.printDeliverGoods(sendDeliveryDTO);
        if (!FreightConstant.KUAIDI_CODE.equals(response.getReturnCode()) && !response.isResult()) {
            throw new GlobalException(response.getMessage());
        }
        return response.getData().getStr(FreightConstant.KUAIDI_KEY);
    }

    @Override
    @Log("查询物流签收信息")
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "queryPackageSignedInfo")
    public Boolean queryPackageSignedInfo(String companyCode, String waybillNo, String recipientsPhone) {
        return logisticsNodesService.queryPageSignedInfo(companyCode, waybillNo, recipientsPhone);

    }


}
