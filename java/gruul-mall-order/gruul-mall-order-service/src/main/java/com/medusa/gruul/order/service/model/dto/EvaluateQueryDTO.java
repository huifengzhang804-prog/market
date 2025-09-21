package com.medusa.gruul.order.service.model.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.enums.EvaluateQueryType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户订单评价查询条件
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class EvaluateQueryDTO extends Page<OrderEvaluate> {

    /**
     * 根据订单号 || 商品名称查询
     */
    private String keywords;
    /**
     * 评价查询类型
     */
    private EvaluateQueryType type;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 成交时间 开始时间
     */
    private LocalDate startTime;

    /**
     * 成交时间 结束时间
     */
    private LocalDate endTime;

    /**
     * 评价星级
     */
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rate;


}
