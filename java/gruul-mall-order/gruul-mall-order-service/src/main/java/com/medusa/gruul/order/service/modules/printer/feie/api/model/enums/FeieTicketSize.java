package com.medusa.gruul.order.service.modules.printer.feie.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/15
 */
@Getter
@RequiredArgsConstructor
public enum FeieTicketSize {
    /**
     * 58小票机
     * 58mm的机器,一行打印16个汉字,32个字母;
     */
    V58(1, 32, "--------------------------------" + PrintFormat.BR, "------------商品信息------------" + PrintFormat.BR),

    /**
     * 80小票机
     * 80mm的机器,一行打印24个汉字,48个字母
     */
    V80(2, 48, "------------------------------------------------" + PrintFormat.BR, "--------------------商品信息--------------------" + PrintFormat.BR);

    @EnumValue
    private final int value;

    private final int byteSize;

    private final String divide;
    private final String productDivide;

    /**
     * 两种宽度的打印纸 32/48个字母 一个汉子占两个字母位
     * 商品名称       数量     价格
     * --------------------------
     * 9             3       4  = 16
     * 18            6       8  = 32
     * 27            9       12 = 48
     * <p>
     * 商品名称       数量     价格
     * ----------------------------
     * 收拾收拾sssssa x100    233.00
     * ----------------------------
     * 合计           x100   233.00
     */
    public int[] productFormatByteSizes(int column) {
        if (column < 1 || column > 3) {
            throw new IllegalArgumentException();
        }
        //只有1列完全占满
        if (column == 1) {
            return new int[]{byteSize};
        }
        //两列所占空间的比例
        if (column == 2) {
            // 32 | 48
            return FeieTicketSize.V58 == this ? new int[]{22, 10} : new int[]{30, 18};
        }
        //三列
        return FeieTicketSize.V58 == this ? new int[]{20, 5, 7} : new int[]{27, 9, 12};
    }
}
