package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.bo.IntegralOrderDetailInfoBO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderDTO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.common.security.model.bean.SecureUser;

/**
 * 创建积分商城订单
 *
 * @author shishuqian
 * date 2023/2/1
 * time 15:44
 **/
public interface CreateIntegralOrderService {

    /**
     * 创建积分商品订单
     *
     * @param integralOrderDTO 积分订单信息dto
     * @param secureUser       登录用户信息
     * @return 创建的订单号
     * @author shishuqian
     */
    String create(IntegralOrderDTO integralOrderDTO, SecureUser<?> secureUser);

    /**
     * 获取积分订单基本信息
     *
     * @param userId           用户id
     * @param integralOrderDTO 积分订单信息dto
     * @return 创建的积分订单
     * @author shishuqian
     */
    IntegralOrder getOrder(Long userId, IntegralOrderDTO integralOrderDTO);

    /**
     * 减积分商品的库存
     *
     * @param integralOrder    创建的积分商品订单
     * @param integralOrderDTO 积分商品订单dto
     * @author shishuqian
     */
    void tryLockStock(IntegralOrder integralOrder, IntegralOrderDTO integralOrderDTO);


    /**
     * 将积分订单信息保存到数据库中，并设置订单未支付自动取消
     *
     * @param integralOrderDetailInfoBO 积分订单信息bo
     * @author shishuqian
     */
    void saveIntegralOrder2DbBatch(IntegralOrderDetailInfoBO integralOrderDetailInfoBO);

    /**
     * 更新积分商品的库存和销量
     *
     * @param integralOrder 积分订单
     * @param isReduce      是否为减少库存
     * @author shishuqian
     */
    void updateIntegralProductDBStockAndSalesVolume(IntegralOrder integralOrder, boolean isReduce);


    /**
     * 查询订单创建情况 就是查看中订单的缓存还在不在 在
     *
     * @param orderNo 订单号
     * @return 是否已创建
     * true 缓存中已不存在已写入db
     * false缓存中存在
     */
    boolean orderCreation(String orderNo);

    /**
     * 获取积分订单超时时间
     *
     * @return 超时时间
     */
    Long getOvertime();

    /**
     * 修改积分订单超时时间
     *
     * @param time 超时时间
     */
    void updateOvertime(Long time);
}
