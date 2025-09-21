package com.medusa.gruul.addon.supplier.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.supplier.model.vo.OrderStorageVO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierStorageRecord;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierStorageRecordMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierStorageRecordService;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * date 2023/7/27
 */
@Component
public class SupplierStorageRecordServiceImpl extends ServiceImpl<SupplierStorageRecordMapper, SupplierStorageRecord> implements ISupplierStorageRecordService {


  @Override
  public Set<String> queryHasProductStorageOrder(Set<String> orderNos) {
    return baseMapper.queryHasProductStorageOrder(orderNos);
  }

  @Override
  public Map<String, Integer> queryStockInCountInfo(Set<String> orderNos) {
    List<SupplierStorageRecord> list = lambdaQuery()
        .in(SupplierStorageRecord::getOrderNo, orderNos)
        .select(SupplierStorageRecord::getOrderNo, SupplierStorageRecord::getSkuRecords)
        .list();
    if (CollectionUtil.isEmpty(list)) {
      return Maps.newHashMap();
    }
    Map<String, Integer> result = list.stream()
        .collect(Collectors.toMap(
            SupplierStorageRecord::getOrderNo, // 键：订单号
            supplierStorageRecord -> supplierStorageRecord.getSkuRecords()
                .values().stream()
                .mapToInt(Integer::intValue)
                .sum()  // 值：SKU数量的总和
        ));
    return result;
  }

  @Override
  public Map<Long, Long> queryProductStockInCountInfo(Set<String> orderNos) {
    List<SupplierStorageRecord> list = lambdaQuery()
        .in(SupplierStorageRecord::getOrderNo, orderNos)
        .select(SupplierStorageRecord::getOrderNo, SupplierStorageRecord::getSkuRecords)
        .list();
    if (CollectionUtil.isEmpty(list)) {
      return Maps.newHashMap();
    }
    Map<Long, Long> result = list.stream()
        .flatMap(supplierStorageRecord -> supplierStorageRecord.getSkuRecords().entrySet().stream())
        .collect(Collectors.toMap(
            entry -> entry.getKey().getProductId(),
            entry->Long.valueOf(entry.getValue()),
            Long::sum
        ));

    return result;
  }


}
