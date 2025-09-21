package com.medusa.gruul.addon.supplier.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierStorageRecord;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

/**
 * @author 张治保
 * date 2023/7/27
 */
public interface ISupplierStorageRecordService extends IService<SupplierStorageRecord> {

  /**
   * 查询已经存在入库商品的订单
   * @param orderNos
   * @return
   */
  Set<String> queryHasProductStorageOrder(Set<String> orderNos);

  /**
   * 查询入库商品数量
   * @param orderNos
   * @return
   */
  Map<String, Integer> queryStockInCountInfo(Set<String> orderNos);

  Map<Long, Long> queryProductStockInCountInfo(Set<String> orderNos);

}
