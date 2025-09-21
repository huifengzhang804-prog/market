package com.medusa.gruul.addon.ic.addon;


import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import com.medusa.gruul.order.api.model.ic.ICStatus;
import com.medusa.gruul.shop.api.model.dto.ShopIcDistributeInfoDTO;
import com.vividsolutions.jts.geom.Point;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * @author miskw
 * @since 2023/3/6
 */
public interface IcAddonProvider {

    String FILTER = "INTRA_CITY_DISTRIBUTION";

    /**
     * 计算配送费用
     *
     * @param platformFreight 运费计算信息
     * @return 计算结果
     */
    Map<String, BigDecimal> getCalculateCost(PlatformFreightParam platformFreight);

    /**
     * 获取店铺的起送金额
     *
     * @param shopIds 店铺ids
     * @return 获取店铺的起送金额
     */
    Map<Long, BigDecimal> getShopInitialDeliveryCharge(Set<Long> shopIds);

    /**
     * 同城发货订单上报
     *
     * @param isSelf 是否是自配送 false为第三方配送
     * @param order  订单信息
     */
    void icOrderReport(Boolean isSelf, ICOrder order);

    /**
     * 确定所有的店铺是否都支持同城配送
     *
     * @param shopIds
     * @return
     */
    boolean checkAllSupportIc(Set<Long> shopIds);

    /**
     * 批量查询同城订单状态
     *
     * @param orderNos              订单号
     * @param handledErrorToPending 已处理的错误标记为正常
     * @return 订单状态 map key 订单号 value 订单状态
     */
    Map<String, ICStatus> icOrderStatus(Set<String> orderNos, Boolean handledErrorToPending);

    /**
     * 查询店铺是否开启同城配送
     *
     * @param shopId
     * @return
     */
    Boolean queryShopIcStatus(Long shopId);

    Boolean queryShopIcStatusForCart(Long shopId);

    /**
     * 获取店铺同城配送的运费信息
     *
     * @param shopIds
     * @return
     */
    Map<Long, ShopIcDistributeInfoDTO> getShopIcDistributeInfo(Set<Long> shopIds, Point point);
}
