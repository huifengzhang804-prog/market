package com.medusa.gruul.addon.invoice.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;

/**
 * 商品服务提供插件支持器
 *
 * @author xiaoq
 * @Description GoodsAddonSupporter.java
 * @date 2022-09-17 09:30
 */
@AddonSupporter(id = "goodsAddonSupporter")
public interface InvoiceGoodsAddonSupporter {
    @AddonMethod(returnType = Integer.class)
    Integer getNewCreatedProducts(String beginTime, String endTime);
}
