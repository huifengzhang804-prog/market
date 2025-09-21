package com.medusa.gruul.addon.bargain.mp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.bargain.constant.BargainConstant;
import com.medusa.gruul.addon.bargain.model.enums.BargainSponsorSkuStatus;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.entity.BargainPaymentInfo;
import com.medusa.gruul.addon.bargain.mp.mapper.BargainPaymentInfoMapper;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainPaymentInfoService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.medusa.gruul.addon.bargain.utils.BargainRedisUtils;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 砍价活动支付信息 服务实现类
 *
 * @author WuDi
 * @since 2023-03-25
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BargainPaymentInfoServiceImpl extends ServiceImpl<BargainPaymentInfoMapper, BargainPaymentInfo> implements IBargainPaymentInfoService {


    private final IBargainService bargainService;
    private final IBargainOrderService bargainOrderService;

    /**
     * 砍价活动支付信息
     *
     * @param orderPaidBroadcast 订单支付信息
     */
    @Override
    public void bargainPaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast) {
        Long activityId = orderPaidBroadcast.getActivityId();
        Long shopId = orderPaidBroadcast.getShopPayAmounts().get(CommonPool.NUMBER_ZERO).getShopId();
        Bargain bargain = bargainService.lambdaQuery()
                .select(Bargain::getStatus, Bargain::getEndTime, Bargain::getBargainValidityPeriod)
                .eq(Bargain::getId, activityId)
                .eq(Bargain::getShopId, shopId)
                .one();
        if (bargain == null) {
            return;
        }
        String orderNo = orderPaidBroadcast.getOrderNo();
        boolean save = this.save(
                new BargainPaymentInfo()
                        .setShopId(shopId)
                        .setActivityId(activityId)
                        .setOrderNo(orderNo)
                        .setAmountReceivable(orderPaidBroadcast.getPayAmount())
                        .setUserId(orderPaidBroadcast.getBuyerId())
        );
        if (!save) {
            throw new GlobalException("砍价活动支付信息保存失败");
        }
        BargainOrder bargainOrder = bargainOrderService.lambdaQuery()
                .select(BargainOrder::getProductId)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getOrderNo, orderNo)
                .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                .one();
        if (bargainOrder == null) {
            return;
        }
        // 更新砍价订单状态
        boolean update = bargainOrderService.lambdaUpdate()
                .set(BargainOrder::getBargainStatus, BargainStatus.SUCCESSFUL_BARGAIN)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getOrderNo, orderNo)
                .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                .update();
        if (!update) {
            throw new GlobalException("砍价订单状态更新失败");
        }

        //todo 非严谨性处理 非原子性操作
        String redisKey = BargainRedisUtils.getBargainSponsorProductSkuKey(
                orderPaidBroadcast.getBuyerId(),
                activityId,
                shopId,
                bargainOrder.getProductId()
        );
        RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();
        if (BooleanUtil.isTrue(redisTemplate.hasKey(redisKey))) {
            // 更新砍价发起人状态为已成功
            redisTemplate.opsForHash()
                    .put(
                            redisKey,
                            BargainConstant.BARGAIN_SPONSOR_STATUS, BargainSponsorSkuStatus.SUCCESSFUL_BARGAIN
                    );
        }
    }

    @Override
    public void bargainRefundInfo(OrderInfo orderInfo) {
        Long activityId = orderInfo.getActivityId();
        Long shopId = orderInfo.getShopId();
        boolean exists = bargainService.lambdaQuery()
                .eq(Bargain::getId, activityId)
                .eq(Bargain::getShopId, shopId)
                .exists();
        if (!exists) {
            return;
        }
        this.lambdaUpdate()
                .eq(BargainPaymentInfo::getActivityId, activityId)
                .eq(BargainPaymentInfo::getShopId, shopId)
                .eq(BargainPaymentInfo::getOrderNo, orderInfo.getOrderNo())
                .remove();
    }

}
