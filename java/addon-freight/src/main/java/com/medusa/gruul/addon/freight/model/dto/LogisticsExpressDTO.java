package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsExpress;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 物流服务公司DTO
 *
 * @author xiaoq
 * @Description 物流服务公司DTO
 * @date 2022-06-20 09:21
 */
@Data
public class LogisticsExpressDTO {

    private Long id;

    @NotNull
    private Long logisticsPrintId;

    /**
     * 物流配置表id
     */
    @NotNull
    private Long freightId;


    /**
     * 客户号
     */
    @NotNull
    private String customerCode;


    /**
     * 客户密码
     */
    @NotNull
    private String customerPassword;

    /**
     * 网点名称
     */
    @NotNull
    private String networkName;

    /**
     * 网点编号
     */
    private String networkCode;

    /**
     * 是否 自营商家
     */
    private SelfShopEnum defSelfShop;

    /**
     * 是否 自营供应商
     */
    private SelfShopEnum defSelfSupplier;

    public LogisticsExpress coverLogisticsExpress() {
        LogisticsExpress logisticsExpress = new LogisticsExpress();
        BeanUtil.copyProperties(this, logisticsExpress);
        return logisticsExpress;
    }
}
