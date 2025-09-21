package com.medusa.gruul.order.service.modules.printer.feie.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
public class BatchResp {

    /**
     * ok:成功的返回值
     */
    private List<String> ok;

    /**
     * no:失败的返回值
     */
    private List<String> no;
}
