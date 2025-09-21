package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsPrint;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 物流打印机DTO
 *
 * @author xiaoq
 * @Description LogisticsPrintDTO.java
 * @date 2022-08-11 16:12
 */
@Data
public class LogisticsPrintDTO {
    private Long id;

    /**
     * 打印机名称
     */
    @NotNull
    @Size(max = 32)
    private String printName;

    /**
     * 打印机身号
     */
    @NotNull
    @Size(max = 32)
    private String deviceNo;

    /**
     * 是否 自营商家
     */
    private SelfShopEnum defSelfShop;

    /**
     * 是否 自营供应商
     */
    private SelfShopEnum defSelfSupplier;

    public LogisticsPrint coverLogisticsPrint() {
        LogisticsPrint logisticsPrint = new LogisticsPrint();
        BeanUtil.copyProperties(this, logisticsPrint);
        return logisticsPrint;
    }
}
