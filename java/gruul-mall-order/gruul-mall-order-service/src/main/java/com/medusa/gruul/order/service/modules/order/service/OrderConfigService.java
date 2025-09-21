package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.global.model.o.FormInput;
import com.medusa.gruul.order.api.entity.OrderForm;
import com.medusa.gruul.order.api.entity.OrderTimeout;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/2/8
 */
public interface OrderConfigService {

    /**
     * 配置订单超时时间
     *
     * @param timeout 超时时间参数
     */
    void timeout(OrderTimeout timeout);

    /**
     * 查询订单超时时间配置
     *
     * @return 订单超时时间配置
     */
    OrderTimeout timeout();


    /**
     * 商铺交易信息编辑
     *
     * @param shopId    店铺id
     * @param orderForm orderForm
     */
    void orderForm(Long shopId, OrderForm orderForm);

    /**
     * 根据店铺id列表 批量查询表单列表
     *
     * @param shopIds 店铺id列表
     * @return 批量查询结果
     */
    Map<Long, List<FormInput>> orderFormsInput(Set<Long> shopIds);

    /**
     * 根据店铺id列表 批量查询下单表单
     *
     * @param shopIds 店铺id列表
     * @return 批量查询结果
     */
    Map<Long, OrderForm> orderForms(Set<Long> shopIds);

    /**
     * 查询店铺交易设置
     *
     * @param shopId 店铺id
     * @return 店铺交易设置
     */
    OrderForm orderForm(Long shopId);
}
