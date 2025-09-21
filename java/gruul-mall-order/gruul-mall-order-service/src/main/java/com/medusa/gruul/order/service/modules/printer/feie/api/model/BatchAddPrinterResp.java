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
@ToString(callSuper = true)
public class BatchAddPrinterResp extends BatchResp {

    /**
     * noGuide:添加失败的建议处理图片
     */
    private List<String> noGuide;
}
