package com.medusa.gruul.addon.rebate.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class RebateDetailsQueryDTO extends Page<RebateDetails> {


    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 返利明细统计
     */
    private RebateDetailStatistic rebateDetailStatistic;


    @Getter
    @Setter
    public static class RebateDetailStatistic {

        /**
         * 收入
         */
        private Long income;

        /**
         * 支出
         */
        private Long expenditure;
    }
}
