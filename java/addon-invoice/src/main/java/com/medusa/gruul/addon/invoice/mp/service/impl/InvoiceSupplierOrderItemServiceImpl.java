package com.medusa.gruul.addon.invoice.mp.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.invoice.mp.entity.SupplierOrderItem;
import com.medusa.gruul.addon.invoice.mp.mapper.InvoiceSupplierOrderItemMapper;
import com.medusa.gruul.addon.invoice.mp.service.ISupplierOrderItemService;
import org.springframework.stereotype.Service;


@Service
@DS("supplier")
public class InvoiceSupplierOrderItemServiceImpl extends ServiceImpl<InvoiceSupplierOrderItemMapper, SupplierOrderItem> implements ISupplierOrderItemService {

}
