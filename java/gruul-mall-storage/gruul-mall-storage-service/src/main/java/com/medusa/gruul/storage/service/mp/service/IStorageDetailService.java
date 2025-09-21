package com.medusa.gruul.storage.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;

import java.util.List;

/**
 * IStorageDetailService.java
 *
 * @author xiaoq
 * @Description IStorageDetailService.java
 * @date 2023-07-27 14:43
 */
public interface IStorageDetailService extends IService<StorageDetail> {
    /**
     * 更新库存明细状态
     *
     * @param shopProductKeyList 更新条件
     */
     void updateStorageDetail(List<ShopProductKeyDTO> shopProductKeyList);
}
