package com.medusa.gruul.overview.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.SortEnum;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * @author 张治保
 * date 2022/11/29
 */
@Getter
@Setter
@ToString
public class ShopWithdrawQueryDTO extends Page<OverviewWithdraw> {

    /**
     * 订单状态
     */

    private WithdrawOrderStatus status;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 提现者类型
     */
    private OwnerType type;

    /**
     * 提现工单号
     */
    private String no;
    /**
     * 导出的ids
     */
    private List<Long> exportIds;
    /**
     * 排序字段
     */
    private SortEnum sortEnum;
    /**
     * 申请人手机号
     */
    private String applyUserPhone;

}
