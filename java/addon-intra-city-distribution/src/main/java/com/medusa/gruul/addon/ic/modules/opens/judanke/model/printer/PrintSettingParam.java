package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.PrinterFontSize;
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
public class PrintSettingParam {
    /**
     * deliver_time_size	string	是
     * 期望送达时间字体大小 s小号 m中号 l大号
     */
    private PrinterFontSize deliverTimeSize;

    /**
     * customer_addr_size	string	是
     * 收件地址信息字体大小 s小号 m中号 l大号
     */
    private PrinterFontSize customerAddrSize;

    /**
     * customer_name_size	string	是
     * 收件人姓名字体大小 s小号 m中号 l大号
     */
    private PrinterFontSize customerNameSize;

    /**
     * customer_phone_size	string	是
     * 收件人手机号字体大小 s小号 m中号 l大号
     */
    private PrinterFontSize customerPhoneSize;

    /**
     * goods_size	string	是
     * 商品信息字体大小 s小号 m中号 l大号
     */
    private PrinterFontSize goodsSize;

    /**
     * note_size	string	是
     * 备注字体大小 s小号 m中号 l大号
     */
    private PrinterFontSize noteSize;

}
