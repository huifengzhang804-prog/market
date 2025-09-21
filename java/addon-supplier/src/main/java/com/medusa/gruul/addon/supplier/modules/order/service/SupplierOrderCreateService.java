package com.medusa.gruul.addon.supplier.modules.order.service;

import com.medusa.gruul.addon.supplier.model.dto.OrderCreateDTO;
import com.medusa.gruul.addon.supplier.model.vo.OrderCreateVO;

/**
 * 供应商订单创建服务
 *
 * @author 张治保
 * date 2023/7/19
 */
public interface SupplierOrderCreateService {

    /**
     * 创建订单 返回订单号
     *
     * @param orderCreate 订单创建参数
     * @return 订单创建标识信息
     */
    OrderCreateVO createOrder(OrderCreateDTO orderCreate);

    /**
     * 轮训查询订单创建结果
     *
     * @param mainNo 订单主单号
     * @return 是否创建成功
     */
    boolean createResult(String mainNo);


}
