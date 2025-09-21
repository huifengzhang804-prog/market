package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFontStyle;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

import static com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat.BR;
import static com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat.SPACE;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@Setter
@Accessors(chain = true)
public class Title implements Fxml {

    private static final String PICKUP_CODE_PLACEHOLDER = "#" + PrintPlaceHolder.pickupCode.placeholder();
    /**
     * 平台名称
     */
    private final boolean platformName = true;
    /**
     * 取餐号
     */
    private boolean pickupCode;
    /**
     * 样式
     */
    private List<PrintFontStyle> style;

    @Override
    public String fxml(FeieTicketSize size) {
        String platformNamePlaceholder = PrintPlaceHolder.platformName.placeholder();
        if (!pickupCode && !platformName) {
            return StrUtil.EMPTY;
        }
        return PrintFormat.wrapStyle(
                style,
                pickupCode && platformName ? PICKUP_CODE_PLACEHOLDER + SPACE + platformNamePlaceholder
                        : pickupCode ? PICKUP_CODE_PLACEHOLDER : platformNamePlaceholder
        ) + BR;
    }
}
