package com.medusa.gruul.addon.platform.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;

import java.util.Set;

/**
 * PlatformAddonSupporter.java
 *
 * @author xiaoq
 * @Description PlatformAddonSupporter
 * @date 2023-08-03 14:47
 */
@AddonSupporter(service = "gruul-mall-addon-platform", id = "platformAddonSupporter")
public interface PlatformAddonSupporter {
    /**
     * 获取供应商签约类目商品是否存在
     *
     * @param collect 签约类目二级ids
     * @param shopId  供应商id
     * @return 是否存在
     */
    Boolean getSupplierSigningCategoryProduct(Set<Long> collect, Long shopId);
}
