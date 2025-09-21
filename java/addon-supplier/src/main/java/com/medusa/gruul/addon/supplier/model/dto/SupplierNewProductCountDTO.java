package com.medusa.gruul.addon.supplier.model.dto;

import com.medusa.gruul.addon.supplier.model.enums.DateRangeType;
import com.medusa.gruul.addon.supplier.utils.DateUtils;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>供应商新商品统计DTO</p>
 * @author An.Yan
 */
@Getter
@Setter
public class SupplierNewProductCountDTO {

    private Long shopId;

    // 为了便于扩展,使用日期范围枚举
    private DateRangeType dateRangeType;

    private final String beginTime;

    private final String endTime;

    /**
     * 初始化供应商新增商品统计DTO对象
     * @param shopId 店铺ID
     * @param dateRangeType 日期范围,{@link DateRangeType}
     */
    public SupplierNewProductCountDTO(Long shopId, DateRangeType dateRangeType) {
        this.shopId = shopId;
        switch (dateRangeType) {
            case TODAY -> {
                LocalDateTime now = LocalDateTime.now();
                this.beginTime = DateUtils.convertLocalDateTime2String(DateUtils.getDayStart(now), DateUtils.DATE_TIME_FORMATTER);
                this.endTime = DateUtils.convertLocalDateTime2String(DateUtils.getDayEnd(now), DateUtils.DATE_TIME_FORMATTER);
            }
            default -> {
                this.beginTime = null;
                this.endTime = null;
                throw new GlobalException("无效的DateRangeType");
            }
        }
    }
}
