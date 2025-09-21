package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.overview.api.enums.ExportDataType;

/**
 * @description: 订单导出服务
 * @projectName:gruul-mall-order
 * @see:com.medusa.gruul.order.service.modules.order.service
 * @author:jipeng
 * @createTime:2024/1/8 10:19
 * @version:1.0
 */
public interface OrderExportService {
    /**
     * description 导出订单基类
     * param [queryPage, exportDataType]
     * return java.lang.Long
     * author jipeng
     * createTime 2024/1/8 10:32
     **/
    Long export(OrderQueryDTO queryPage, ExportDataType exportDataType);
}
