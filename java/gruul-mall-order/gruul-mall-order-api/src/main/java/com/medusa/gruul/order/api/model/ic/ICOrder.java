package com.medusa.gruul.order.api.model.ic;

import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.entity.ShopOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 同城配送订单
 *
 * @author 张治保
 * @since 2024/8/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICOrder implements Serializable {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 取件码
     */
    private Long pickupCode;

    /**
     * 收货人信息
     */
    private UserAddressDTO receiver;

    /**
     * 商品信息
     */
    private List<ICProductSku> skus;

    /**
     * 用户备注
     */
    private ShopOrder.Remark remark;

    /**
     * 是否需要保温箱 默认 false
     */
    private Boolean warmBox = Boolean.FALSE;
}
