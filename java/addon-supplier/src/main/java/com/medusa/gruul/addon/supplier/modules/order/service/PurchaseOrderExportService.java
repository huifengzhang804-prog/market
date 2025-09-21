package com.medusa.gruul.addon.supplier.modules.order.service;

import com.medusa.gruul.addon.supplier.model.dto.OrderQueryPageDTO;
import com.medusa.gruul.overview.api.enums.ExportDataType;

/**
 * @description: 采购订单导出服务
 * @projectName:addon-supplier
 * @see:com.medusa.gruul.addon.supplier.modules.order.service
 * @author:jipeng
 * @createTime:2024/1/11 9:35
 * @version:1.0
 */
public interface PurchaseOrderExportService {
  /**
   * description 采购订单导出
   * param [query]
   * return java.lang.Long
   * author jipeng
   * createTime 2024/1/11 9:39
   **/
  Long export(OrderQueryPageDTO query, ExportDataType purchaseOrder);
}
