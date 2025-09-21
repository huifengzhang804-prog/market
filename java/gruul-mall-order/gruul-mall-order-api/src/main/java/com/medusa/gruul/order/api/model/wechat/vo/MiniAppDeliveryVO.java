package com.medusa.gruul.order.api.model.wechat.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author wufuzhong
 * @Date 2024 2024/1/9 17:11
 * @Description 微信小程序 运力信息
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MiniAppDeliveryVO implements Serializable {

    private static final long serialVersionUID = 3890123664227638425L;

    @JSONField(name = "errcode")
    private Integer code;

    @JSONField(name = "delivery_list")
    private List<DeliveryVO> deliveryList;
}
