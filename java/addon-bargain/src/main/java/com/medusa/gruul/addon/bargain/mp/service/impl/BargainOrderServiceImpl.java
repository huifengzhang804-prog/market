package com.medusa.gruul.addon.bargain.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.bargain.model.dto.BargainOrderQueryDTO;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.model.vo.BargainOrderVO;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.mapper.BargainOrderMapper;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 砍价订单 服务实现类
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Service
@RequiredArgsConstructor
public class BargainOrderServiceImpl extends ServiceImpl<BargainOrderMapper, BargainOrder> implements IBargainOrderService {

    private final IBargainService bargainService;

    /**
     * 分页查询砍价订单
     *
     * @param bargainOrderQuery 砍价订单查询参数
     * @return 砍价订单分页数据
     */
    @Override
    public IPage<BargainOrder> getBargainOrderPage(BargainOrderQueryDTO bargainOrderQuery) {
        return this.lambdaQuery()
                .eq(BargainOrder::getShopId, ISystem.shopIdOpt().get())
                .eq(bargainOrderQuery.getBargainStatus() != null, BargainOrder::getBargainStatus, bargainOrderQuery.getBargainStatus())
                .like(bargainOrderQuery.getKeyword() != null, BargainOrder::getUserNickname, bargainOrderQuery.getKeyword())
                .orderByDesc(BargainOrder::getPublishBargainingTime)
                .page(new Page<>(bargainOrderQuery.getCurrent(), bargainOrderQuery.getSize()));
    }

    @Override
    public BargainOrderVO getBargainOrderDetail(Long shopId, Long id) {
        BargainOrder bargainOrder = this.lambdaQuery()
                .eq(BargainOrder::getId, id)
                .eq(BargainOrder::getShopId, shopId)
                .one();
        if (bargainOrder == null) {
            return null;
        }
        Bargain bargain = bargainService.lambdaQuery()
                .select(Bargain::getName)
                .eq(Bargain::getId, bargainOrder.getActivityId())
                .eq(Bargain::getShopId, bargainOrder.getShopId())
                .one();
        return new BargainOrderVO()
                .setId(bargainOrder.getId())
                .setActivityName(bargain.getName())
                .setOrderNo(bargainOrder.getOrderNo())
                .setBargainStatus(bargainOrder.getBargainStatus())
                .setUserHeadPortrait(bargainOrder.getUserHeadPortrait())
                .setUserNickname(bargainOrder.getUserNickname())
                .setSponsorId(bargainOrder.getSponsorId())
                .setActivityId(bargainOrder.getActivityId())
                .setBargainingPeople(bargainOrder.getBargainingPeople())
                .setShopId(bargainOrder.getShopId())
                .setProductId(bargainOrder.getProductId())
                .setProductName(bargainOrder.getProductName())
                .setProductPic(bargainOrder.getProductPic())
                .setSkuId(bargainOrder.getSkuId())
                .setFloorPrice(bargainOrder.getFloorPrice())
                .setPublishBargainingTime(bargainOrder.getPublishBargainingTime())
                .setEndTime(bargainOrder.getEndTime());
    }

    /**
     * 更新砍价状态
     *
     * @param bargainOrder 砍价订单
     */
    @Override
    public void updateBargainStatus(BargainOrder bargainOrder) {
        Long activityId = bargainOrder.getActivityId();
        List<BargainOrder> orders = this.lambdaQuery()
                .select(BargainOrder::getId)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getSponsorId, bargainOrder.getSponsorId())
                .eq(BargainOrder::getShopId, bargainOrder.getShopId())
                .eq(BargainOrder::getProductId, bargainOrder.getProductId())
                .eq(BargainOrder::getSkuId, bargainOrder.getSkuId())
                .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                .list();
        if (CollUtil.isEmpty(orders)) {
            return;
        }
        // 更新状态为砍价失败
        this.lambdaUpdate()
                .set(BargainOrder::getBargainStatus, BargainStatus.FAILED_TO_BARGAIN)
                .in(BargainOrder::getId, orders.stream().map(BargainOrder::getId).toList())
                .eq(BargainOrder::getActivityId, activityId)
                .update();

    }

    @Override
    public void bargainOrderCreateFail(OrderInfo orderInfo) {
        this.lambdaUpdate()
                .set(BargainOrder::getOrderNo, null)
                .eq(BargainOrder::getOrderNo, orderInfo.getOrderNo())
                .eq(BargainOrder::getActivityId, orderInfo.getActivityId())
                .eq(BargainOrder::getSponsorId, orderInfo.getBuyerId())
                .update();
    }
}
