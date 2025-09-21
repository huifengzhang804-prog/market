package com.medusa.gruul.overview.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * <p>采购支出查询DTO</p>
 * @author An.Yan
 */
@Getter
@Setter
@ToString
public class PurchasePayoutQueryDTO extends Page<OverviewStatement> {

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 订单号
     */
    private String purchaseOrderNo;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    private Long shopId;

    /**
     * 交易流水号
     */
    private String tradeNo;

    public String getTradeNo() {
        return StrUtil.trimToNull(this.tradeNo);
    }
}
