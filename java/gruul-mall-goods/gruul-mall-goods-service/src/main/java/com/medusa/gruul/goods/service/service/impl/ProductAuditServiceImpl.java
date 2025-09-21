package com.medusa.gruul.goods.service.service.impl;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.enums.ProductAuditType;
import com.medusa.gruul.goods.service.service.ProductAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商品审核serviceImpl
 *
 * @author xiaoq
 * @Description ProductAuditServiceImpl.java
 * @date 2023-10-19 13:38
 */
@Service
@RequiredArgsConstructor
public class ProductAuditServiceImpl implements ProductAuditService {
    @Override
    public ProductAuditType productAuditSetting() {
        ProductAuditType productAuditType = FastJson2.convert(
                RedisUtil.getCacheObject(GoodsConstant.GOODS_PRODUCT_AUDIT),
                ProductAuditType.class
        );
        if (productAuditType == null) {
            productAuditType = ProductAuditType.SYSTEM_AUDIT;
            RedisUtil.setCacheObject(GoodsConstant.GOODS_PRODUCT_AUDIT, productAuditType);
        }
        return productAuditType;

    }

    @Override
    public void updateProductAuditSetting(ProductAuditType productAuditType) {
        RedisUtil.setCacheObject(GoodsConstant.GOODS_PRODUCT_AUDIT, productAuditType);
    }
}
