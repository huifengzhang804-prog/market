package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.dto.IntegralOrderDeliveryDTO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.List;

/**
 * @author shishuqian
 * date 2023/2/6
 * time 9:59
 * 订单发货service
 **/
public interface IntegralOrderDeliverService {


    /**
     * 所有待发货商品，批量发货
     *
     * @return 待发货积分订单列表
     */
    List<IntegralOrder> undeliverBatch();


    /**
     * 根据订单号，获取该待发货订单
     *
     * @param integralOrderNo 积分订单号
     * @return 积分订单信息
     */
    IntegralOrder undeliver(String integralOrderNo);

    /**
     * 根据订单号，确认该订单收获
     *
     * @param integralOrderNo 积分订单号
     */
    void complete(Boolean isSystem, String integralOrderNo);

    /**
     * 订单发货 批量发货  手动发货
     *
     * @param integralOrderDeliveryDTOList 要发货的积分订单列表dto
     */
    void deliver(List<IntegralOrderDeliveryDTO> integralOrderDeliveryDTOList) throws WxErrorException;
}
