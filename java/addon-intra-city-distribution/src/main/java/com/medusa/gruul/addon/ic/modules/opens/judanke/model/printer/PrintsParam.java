package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.PrinterBrand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintsParam implements Serializable {
    /**
     * guid	string	是
     * 打印机列表中的guid
     */
    private String guid;

    /**
     * brand	string	是
     * 打印机品牌
     */
    private PrinterBrand brand;

    /**
     * relative_user_id	int	是
     * 关联商户id
     */
    private Long relativeUserId;

    /**
     * printer_datas	array	是
     * 打印数据
     */
    private List<PrinterData> printerDatas;
}
