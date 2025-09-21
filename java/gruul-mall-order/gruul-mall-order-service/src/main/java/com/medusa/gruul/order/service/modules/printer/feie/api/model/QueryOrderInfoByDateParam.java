package com.medusa.gruul.order.service.modules.printer.feie.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class QueryOrderInfoByDateParam {

    /**
     * sn 	必须	string	打印机编号
     */
    private String sn;

    /**
     * date 	必须	string	查询日期，格式YY-MM-DD，如：2016-09-20
     */
    private LocalDate date;

}
