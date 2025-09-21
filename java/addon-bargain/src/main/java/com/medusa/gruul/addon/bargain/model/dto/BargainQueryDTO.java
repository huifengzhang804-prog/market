package com.medusa.gruul.addon.bargain.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 砍价活动列表查询参数
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
public class BargainQueryDTO extends Page<Bargain> {


    /**
     * 活动状态
     */
    private ActivityStatus status;


    /**
     * 关键字
     */
    private String keyword;

    /**
     * 活动开始日期
     */
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    private LocalDateTime endTime;

    /**
     * 店铺id
     */
    private Long shopId;
    
}
