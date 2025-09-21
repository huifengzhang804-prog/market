package com.medusa.gruul.addon.freight.model.param;

import com.medusa.gruul.addon.freight.model.enums.SequenceEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 物流节点信息param
 *
 * @author :  xiaoq
 * @Description : LogisticsNodeInfoParam.java
 * @date : 2022-05-06 13:53
 */
@Data
public class LogisticsNodeInfoParam {

    /**
     * 物流公司code
     */
    @NotNull
    private String companyCode;

    /**
     * 物流单号
     */
    @NotNull
    private String waybillNo;

    /**
     * 收件人手机号
     */
    private String recipientsPhone;

    /**
     * 物流节点查询排序
     */
    @NotNull
    private SequenceEnum order;


    /**
     * 出发地城市
     */
    private String from;

    /**
     * 目的地城市
     */
    private String to;
}
