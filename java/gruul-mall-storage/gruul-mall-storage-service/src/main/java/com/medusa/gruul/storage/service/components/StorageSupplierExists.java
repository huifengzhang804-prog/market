package com.medusa.gruul.storage.service.components;

import com.medusa.gruul.common.redis.util.RedisExist;
import com.medusa.gruul.storage.service.model.constant.StorageConstant;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * @since 2024/6/21
 */
@Component
public class StorageSupplierExists extends RedisExist<Long> {
    public StorageSupplierExists() {
        super(StorageConstant.CACHE_KEY_SUPPLIER_EXISTS);
    }
}
