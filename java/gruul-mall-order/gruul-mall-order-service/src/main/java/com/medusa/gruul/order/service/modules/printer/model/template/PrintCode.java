package com.medusa.gruul.order.service.modules.printer.model.template;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.FeieDirective;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

/**
 * 条码信息 二维码｜条形码
 *
 * @author 张治保
 * @since 2024/8/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintCode implements Fxml {

    private static final Pattern NUMBER_OR_ALPHABET = Pattern.compile("[a-zA-Z0-9]+");
    /**
     * 二维码、条形码
     */
    @NotNull
    private PrintCodeType type;

    /**
     * 条码内容仅支持数字和大小写字母
     */
    @NotBlank
    @Size(max = 200)
    private String content;

    /**
     * 说明
     */
    @Size(max = 100)
    private String desc;


    @Override
    public String fxml(FeieTicketSize size) {
        if (type == null) {
            return StrUtil.EMPTY;
        }
        String curContent = content.trim();
        //二维码指令
        if (PrintCodeType.QR_CODE == type) {
            return FeieDirective.QR.render(curContent) +
                    (StrUtil.isBlank(desc) ? StrUtil.EMPTY : FeieDirective.C.render(desc.trim()));
        }
        //内容只能是数字和大小写字母
        if (!NUMBER_OR_ALPHABET.matcher(curContent).matches()) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        //<BC128_A>123ABCDEF</BC128_A> ：数字和大写字母混合的条形码，最多支持14位的数字、大写字母混合条形码
        //<BC128_B>123ABCDef</BC128_B> ：数字和大小写字母混合的条形码，最多支持14位的数字、大写字母、小写字母混合条形码
        //<BC128_C>0123456789</BC128_C> ：最多支持22位纯数字
        //如果是纯数字最大22位
        //否则最大14
        boolean isNumbers = PatternPool.NUMBERS.matcher(curContent).matches();
        if (curContent.length() > (isNumbers ? 22 : 14)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        //条形码指令
        String codeDirective = FeieDirective.C.render((isNumbers ? FeieDirective.BC128_C : FeieDirective.BC128_B).render(curContent));
        if (StrUtil.isNotBlank(desc)) {
            codeDirective += FeieDirective.C.render(desc.trim());
        }
        return codeDirective;
    }
}
