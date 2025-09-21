package com.medusa.gruul.addon.freight.model.vo;

import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 物流公司服务VO
 *
 * @author xiaoq
 * @Description
 * @date 2022-06-14 16:09
 */
@Data
public class LogisticsExpressVO {

    private Long id;

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
    private String networkName;

    /**
     * 网点编号
     */
    private String networkCode;


    /**
     * 物流公司名称
     */
    private String logisticsCompanyName;

    /**
     * 物流公司编码
     */
    private String logisticsCompanyCode;

    /**
     * 物流公司状态
     */
    private LogisticsCompanyStatus logisticsCompanyStatus;

    /**
     * 打印机名称
     */
    private String printName;

    /**
     * 打印机名称
     */
    private Long logisticsPrintId;

    /**
     * 是否 自营商家
     */
    private SelfShopEnum defSelfShop;

    /**
     * 是否 自营供应商
     */
    private SelfShopEnum defSelfSupplier;

}
