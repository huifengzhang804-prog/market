package com.medusa.gruul.order.api.model;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.entity.OrderPayment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2022/10/19
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPaidBroadcastDTO implements Serializable {

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;

    /**
     * 订单活动类型
     */
    private OrderType activityType;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家用户id
     */
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 买家头像
     */
    private String buyerAvatar;

    /**
     * 支付用户id
     */
    private Long payerId;

    /**
     * 支付回调时间
     */
    private LocalDateTime notifyTime;

    /**
     * 总金额
     */
    private Long payAmount;
    /**
     * 支付信息
     */
    private OrderPayment orderPayment;

    /**
     * 店铺支付记录
     */
    private List<ShopPayAmountDTO> shopPayAmounts;

    /**
     * 附加数据
     */
    private JSONObject extra;


    @Getter
    @Setter
    @Accessors(chain = true)
    @ToString
    public static class ShopPayAmountDTO {

        /**
         * 店铺id
         */
        private Long shopId;

        /**
         * 金额
         */
        private Long amount;

        /**
         * 运费
         */
        private Long freightAmount;

        /**
         * 优惠金额
         */
        private Long discountAmount;
    }


}
