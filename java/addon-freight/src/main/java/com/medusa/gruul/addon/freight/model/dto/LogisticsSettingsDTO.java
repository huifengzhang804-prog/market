package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 物流设置DTO
 *
 * @author xiaoq
 * @Description LogisticsSettingsDTO.java
 * @date 2022-08-12 10:31
 */
@Data
public class LogisticsSettingsDTO {

    private Long id;

    /**
     * 快递100授权key
     */
    @NotNull
    private String key;

    /**
     * 快递100客户号
     */
    @NotNull
    private String customer;

    /**
     * 授权secret用于电子面单打印
     */
    private String secret;

    public LogisticsSettings coverLogisticsSettings() {
        LogisticsSettings logisticsSettings = new LogisticsSettings();
        BeanUtil.copyProperties(this, logisticsSettings);
        return logisticsSettings;
    }
}
