package com.medusa.gruul.goods.service.service;

import com.medusa.gruul.goods.api.model.enums.ProductAuditType;

/**
 * 商品审核Service
 *
 * @author xiaoq
 * @Description ProductAuditService.java
 * @date 2023-10-19 13:37
 */
public interface ProductAuditService {
    /**
     * 商品审核配置
     *
     * @return 审核类型
     */
    ProductAuditType productAuditSetting();

    /**
     * 商品审核配置修改
     *
     * @param productAuditType 审核类型
     */
    void updateProductAuditSetting(ProductAuditType productAuditType);

}
