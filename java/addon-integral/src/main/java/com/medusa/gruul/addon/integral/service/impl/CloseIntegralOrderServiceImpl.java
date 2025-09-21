package com.medusa.gruul.addon.integral.service.impl;

import com.medusa.gruul.addon.integral.model.bo.IntegralOrderDetailInfoBO;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.addon.integral.service.CloseIntegralOrderService;
import com.medusa.gruul.addon.integral.service.CommonIntegralProductService;
import com.medusa.gruul.addon.integral.service.CreateIntegralOrderService;
import com.medusa.gruul.addon.integral.util.IntegralUtil;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 16:49
 **/

@Service
@RequiredArgsConstructor
public class CloseIntegralOrderServiceImpl implements CloseIntegralOrderService {

    private final IIntegralOrderService iIntegralOrderService;

    private final CommonIntegralProductService commonIntegralProductService;

    private final CreateIntegralOrderService createIntegralOrderService;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeIntegralOrderPaidTimeout(IntegralOrderDetailInfoBO integralOrderDetailInfoBO) {

        this.closeIntegralOrder(integralOrderDetailInfoBO, IntegralOrderStatus.SYSTEM_CLOSED);
    }


    /**
     * 1、获取状态为未支付 订单号为指定的订单
     * 2、更新订单状态为关闭
     * 3、归还redis缓存中的库存
     * 4、归还数据库中的库存和销量
     * 5、归还用户积分
     */
    @SuppressWarnings("unchecked")
    @Override
    public void closeIntegralOrder(IntegralOrderDetailInfoBO integralOrderDetailInfoBO, IntegralOrderStatus integralOrderStatus) {

        Long buyerId = integralOrderDetailInfoBO.getBuyerId();
        String integralOrderNo = integralOrderDetailInfoBO.getIntegralOrderNo();
        //1、获取未支付的订单
        IntegralOrder unpaidIntegralOrder = this.iIntegralOrderService.getUnpaidIntegralOrder(integralOrderNo);
        if (unpaidIntegralOrder == null) {
            return;
        }

        //2、更新订单
        this.iIntegralOrderService.lambdaUpdate()
                .eq(IntegralOrder::getNo, integralOrderNo)
                .eq(IntegralOrder::getBuyerId, buyerId)
                .set(IntegralOrder::getStatus, integralOrderStatus)
                .update();

        //3、归还redis中的库存
        //从redis中获取可用的库存
        IntegralProduct integralProductInfo = this.commonIntegralProductService.getIntegralProductInfo(unpaidIntegralOrder.getProductId());

        if (integralProductInfo == null) {
            throw new GlobalException("该积分商品不存在");
        }
        //积分商品在redis缓存中的key
        String cacheKey = IntegralUtil.integralProductCacheKey(unpaidIntegralOrder.getProductId());

        //判断缓存值是否存在
        IntegralProduct integralProductCache = RedisUtil.getCacheMap(cacheKey, IntegralProduct.class);

        if (integralProductCache != null) {
            RedisUtil.executePipelined(
                    //加回库存
                    operations ->
                            operations.opsForHash().increment(cacheKey, IntegralConstant.INTEGRAL_PRODUCT_STOCK, unpaidIntegralOrder.getNum())
            );
        }

        //4、归还数据库中的库存和销量
        this.createIntegralOrderService.updateIntegralProductDBStockAndSalesVolume(unpaidIntegralOrder, false);


        DataChangeMessage dm = new DataChangeMessage();
        dm.setUserId(unpaidIntegralOrder.getBuyerId());
        dm.setValue(unpaidIntegralOrder.getPrice());
        dm.setChangeType(ChangeType.INCREASE);
        dm.setExtendInfo(GainIntegralType.ORDER_CANCEL.name());

        //5、发送归还用户积分的消息 生成积分变动明细
        this.rabbitTemplate.convertAndSend(
                UserRabbit.USER_INTEGRAL_CHANGE.exchange(),
                UserRabbit.USER_INTEGRAL_CHANGE.routingKey(),
                new IntegralChangeDTO()
                        .setNo(unpaidIntegralOrder.getNo())
                        .setDataChangeMessage(dm)
        );


    }
}
