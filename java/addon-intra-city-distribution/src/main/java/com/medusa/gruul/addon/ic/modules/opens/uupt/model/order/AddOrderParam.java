package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.OrderSource;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PayType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.SpecialType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PushType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AddOrderParam {

    /**
     * 是 金额令牌，计算订单价格接口返回的price_token
     */
    private String priceToken;

    /**
     * String 否 发货人名称
     */
    private String sender;

    /**
     * String 否 发货人电话（如果为空则是用户注册的手机号）
     */
    private String senderPhone;

    /**
     * String 否 收件人名称
     */
    private String receiver;

    /**
     * String 是 收件人电话 手机号码； 虚拟号码格式（手机号_分机号码）例如：13700000000_1111
     */
    private String receiverPhone;

    /**
     * 否 收件人真实脱敏电话(外卖订单虚拟号时，投递餐柜使用)格式：（137****0000或手机尾号0000）
     */
    private String receiverRealPhone;

    /**
     * String 否 备注
     */
    private String note;

    /**
     * String 否 订单提交成功后及状态变化的回调地址
     */
    private String callbackUrl;

    /**
     * String 是 推送方式，默认开放订单，参考推送方式枚举
     */
    private PushType pushType;

    /**
     * specialType
     * String
     * 是
     * 是否需要保温箱，参考是否需要保温箱枚举
     */
    private SpecialType specialType;

    /**
     * payType
     * String
     * 是
     * 支付方式。参考支付方式枚举（企业余额不足自动转账户余额支付）
     */
    private PayType payType;

    /**
     * orderSource
     * String
     * 否
     * 订单来源，默认其他，参考订单来源枚举
     */
    private OrderSource orderSource;

    /**
     * shortOrderNum
     * string
     * 否
     * 订单平台短单号（0-10000），该单号会展示给骑手突出展示：“美团 #1”
     */
    private String shortOrderNum;

    /**
     * 否 商品详细信息列表
     * [{"goodsName":"辣条","goodsNum":1}]
     */
    private List<ProductDetail> productDetailList;

    /**
     * 否
     * 第三方门店ID
     */
    private String thirdShopId;

}
