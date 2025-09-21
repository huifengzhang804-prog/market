package com.medusa.gruul.addon.supplier.model.dto;

import com.medusa.gruul.addon.supplier.model.SupplierConst;
import com.medusa.gruul.addon.supplier.model.enums.DateRangeType;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>供应商交易统计DTO</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class SupplierTradeStaticDTO {

    private final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss");
    private Long shopId;
    // 为了便于扩展,使用日期范围枚举
    private DateRangeType dateRangeType;
    @Pattern(regexp = SupplierConst.DATE_REGEX, message = "输入时间格式不正确")
    private String beginTime;
    @Pattern(regexp = SupplierConst.DATE_REGEX, message = "输入时间格式不正确")
    private String endTime;

    public SupplierTradeStaticDTO(String beginTime, String endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public SupplierTradeStaticDTO(Long shopId, DateRangeType dateRangeType) {
        this.shopId = shopId;
        switch (dateRangeType) {
            case TODAY -> {
                LocalDateTime now = LocalDateTime.now();
                this.beginTime = dfDate.format(getDayStart(now));
                this.endTime = dfDate.format(getDayEnd(now));
            }
            default -> {
                this.beginTime = null;
                this.endTime = null;
                throw new GlobalException("无效的DateRangeType");
            }
        }
    }

    /**
     * 获取当天的00:00:00
     *
     * @return
     */
    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.with(LocalTime.MIN);
    }

    /**
     * 获取当天的23:59:59
     *
     * @return
     */
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.with(LocalTime.MAX);
    }

    public Long getShopId() {
        return shopId;
    }

    public DateRangeType getDateRangeType() {
        return dateRangeType;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getBeginTimeMill() {
        return this.beginTime + " 00:00:00";
    }

    public String getEndTimeMill() {
        return this.endTime + " 23:59:59";
    }
}
