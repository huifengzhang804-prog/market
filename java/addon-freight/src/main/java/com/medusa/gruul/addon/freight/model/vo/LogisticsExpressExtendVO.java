package com.medusa.gruul.addon.freight.model.vo;

import lombok.Data;

/**
 *
 * 物流服务扩展VO
 *
 * @author xiaoq
 * @Description LogisticsExpressExtendVO.java
 * @date 2022-08-13 09:37
 */
@Data
public class LogisticsExpressExtendVO {

    // --------打印机信息----------------
    /**
     * 打印机名称
     */
    private String printName;

    /**
     * 打印机机身号
     */
    private String deviceNo;

    //----------物流公司服务信息------------

    /**
     * 客户号
     */
    private String customerCode;


    /**
     * 客户密码
     */
    private String customerPassword;

    //------------ 物流配置信息 ----------------
    /**
     * 物流公司名称
     */
    private String logisticsCompanyName;

    /**
     * 物流公司编码
     */
    private String logisticsCompanyCode;

    /**
     * 打印模板编号
     */
    private String printTempNo;
}
