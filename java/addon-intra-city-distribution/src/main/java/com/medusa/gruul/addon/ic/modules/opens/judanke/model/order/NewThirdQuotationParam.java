package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryBrand;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import com.medusa.gruul.common.fastjson2.FastJson2;
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
public class NewThirdQuotationParam {

    /**
     * order_id	int	是
     * 订单号
     */
    private Long orderId;

    /**
     * relative_user_id	int	是
     * 订单号
     */
    private Long relativeUserId;

    /**
     * pickup_time	string	否
     * 预定取件时间（booking=1有效）
     */
    @JSONField(format = FastJson2.DATETIME_PATTEN)
    private LocalDateTime pickupTime;

    /**
     * booking	int	否
     * 是否为预定单 0:否,1:是
     */
    private Switch booking;

    /**
     * delivery_brands	array	否
     * 选取运力匹配（不传或者为空表示获取全部）
     */
    private Set<DeliveryBrand> deliveryBrands;

    /**
     * delivery_remark	string	否
     * 发单备注（传给运力公司）
     */
    private String deliveryRemark;


    /**
     * delivery_extend 参数为 {"ddks":{"delivery_type":1},"ss":{"delivery_type":0}}
     * key 运力品牌
     * value: 运力类型
     */
    private Map<DeliveryBrand, DeliveryExtend> deliveryExtend;


}
