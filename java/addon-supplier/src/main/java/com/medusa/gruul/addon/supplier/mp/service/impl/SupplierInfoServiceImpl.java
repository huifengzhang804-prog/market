package com.medusa.gruul.addon.supplier.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierInfo;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierInfoMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierInfoService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/7/21
 */
@Service
public class SupplierInfoServiceImpl extends ServiceImpl<SupplierInfoMapper, SupplierInfo> implements ISupplierInfoService {
}
