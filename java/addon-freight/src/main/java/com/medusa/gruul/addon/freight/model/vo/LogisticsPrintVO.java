package com.medusa.gruul.addon.freight.model.vo;

import lombok.Data;

/**
 * 物流打印VO
 *
 * @author xiaoq
 * @Description LogisticsPrintVO.java
 * @date 2022-08-11 16:13
 */
@Data
public class LogisticsPrintVO {
    private Long id;

    /**
     * 打印机名称
     */
    private String printName;

    /**
     * 打印机身号
     */
    private String deviceNo;
}
