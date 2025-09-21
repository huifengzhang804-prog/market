package com.medusa.gruul.goods.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.goods.api.entity.SupplierRateRecord;
import com.medusa.gruul.goods.service.mp.mapper.SupplierRateRecordMapper;
import com.medusa.gruul.goods.service.mp.service.ISupplierRateRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 供应商评分记录实现层
 *
 * @author xiaoq
 * @Description SupplierRateRecordImpl.java
 * @date 2022-12-07 13:34
 */
@Service
@RequiredArgsConstructor
public class SupplierRateRecordImpl extends ServiceImpl<SupplierRateRecordMapper, SupplierRateRecord> implements ISupplierRateRecordService {
}
