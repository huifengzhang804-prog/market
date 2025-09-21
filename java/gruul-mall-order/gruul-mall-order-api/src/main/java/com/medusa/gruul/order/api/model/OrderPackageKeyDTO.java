package com.medusa.gruul.order.api.model;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.order.api.model.wechat.logistics.DeliveryMode;
import com.medusa.gruul.order.api.model.wechat.logistics.LogisticsType;
import com.medusa.gruul.order.api.model.wechat.logistics.OrderUploadType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/8/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPackageKeyDTO implements Serializable {

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;

    /**
     * 买家id
     */
    private Long buyerId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 店铺订单号
     */
    private String shopOrderNo;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 包裹id
     */
    private Long packageId;
    /**
     * 支付流水号
     */
    private Transaction transaction;

    /**
     * 已支付的运费
     */
    private Long paidFreight;

    /**
     * 已优惠的运费
     */
    private Long discountFreight;

    /**
     * 附加数据
     */
    private JSONObject extra;

    /**
     * 小程序订单发货上传接口类型
     */
    private OrderUploadType orderUploadType;

    /**
     * 是否已全部发货完毕
     */
    private Boolean isAllDelivered;

    /**
     * 微信小程序订单发货方式
     */
    private DeliveryMode deliveryMode;

    /**
     * 微信小程序订单 物流模式
     */
    private LogisticsType logisticsType;

    public Transaction transaction() {
        return transaction == null ? new Transaction() : transaction;
    }
}
