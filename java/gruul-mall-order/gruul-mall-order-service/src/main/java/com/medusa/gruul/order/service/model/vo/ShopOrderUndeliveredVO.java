package com.medusa.gruul.order.service.model.vo;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.order.api.entity.OrderReceiver;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 *
 *
 * @author 张治保
 * date 2022/8/8
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopOrderUndeliveredVO {

    /**
     * 收货人信息
     */
    private OrderReceiver orderReceiver;

    /**
     * 未发货商品列表
     */
    private List<ShopOrderItem> shopOrderItems;

    /**
     * 订单额外数据
     */
    private JSONObject extra;
}
