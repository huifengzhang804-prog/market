package com.medusa.gruul.overview.service.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.overview.api.enums.QueryDateType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易排行查询param
 *
 * @author xiaoq
 * @Description DealRankingParam.java
 * @date 2022-10-20 18:52
 */
@Data
public class DealRankingParam {

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;


    /**
     * 查询概况日期type
     */
    private QueryDateType dateType;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * yyyy-MM
     * 当前年月
     */
    @Pattern(groups = DealRankingParam.MobileDealStatistics.class, regexp = "^(\\d{4})-(0?[1-9]|1[0-2])$", message = "输入时间格式不正确")
    @NotNull(groups = DealRankingParam.MobileDealStatistics.class)
    private String currentMonth;

    public LocalDateTime getStartDateTime() {
        return this.startDate.atTime(0, 0, 0);
    }

    public LocalDateTime getEndDateTime() {
        return this.endDate.atTime(23, 59, 59);
    }


    public interface MobileDealStatistics {

    }
}
