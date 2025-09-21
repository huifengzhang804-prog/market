package com.medusa.gruul.order.service.model.vo;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/3/20
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderRespVO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 额外数据
     */
    private JSONObject extra;
}
