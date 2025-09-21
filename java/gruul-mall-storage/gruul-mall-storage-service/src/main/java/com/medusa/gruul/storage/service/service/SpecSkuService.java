package com.medusa.gruul.storage.service.service;

import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;

/**
 * 规格与sku
 *
 * @author 张治保
 * date 2022/7/12
 */
public interface SpecSkuService {
    /**
     * 保存/更新 商品sku与spec规格信息
     *
     * @param storageSpecSku 商品规格与sku信息
     */
    void saveOrUpdateSpecSku(StorageSpecSkuDTO storageSpecSku);

}
