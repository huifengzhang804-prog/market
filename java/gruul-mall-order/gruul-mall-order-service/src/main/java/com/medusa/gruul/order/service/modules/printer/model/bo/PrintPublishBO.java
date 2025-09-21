package com.medusa.gruul.order.service.modules.printer.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/11/19
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintPublishBO implements Serializable {
    /**
     * 打印机sn码
     */
    private String sn;

    /**
     * 打印内容
     */
    private String content;

    /**
     * 打印次数
     */
    private Integer times;

}
