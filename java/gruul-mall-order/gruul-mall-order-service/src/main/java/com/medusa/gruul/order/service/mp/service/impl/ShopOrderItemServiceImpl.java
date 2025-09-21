package com.medusa.gruul.order.service.mp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.ConsignmentProductHotSoldDTO;
import com.medusa.gruul.order.api.model.ConsignmentProductHotSoldShopDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;
import com.medusa.gruul.order.service.mp.mapper.ShopOrderItemMapper;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 服务实现类
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class ShopOrderItemServiceImpl extends ServiceImpl<ShopOrderItemMapper, ShopOrderItem> implements IShopOrderItemService {


    @Override
    public List<ShopOrderItem> undelivered(DeliveryQueryBO query) {
        return this.lambdaQuery()
                .eq(ShopOrderItem::getOrderNo, query.getOrderNo())
                .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                .exists(
                        "SELECT 1 FROM t_order AS ord WHERE ord.`no` = t_shop_order_item.order_no AND ord.`status` = " + OrderStatus.PAID.getValue()
                )
                .exists(
                        StrUtil.isNotBlank(query.getShopOrderNo()),
                        "SELECT 1 FROM t_shop_order AS shopOrder WHERE shopOrder.shop_id = t_shop_order_item.shop_id AND shopOrder.order_no = t_shop_order_item.order_no AND shopOrder.`no` = {0}",
                        query.getShopOrderNo()
                )
                .eq(query.getSellType() != null, ShopOrderItem::getSellType, query.getSellType())
                .eq(query.getShopId() != null, ShopOrderItem::getShopId, query.getShopId())
                .eq(query.getSupplierId() != null, ShopOrderItem::getSupplierId, query.getSupplierId())
                .list();
    }

    @Override
    public ShopOrderItem getCompleteItem(String orderNo, Long shopId, Long itemId) {
        return baseMapper.getCompleteItem(orderNo, shopId, itemId);
    }

    @Override
    public Long getProductBoughtNumCount(Long userId, ShopProductKey shopProductKey) {
        return baseMapper.getProductBoughtNumCount(userId, shopProductKey);
    }

    @Override
    public Long getProductSkuBoughtNumCount(Long userId, ShopProductSkuKey shopProductSkuKey) {
        return baseMapper.getProductSkuBoughtNumCount(userId, shopProductSkuKey);
    }

    @Override
    public List<OrderDeliveryItemDTO> getDeliverGoodsInfo(String no, Long shopId) {
        return baseMapper.queryDeliverGoodsInfo(no, shopId);
    }

    /**
     * 以代销商品+店铺为维度,统计时间范围内热销排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldDTO}
     */
    @Override
    public List<ConsignmentProductHotSoldDTO> countConsignmentProductHotSoldList(LocalDateTime beginTime, LocalDateTime endTime) {
        return baseMapper.countConsignmentProductHotSoldList(beginTime, endTime);
    }

    /**
     * 以店铺为维度,统计时间范围内代销商品的营业额排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldShopDTO}
     */
    @Override
    public List<ConsignmentProductHotSoldShopDTO> countConsignmentProductHotSoldShopList(LocalDateTime beginTime, LocalDateTime endTime) {
        return baseMapper.countConsignmentProductHotSoldShopList(beginTime, endTime);
    }

    /**
     * 统计订单数量-和订单列表保持一致
     *
     * @return {@link Long}
     */
    @Override
    public Long countCompletedOrderNum() {
        return baseMapper.countCompletedOrderNum();
    }

    @Override
    public List<ShopOrderItem> queryShopOrderItemAfsStatus(Set<Long> shopOrderItemIds) {
        return lambdaQuery().select(
                        ShopOrderItem::getId
                        , ShopOrderItem::getAfsStatus)
                .in(ShopOrderItem::getId, shopOrderItemIds)
                .list();
    }

}
