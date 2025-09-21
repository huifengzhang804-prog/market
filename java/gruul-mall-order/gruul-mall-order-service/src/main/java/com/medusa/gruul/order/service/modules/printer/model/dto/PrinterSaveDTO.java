package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrinterSaveDTO {

    /**
     * 关联的业务模式
     */
    @NotNull
    private PrintMode mode;

    /**
     * 打印机品牌
     */
    @NotNull
    private PrinterBrand brand;

    /**
     * 打印机名称
     */
    @NotBlank
    @Size(max = 20)
    private String name;

    /**
     * 打印机使用位置
     */
    private String place;

    /**
     * 打印机 sn 码
     */
    @NotBlank
    private String sn;

    /**
     * 打印机密钥 打印机有则必填
     */
    private String key;

    /**
     * 流量卡号码  流量卡版本的打印机必填
     */
    private String flowCard;


}
