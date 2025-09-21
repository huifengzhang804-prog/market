package com.medusa.gruul.order.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.ConsignmentProductHotSoldDTO;
import com.medusa.gruul.order.api.model.ConsignmentProductHotSoldShopDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper 接口
 *
 * @author 张治保
 * @since 2022-06-08
 */
public interface ShopOrderItemMapper extends BaseMapper<ShopOrderItem> {

    /**
     * 查询未发货商品 列表
     *
     * @param query 订单号等查询条件信息
     * @return 未发货商品列表
     */
    List<ShopOrderItem> undelivered(@Param("query") DeliveryQueryBO query);

    /**
     * 查询完整的店铺订单商品
     *
     * @param orderNo 订单号
     * @param shopId  店铺id
     * @param itemId  订单商品id
     * @return 店铺订单商品信息
     */
    ShopOrderItem getCompleteItem(@Param("orderNo") String orderNo, @Param("shopId") Long shopId, @Param("itemId") Long itemId);


    /**
     * 获取用户购买过的该商品的数量
     *
     * @param userId            用户id
     * @param shopProductSkuKey 商品关键key
     * @return 统计数量
     */
    Long getProductBoughtNumCount(@Param("userId") Long userId, @Param("shopProductSkuKey") ShopProductKey shopProductSkuKey);

    /**
     * 获取用户购买过的该商品SKU的数量
     *
     * @param userId            用户id
     * @param shopProductSkuKey 商品关键key
     * @return 统计数量
     */
    Long getProductSkuBoughtNumCount(@Param("userId") Long userId, @Param("shopProductSkuKey") ShopProductSkuKey shopProductSkuKey);

    /**
     * 查询发货订单商品信息
     *
     * @param no     订单号
     * @param shopId 商品id
     * @return List<OrderDeliveryItemDTO>
     */
    List<OrderDeliveryItemDTO> queryDeliverGoodsInfo(@Param("no") String no, @Param("shopId") Long shopId);

    /**
     * 以代销商品+店铺为维度,统计时间范围内热销排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldDTO}
     */
    List<ConsignmentProductHotSoldDTO> countConsignmentProductHotSoldList(@Param("beginTime") LocalDateTime beginTime,
                                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 以店铺为维度,统计时间范围内代销商品的营业额排行榜(TOP5)
     *
     * @param beginTime 统计开始时间
     * @param endTime   统计结束时间
     * @return {@link ConsignmentProductHotSoldShopDTO}
     */
    List<ConsignmentProductHotSoldShopDTO> countConsignmentProductHotSoldShopList(@Param("beginTime") LocalDateTime beginTime,
                                                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 统计订单数量-和订单列表保持一致
     *
     * @return {@link Long}
     */
    Long countCompletedOrderNum();

}
