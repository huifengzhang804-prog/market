package com.medusa.gruul.common.custom.aggregation.decoration.vo;

import com.medusa.gruul.common.custom.aggregation.decoration.enums.AggregationPlatform;
import lombok.*;

/**
 * 聚合装修vo
 *
 * @author xiaoq
 * @Description AggregationDecorationVO.java
 * @date 2022-11-03 11:09
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AggregationDecorationVO {

    /**
     * 聚合平台类型
     */
    private AggregationPlatform aggregationPlatform;


    /**
     * 首页名称
     */
    private String homePageName;
}
