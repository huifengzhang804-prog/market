package com.medusa.gruul.addon.invoice.addon;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;

import java.util.Map;

/**
 * 商品服务提供插件支持器
 *
 * @author xiaoq
 * @Description GoodsAddonSupporter.java
 * @date 2022-09-17 09:30
 */
@AddonSupporter(id = "invoiceAddonSupporter")
public interface InvoiceAddonSupporter {

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return {@link  JSONObject}
     */
    @AddonMethod(returnType = JSONObject.class)
    Map<String, String> getPurchaseOrderById(String orderNo);

    @AddonMethod(returnType = Void.class)
    void syncSupplyOrderInvoiceStatus(String orderNo, Integer invoiceStatusCode);
}
