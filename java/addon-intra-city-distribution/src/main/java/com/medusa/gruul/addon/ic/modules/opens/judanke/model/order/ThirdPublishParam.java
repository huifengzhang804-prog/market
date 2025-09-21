package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryBrand;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ThirdPublishParam {
    /**
     * order_id	int	是
     * 订单ID
     */
    private Long orderId;

    /**
     * relative_user_id	int	是
     * 聚单客商家ID
     */
    private Long relativeUserId;

    /**
     * pickup_time	string	否
     * 预定取件时间（booking=1有效）Y-m-d H:i:s
     */
    private LocalDateTime pickupTime;

    /**
     * booking	int	否
     * 是否为预定单 0:否,1:是
     */
    private Switch booking;

    /**
     * delivery_brands	array	是
     * 选取运力匹配
     */
    private Set<DeliveryBrand> deliveryBrands;


    /**
     * delivery_remark	string	否
     * 发单备注（传给运力公司，例如：美团 #4 期望 17:25 前送达）
     */
    private String deliveryRemark;

    /**
     * delivery_extend	object	否
     * 运力扩展参数（该参数未传递，默认使用聚合运力）
     */
    private Map<DeliveryBrand, DeliveryExtend> deliveryExtend;


}
