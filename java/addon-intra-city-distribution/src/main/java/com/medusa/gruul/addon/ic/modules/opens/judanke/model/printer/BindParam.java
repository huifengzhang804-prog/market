package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.PrinterBrand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BindParam {

    /**
     * sn	string	是
     * 打印机SN码
     */
    private String sn;

    /**
     * key	string	否
     * 打印机密钥
     */
    private String key;

    /**
     * brand	string	是
     * 打印机品牌（大写）（FE =飞鹅， YLY= 易联云 ，ZW =中午云 ，XY = 芯烨 ，JB =佳博 ，SP =商鹏 ，YS= 优声云， MDLJ =美达逻捷 ，DQ= 大趋智能 ，ST= 水獭）
     */
    private PrinterBrand brand;

    /**
     * name	string	否
     * 打印机名称
     */
    private String name;

    /**
     * relative_user_id	int	是
     * 关联的商户id
     */
    private Long relativeUserId;
}
