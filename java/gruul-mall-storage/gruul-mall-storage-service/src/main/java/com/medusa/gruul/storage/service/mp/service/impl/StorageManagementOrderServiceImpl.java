package com.medusa.gruul.storage.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;
import com.medusa.gruul.storage.service.mp.mapper.StorageManagementOrderMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageManagementOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 仓储管理订单服务实现类
 *
 * @author xiaoq
 * @Description StorageManagementOrderServiceImpl.java
 * @date 2023-07-25 15:44
 */
@Service
@RequiredArgsConstructor
public class StorageManagementOrderServiceImpl extends ServiceImpl<StorageManagementOrderMapper, StorageManagementOrder> implements IStorageManagementOrderService {
    @Override
    public StorageManagementOrder getStorageManagementOrderDetail(Long id, Long shopId) {
        return this.baseMapper.queryStorageManagementOrderDetail(id, shopId);
    }
}
