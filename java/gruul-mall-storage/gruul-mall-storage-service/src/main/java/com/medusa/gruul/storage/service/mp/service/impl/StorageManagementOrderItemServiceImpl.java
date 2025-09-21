package com.medusa.gruul.storage.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrderItem;
import com.medusa.gruul.storage.service.mp.mapper.StorageManagementOrderItemMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageManagementOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 仓储管理订单子项ServiceImpl
 *
 * @author xiaoq
 * @Description StorageManagementOrderItemServiceImpl.java
 * @date 2023-07-26 16:11
 */
@Service
@RequiredArgsConstructor
public class StorageManagementOrderItemServiceImpl extends ServiceImpl<StorageManagementOrderItemMapper, StorageManagementOrderItem> implements IStorageManagementOrderItemService {
}
