package com.medusa.gruul.order.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.ConsignmentProductHotSoldDTO;
import com.medusa.gruul.order.api.model.ConsignmentProductHotSoldShopDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 服务类
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface IShopOrderItemService extends IService<ShopOrderItem> {
    /**
     * 查询未发货商品 列表
     *
     * @param query 订单号等查询条件信息
     * @return 未发货商品列表
     */
    List<ShopOrderItem> undelivered(DeliveryQueryBO query);

    /**
     * 查询完整的店铺订单商品
     *
     * @param orderNo 订单号
     * @param shopId  店铺id
     * @param itemId  订单商品id
     * @return 店铺订单商品信息
     */
    ShopOrderItem getCompleteItem(String orderNo, Long shopId, Long itemId);

    /**
     * 获取用户购买过的该商品的数量
     *
     * @param userId            用户id
     * @param shopProductSkuKey 商品关键key
     * @return 统计数量
     */
    Long getProductBoughtNumCount(Long userId, ShopProductKey shopProductSkuKey);

    /**
     * 获取用户购买过的该商品SKU的数量
     *
     * @param userId            用户id
     * @param shopProductSkuKey 商品关键key
     * @return 统计数量
     */
    Long getProductSkuBoughtNumCount(Long userId, ShopProductSkuKey shopProductSkuKey);

    /**
     * 获取发货商品信息
     *
     * @param no     订单no
     * @param shopId shopId
     * @return List<OrderDeliveryItemDTO>
     */
    List<OrderDeliveryItemDTO> getDeliverGoodsInfo(String no, Long shopId);

    /**
     * 以代销商品+店铺为维度,统计时间范围内热销排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldDTO}
     */
    List<ConsignmentProductHotSoldDTO> countConsignmentProductHotSoldList(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 以店铺为维度,统计时间范围内代销商品的营业额排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldShopDTO}
     */
    List<ConsignmentProductHotSoldShopDTO> countConsignmentProductHotSoldShopList(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 统计已完成的订单数量
     *
     * @return {@link Long}
     */
    Long countCompletedOrderNum();

    /**
     * 查询店铺订单项的售后状态
     *
     * @param shopOrderItemIds 店铺订单项id集合
     * @return {@link List<ShopOrderItem>}
     */
    List<ShopOrderItem> queryShopOrderItemAfsStatus(Set<Long> shopOrderItemIds);
}
