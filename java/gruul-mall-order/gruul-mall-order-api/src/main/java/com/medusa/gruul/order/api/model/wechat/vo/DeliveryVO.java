package com.medusa.gruul.order.api.model.wechat.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wufuzhong
 * @Date 2024 2024/1/9 17:48
 * @Description
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliveryVO implements Serializable {


    private static final long serialVersionUID = -4210357819946304958L;

    /**
     * 快递公司ID
     */
    @JSONField(name = "delivery_id")
    private String deliveryId;

    /**
     * 快递公司名称
     */
    @JSONField(name = "delivery_name")
    private String deliveryName;
}
