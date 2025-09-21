package com.medusa.gruul.order.service.modules.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.service.model.dto.OrderPlatFormDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.vo.OrderPlatFormDeliveryVO;

/**
 * 订单查询服务
 *
 * @author 张治保
 * date 2022/6/16
 */
public interface QueryOrderCallShopRpcService {

    /**
     * 订单分页查询
     *
     * @param queryPage 查询条件
     * @param export    是否是导出查询
     * @return 分页查询结果
     */
    IPage<Order> orderPage(boolean export, OrderQueryDTO queryPage);


    /**
     * 根据订单号，查询自营店铺订单和自营供货商订单
     *
     * @param dto 订单参数
     * @return 返回自营店铺订单和自营供货商订单
     */
    OrderPlatFormDeliveryVO getPlatFormDeliveryOrder(OrderPlatFormDeliveryDTO dto);
}
