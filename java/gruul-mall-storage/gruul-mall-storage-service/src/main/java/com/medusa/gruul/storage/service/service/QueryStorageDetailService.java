package com.medusa.gruul.storage.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.storage.service.model.param.StorageDetailParam;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;

/**
 * QueryStorageDetailService
 *
 * @author xiaoq
 * @Description QueryStorageDetailService.java
 * @date 2023-07-27 14:42
 */
public interface QueryStorageDetailService {
    IPage<StorageDetail> getStorageDetailList(StorageDetailParam storageDetailParam);

    /**
     * 导出
     * @param storageDetailParam
     */
    void export(StorageDetailParam storageDetailParam);
}
