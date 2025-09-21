package com.medusa.gruul.addon.integral.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderQueryDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderRemarkDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.model.vo.IntegralOrderListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;

import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2023-01-31 14:51
 */
public interface IIntegralOrderService extends IService<IntegralOrder> {

    /**
     * 获取未支付的订单信息
     *
     * @param integralOrderNo 积分订单号
     * @return 积分订单信息
     * @author shishuqian
     */
    IntegralOrder getUnpaidIntegralOrder(String integralOrderNo);

    /**
     * 积分订单批量备注
     *
     * @param integralOrderRemark 积分订单备注信息dto
     * @author shishuqian
     */
    void integralOrderBatchRemark(IntegralOrderRemarkDTO integralOrderRemark);


    /**
     * 分页查询订单信息
     *
     * @param integralOrderQueryDTO 积分订单查询dto
     * @return 积分订单分页列表
     * @author shishuqian
     */
    IPage<IntegralOrderListVO> integralOrderPage(IntegralOrderQueryDTO integralOrderQueryDTO);

    /**
     * 根据订单号，查询积分订单的详细信息
     *
     * @param integralOrderNo 积分订单号
     * @return 积分订单信息
     * @author shishuqian
     */
    IntegralOrder getIntegralOrderDetailInfo(String integralOrderNo);

    /**
     * 积分订单批量发货导入所有未发货积分订单
     *
     * @param paid 订单状态
     * @return 积分订单
     */
    List<IntegralOrder> unDeliverBatch(IntegralOrderStatus paid);

    /**
     * 查询待发货订单信息
     *
     * @param orderNo 订单号
     * @param status  订单状态
     * @return 待发货订单
     */
    IntegralOrder undeliver(String orderNo, IntegralOrderStatus status);

    /**
     * 获取警告订单数量
     * @return  警告订单数量
     */
    Long warningIntegralOrder();


}
