package com.medusa.gruul.overview.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.SortEnum;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import io.vavr.control.Option;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author 张治保
 * date 2022/11/29
 */
@Getter
@Setter
@ToString
public class PlatformWithdrawQueryDTO extends Page<OverviewWithdraw> {

    /**
     * 订单状态
     */
    @NotNull
    private WithdrawOrderStatus status;

    /**
     * 提现工单号
     */
    private String id;

//    /**
//     * 姓名
//     */
//    private String name;
    /**
     * 打款方式 true 线下打款 false 线上打款
     */
    private Boolean offline;

    /**
     * 提现用户的身份
     */
    private OwnerType type;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;
    /**
     * 申请导出的nos
     */
    private List<String> exportNos;
    /**
     * 排序字段
     */
    private SortEnum sortEnum;
    /**
     * 申请人手机号
     */
    private String applyUserPhone;
    /**
     * 审核人手机号 审核手机号通常是系统管理员 有必要这个么？
     */
    private String auditUserPhone;
    /**
     * 交易开始时间
     */
    private LocalDate tradeStartDate;
    /**
     * 交易结束时间
     */
    private LocalDate tradeEndDate;


    /**
     * startTime
     */
    public LocalDateTime getStartTime() {
        return Option.of(getStartDate()).map(LocalDate::atStartOfDay).getOrNull();
    }

    /**
     * endTime
     */
    public LocalDateTime getEndTime() {
        return Option.of(getEndDate()).map(date -> date.atTime(LocalTime.MAX)).getOrNull();
    }

    /**
     *
     * 交易的开始时间
     */
    public LocalDateTime getTradeStartTime() {
        return Option.of(getTradeStartDate()).map(LocalDate::atStartOfDay).getOrNull();
    }

    /**
     * 交易的结束时间
     * @return
     */
    public LocalDateTime getTradeEndTime() {
        return Option.of(getTradeEndDate()).map(date -> date.atTime(LocalTime.MAX)).getOrNull();
    }
}
