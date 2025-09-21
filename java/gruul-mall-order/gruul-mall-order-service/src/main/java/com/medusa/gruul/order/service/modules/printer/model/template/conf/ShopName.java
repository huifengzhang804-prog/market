package com.medusa.gruul.order.service.modules.printer.model.template.conf;


import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFontStyle;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

/**
 * @author 张治保
 * @since 2024/8/21
 */
public class ShopName extends BindFxml<ShopName> {

    public ShopName() {
        super(false);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.shopName;
    }

    @Override
    public String fxml(FeieTicketSize size) {
        if (!isSelected()) {
            return StrUtil.EMPTY;
        }
        return super.fxml(size) + (getStyle().contains(PrintFontStyle.ALIGN_CENTER) ? StrUtil.EMPTY : PrintFormat.BR);
    }


}