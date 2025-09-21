package com.medusa.gruul.overview.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.api.enums.SortEnum;
import com.medusa.gruul.overview.api.enums.SourceEnum;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 对账单查询条件
 *
 * @author WuDi
 * date 2022/10/10
 */
@Getter
@Setter
@ToString
public class OverviewStatementQueryDTO implements Serializable {

    /**
     * 分页条件
     */

    Page<OverviewStatement> page;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 资金流向:0->收入;1->支出
     */
    private ChangeType changeType;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;


    /**
     * 对账单查询类型
     */
    private TransactionType transactionType;

//    /**
//     * 关键词
//     */
//    private String keyword;

    private SourceEnum sourceEnum;

    private List<Long> exportIds;

    private SortEnum sortEnum;
    /**
     * 关联订单号
     */
    private String orderNo;
    /**
     * 流水号
     */
    private String tradeNo;
    /**
     * 目标店铺id
     */
    private Long targetShopId;







    /**
     * startTime
     */
    public LocalDateTime getStartTime() {
        return Option.of(getStartDate())
                .map(LocalDate::atStartOfDay)
                .getOrNull();
    }

    /**
     * endTime
     */
    public LocalDateTime getEndTime() {
        return Option.of(getEndDate())
                .map(end -> end.atTime(LocalTime.MAX))
                .getOrNull();
    }
}
