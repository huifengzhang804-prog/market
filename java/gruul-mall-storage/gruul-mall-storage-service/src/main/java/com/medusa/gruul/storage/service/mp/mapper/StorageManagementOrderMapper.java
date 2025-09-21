package com.medusa.gruul.storage.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-07-25 15:46
 */
public interface StorageManagementOrderMapper extends BaseMapper<StorageManagementOrder> {
    StorageManagementOrder queryStorageManagementOrderDetail(@Param("id") Long id, @Param("shopId") Long shopId);
}
