package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderReceiver;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.ProductDTO;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;
import com.medusa.gruul.order.service.model.dto.ShopPackageDTO;

import java.util.List;
import java.util.Map;

/**
 * 订单生成服务
 *
 * @author 张治保
 * date 2022/6/13
 */
public interface GenerateOrderService {

    /**
     * 生成订单
     *
     * @param loginUser  当前登录的用户资料
     * @param orderShops 订单参数详情
     * @return 订单信息
     */
    MemberOrder getOrder(SecureUser<?> loginUser, OrderShopsDTO orderShops);

    /**
     * 生成收货人地址信息
     *
     * @param orderNo  订单号
     * @param receiver 收货人信息
     * @return 渲染后的entity
     */
    OrderReceiver getOrderReceiver(String orderNo, ReceiverDTO receiver);

    /**
     * 生成店铺订单
     *
     * @param productMap       商品信息map
     * @param remark           店铺订单备注表单
     * @param userId           下单用户的id
     * @param index            序号 下标
     * @param order            主订单详情
     * @param shopPackage      店铺信息详情
     * @param distributionMode 配送方式
     * @return 店铺订单
     */
    ShopOrder getShopOrder(Map<Long, Product> productMap, ShopOrder.Remark remark, Long userId, int index, Order order, ShopPackageDTO shopPackage, DistributionMode distributionMode);

    /**
     * 生成店铺订单包裹商品
     *
     * @param productMap       商品信息map
     * @param userId           下单用户的id
     * @param shopOrder        店铺订单
     * @param products         店铺商品列表
     * @param activityType     活动类型
     * @param activityId       活动id
     * @param distributionMode 配送模式
     * @return 店铺订单商品
     */
    List<ShopOrderItem> getShopOrderItems(Map<Long, Product> productMap, Long userId, ShopOrder shopOrder, List<ProductDTO> products, OrderType activityType, Long activityId, DistributionMode distributionMode);


}
