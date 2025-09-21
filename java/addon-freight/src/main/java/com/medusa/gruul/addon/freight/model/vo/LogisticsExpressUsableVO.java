package com.medusa.gruul.addon.freight.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 可用的物流服务VO
 *
 * @author xiaoq
 * @Description 可用的物流服务VO
 * @date 2022-06-20 16:55
 */
@Data
public class LogisticsExpressUsableVO {

    private Long id;

    /**
     * 物流配置表id
     */
    @NotNull
    private Long freightId;


    /**
     * 物流公司名称
     */
    private String logisticsCompanyName;

    /**
     * 物流公司编码
     */
    private String logisticsCompanyCode;

    /**
     * 打印机名称
     */
    private String printName;

    /**
     * 打印机名称
     */
    private Long logisticsPrintId;


}
