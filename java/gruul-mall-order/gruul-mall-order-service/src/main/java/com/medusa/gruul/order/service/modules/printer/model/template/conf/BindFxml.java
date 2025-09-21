package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFontStyle;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/20
 */
@ToString
@Accessors(chain = true)
@RequiredArgsConstructor
public abstract class BindFxml<T extends BindFxml<T>> implements Fxml, Serializable {

    /**
     * 结尾处是否需要换行
     */
    @JsonIgnore
    private final boolean br;

    /**
     * 是否选中
     */
    @Getter
    private boolean selected;

    /**
     * 样式
     */
    @Getter
    private List<PrintFontStyle> style;

    @SuppressWarnings("unchecked")
    public T setSelected(boolean selected) {
        this.selected = selected;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setStyle(List<PrintFontStyle> style) {
        this.style = style;
        return (T) this;
    }

    /**
     * 获取占位符
     *
     * @return 占位符
     */
    protected abstract PrintPlaceHolder placeholder();

    @Override
    public String fxml(FeieTicketSize size) {
        if (selected) {
            //样式包装
            return PrintFormat.wrapStyle(style, placeholder().placeholder())
                    //换行命令
                    + (br ? PrintFormat.BR : StrUtil.EMPTY);
        }
        return StrUtil.EMPTY;
    }
}
