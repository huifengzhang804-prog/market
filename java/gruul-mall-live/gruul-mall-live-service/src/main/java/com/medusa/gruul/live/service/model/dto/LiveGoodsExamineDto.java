package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author miskw
 * @date 2022/11/9
 * @Describe 审核商品查询条件
 */
@Data
public class LiveGoodsExamineDto extends Page<Object> {

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 价格左区间
     */
    private BigDecimal leftPrice;

    /**
     * 价格右区间
     */
    private BigDecimal rightPrice;



}
