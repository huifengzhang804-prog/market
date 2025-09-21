package com.medusa.gruul.order.service.model.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.enums.EvaluateQueryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户订单评价查询条件
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductEvaluateQueryDTO extends Page<OrderEvaluate> {


    /**
     * 评价查询类型
     */
    private EvaluateQueryType type;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 用户id
     */
    private Long userId;


}
