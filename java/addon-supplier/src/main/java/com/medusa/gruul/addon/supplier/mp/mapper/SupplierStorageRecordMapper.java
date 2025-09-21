package com.medusa.gruul.addon.supplier.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierStorageRecord;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

/**
 * @author 张治保
 * date 2023/7/27
 */
public interface SupplierStorageRecordMapper extends BaseMapper<SupplierStorageRecord> {

  /**
   * 查询已经存在入库商品的采购订单
   * @param orderNos
   * @return
   */
  Set<String> queryHasProductStorageOrder(@Param("orderNos") Set<String> orderNos);
}
