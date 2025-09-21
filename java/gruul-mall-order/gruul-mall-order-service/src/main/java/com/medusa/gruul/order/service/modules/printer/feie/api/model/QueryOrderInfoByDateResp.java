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
public class QueryOrderInfoByDateResp {

    /**
     * 已打印订单数
     */
    private Long print;

    /**
     * 等待打印数
     */
    private Long waiting;

}
