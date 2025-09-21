package com.medusa.gruul.order.service.modules.printer.model.template;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.FeieDirective;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/15
 */
@Getter
@RequiredArgsConstructor
public enum PrintFontStyle {
    /**
     * 居左
     */
    ALIGN_LEFT(null, 1),

    /**
     * 居中
     */
    ALIGN_CENTER(FeieDirective.C, 1),

    /**
     * 居右
     */
    ALIGN_RIGHT(FeieDirective.RIGHT, 1),

    /**
     * 正常高度
     */
    HEIGHT_1(null, 1),

    /**
     * 双倍高度
     */
    HEIGHT_2(FeieDirective.L, 1),

    /**
     * 正常宽度
     */
    WIDTH_1(null, 1),

    /**
     * 双倍宽度
     */
    WIDTH_2(FeieDirective.W, 2),

    /**
     * 正常字体大小
     */
    SIZE_1(null, 1),

    /**
     * 双倍字体大小
     */
    SIZE_2(FeieDirective.B, 2),

    /**
     * 正常字体粗细
     */
    WEIGHT_1(null, 1),

    /**
     * 字体加粗
     */
    WEIGHT_2(FeieDirective.BOLD, 1),
    ;

    /**
     * 飞鹅打印机指令
     */
    private final FeieDirective directive;

    /**
     * 字节膨胀比例
     */
    private final int bytesExpansion;


    /**
     * 获取字体放大倍数
     *
     * @return 字体放大倍数
     */
    public static int toExpansion(List<PrintFontStyle> styles) {
        if (CollUtil.isEmpty(styles)) {
            return 1;
        }
        int expansion = styles.stream()
                .map(PrintFontStyle::getBytesExpansion)
                .reduce(1, (a, b) -> a * b);
        expansion = Math.max(expansion, 1);
        return expansion;
    }

}
