package com.medusa.gruul.addon.supplier.model.dto;

import com.medusa.gruul.addon.supplier.model.enums.DateRangeType;
import com.medusa.gruul.addon.supplier.model.enums.TradeStaticType;
import com.medusa.gruul.addon.supplier.utils.DateUtils;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>供应商商品交易统计</p>
 */
@Getter
@Setter
public class SupplierGoodsTradeStaticDTO {

    private Long supplierId;

    @NotNull
    private DateRangeType dateRangeType;

    private String beginTime;

    private String endTime;

    @NotNull
    private TradeStaticType tradeStaticType;

    public SupplierGoodsTradeStaticDTO(DateRangeType dateRangeType, TradeStaticType tradeStaticType) {

    }

    public void settingAndCheckAttributes() {
        LocalDateTime today = LocalDateTime.now();
        setEndTime(DateUtils.convertLocalDateTime2String(DateUtils.getDayEnd(today), DateUtils.DATE_TIME_FORMATTER));
        switch (this.getDateRangeType()) {
            case TODAY ->
                    setBeginTime(DateUtils.convertLocalDateTime2String(DateUtils.getDayStart(today), DateUtils.DATE_TIME_FORMATTER));
            case NEARLY_A_WEEK ->
                    setBeginTime(DateUtils.convertLocalDateTime2String(DateUtils.getDayStart(today.minusDays(CommonPool.NUMBER_SEVEN)), DateUtils.DATE_TIME_FORMATTER));
            case NEARLY_A_MONTH ->
                    setBeginTime(DateUtils.convertLocalDateTime2String(DateUtils.getDayStart(today.minusMonths(CommonPool.NUMBER_ONE)), DateUtils.DATE_TIME_FORMATTER));
            default -> throw new GlobalException("无效的DateType");
        }
        if (!TradeStaticType.TRADE_AMOUNT.equals(tradeStaticType) && !TradeStaticType.TRADE_NUMBER.equals(tradeStaticType)) {
            throw new GlobalException("无效的TradeStaticType");
        }
    }

}
