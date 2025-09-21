package com.medusa.gruul.overview.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import com.medusa.gruul.overview.service.model.vo.WithdrawDistributorStatistic;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/11/22
 */
@Getter
@Setter
@ToString(callSuper = true)
public class WithdrawQuery extends Page<OverviewWithdraw> {

    /**
     * 订单状态
     */
    @NotNull
    private WithdrawOrderStatus status;

    /**
     * 提现者身份
     */
    private OwnerType ownerType;

    /**
     * 提现已到账金额
     */
    private WithdrawDistributorStatistic statistic;

    /**
     * 提现工单 来源类型
     */
    private WithdrawSourceType withdrawSourceType;

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

}
