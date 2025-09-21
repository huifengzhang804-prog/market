package com.medusa.gruul.addon.supplier.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderItem;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierOrderItemMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderItemService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/7/20
 */
@Service
public class SupplierOrderItemServiceImpl extends ServiceImpl<SupplierOrderItemMapper, SupplierOrderItem> implements ISupplierOrderItemService {
}
