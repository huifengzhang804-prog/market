package com.medusa.gruul.order.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2023/3/3
 */
@Getter
@Setter
@ToString
public class OrderActivityDTO {

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 拼团团号 如果不为空 则加入指定的团购，为空则创建新团
     */
    private String teamNo;
}
