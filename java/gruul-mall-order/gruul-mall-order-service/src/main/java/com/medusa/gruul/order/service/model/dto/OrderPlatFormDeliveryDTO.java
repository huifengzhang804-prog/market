package com.medusa.gruul.order.service.model.dto;

import com.medusa.gruul.common.model.enums.DistributionMode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author: wufuzhong
 * @Date: 2023/11/10 11:36:36
 * @Description: 平台发货订单对象 DTO
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPlatFormDeliveryDTO {

    /**
     * 配送类型
     */
    private DistributionMode distributionMode;

    /**
     * 订单编号数组
     */
    private Set<String> orderNos;

}
