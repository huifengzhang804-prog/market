package com.medusa.gruul.order.service.modules.printer.feie.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrinterEditParam {
    /**
     * sn(必填) 	必须	string	打印机编号
     */
    private String sn;

    /**
     * name(必填) 	必须	string	打印机备注名称
     */
    private String name;

    /**
     * phonenum 		string	打印机流量卡号码
     */
    private String phonenum;
}
