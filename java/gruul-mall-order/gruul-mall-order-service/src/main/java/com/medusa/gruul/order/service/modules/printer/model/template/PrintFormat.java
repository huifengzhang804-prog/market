package com.medusa.gruul.order.service.modules.printer.model.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.FeieDirective;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/17
 */
public interface PrintFormat {


    /**
     * 换行明明
     */
    String BR = FeieDirective.BR.render();

    /**
     * 字符串空格
     */
    String SPACE = " ";
    /**
     * gpk charset
     */
    Charset GBK_CHARSET = Charset.forName("GBK");

    /**
     * 一个打印订单最大的gbk字节长度
     */
    int MAX_PRINT_ORDER_GBK_SIZE = 5000;

    /**
     * 打印订单内容切割 一个订单能打印的内容最多5000字节
     *
     * @param content 打印内容
     * @return 切割后的打印内容
     */
    static List<String> printContentSplit(String content) {
        byte[] gbkBytes = content.getBytes(GBK_CHARSET);
        int length = gbkBytes.length;
        if (length <= MAX_PRINT_ORDER_GBK_SIZE) {
            return List.of(content);
        }
        List<String> result = new ArrayList<>();
        int startIndex = 0;
        // 按5000字节切割
        while (startIndex < length) {
            int endIndex = Math.min(startIndex + MAX_PRINT_ORDER_GBK_SIZE, length);
            if (endIndex == length) {
                result.add(new String(gbkBytes, startIndex, endIndex - startIndex, GBK_CHARSET));
                break;
            }
            // 处理边界情况：确保不会切割字符
            while (endIndex < length && (gbkBytes[endIndex] < 0 && gbkBytes[endIndex - 1] < 0)) {
                endIndex--;
            }
            // 将切割的字节数组转换回字符串
            String part = new String(gbkBytes, startIndex, endIndex - startIndex, GBK_CHARSET);
            //找到最后一个BR 换行符 截取字符串
            //part = 111111111111<BR>aaaaa
            int index;
            if (!part.endsWith(BR) && (index = part.lastIndexOf(BR)) != -1) {
                int brEndIndex = index + BR.length();
                //br后的数据放在下一个打印订单
                //即：aaaaa放在下一个打印订单
                //endIndex = endIndex - "aaaaa".getBytes(GBK_CHARSET).length
                endIndex -= part.substring(brEndIndex).getBytes(GBK_CHARSET).length;
                //part = 111111111111<BR>
                part = part.substring(0, brEndIndex);
            }
            result.add(part);
            startIndex = endIndex;
        }
        return result;

    }

    /**
     * 获取字符串在 gbk 编码下占用的字节数
     *
     * @param string 字符串
     * @return gbk 编码下占用的字节数
     */
    static int gbkBytesSize(String string) {
        return string.getBytes(GBK_CHARSET).length;
    }

    /**
     * 两种宽度的打印纸 32/48个字母 一个汉子占两个字母位
     * 商品名称       数量     价格
     * -----------------------------
     * 9             3       4  = 16
     * 18            6       8  = 32
     * 27            9       12 = 48
     * <p>
     * 商品名称       数量     价格
     * -----------------------------
     * 收拾收拾sssssa x100    233.00
     * -----------------------------
     * 合计           x100   233.00
     * </p>
     */
    static List<String> format(int byteSize, String string, boolean hasNext) {
        //转成字符数组
        char[] chars = string.trim().toCharArray();
        //如果有下一列 则当前列的每行都预留一个空格
        int spaceNext = hasNext ? 1 : 0;
        //当前行所能占用的最大字节数
        int maxRowBytesSize = byteSize - spaceNext;
        int curRowSize = byteSize - spaceNext;
        StringBuilder sb = new StringBuilder();
        List<String> rows = new ArrayList<>();
        for (char ch : chars) {
            int curCharSize = gbkBytesSize(String.valueOf(ch));
            //当前行剩余的字符位
            int remainSize = curRowSize - curCharSize;
            //如果当前行还有足够的字符位
            if (remainSize > 0) {
                //如果当前行的第一个字符是空格 则忽略当前字符
                if (curRowSize == maxRowBytesSize && ch == StrPool.C_SPACE) {
                    continue;
                }
                sb.append(ch);
                curRowSize = remainSize;
                continue;
            }
            //如果字符位正好狗当前字符
            if (remainSize == 0) {
                //渲染当前行数据
                sb.append(ch);
                if (hasNext) {
                    sb.append(StrPool.C_SPACE);
                }
                rows.add(sb.toString());
                //重置下一行
                curRowSize = maxRowBytesSize;
                sb = new StringBuilder();
                continue;
            }
            //当前字符超出剩余的字符位
            //剩余位使用空格填充 当前char移到下一行
            sb.append(SPACE.repeat(Math.max(spaceNext, curRowSize + spaceNext)));
            rows.add(sb.toString());
            //重置下一行
            curRowSize = maxRowBytesSize - curCharSize;
            sb = new StringBuilder().append(ch);
        }
        if (!sb.isEmpty()) {
            rows.add(
                    sb.append(SPACE.repeat(Math.max(spaceNext, curRowSize + spaceNext)))
                            .toString()
            );
        }
        return rows;
    }

    /**
     * 格式化每一行数据  跟列名对齐
     *
     * @param size 打印机纸张尺寸
     * @param rows 每一行数据
     * @return 格式化后的每一行数据
     */
    static List<String> formatRows(FeieTicketSize size, List<List<String>> rows) {
        int columnSize = rows.get(0).size();
        int[] bytesSizes = size.productFormatByteSizes(columnSize);
        List<String> result = new ArrayList<>();
        for (List<String> row : rows) {
            List<List<String>> rowCells = new ArrayList<>();
            for (int column = 0; column < columnSize; column++) {
                String string = row.get(column);
                rowCells.add(format(bytesSizes[column], string, column < columnSize - 1));
            }
            int rowNumber = 0;
            int maxIndex = rowCells.stream().map(List::size).max(Integer::compareTo).orElse(0);
            while (rowNumber < maxIndex) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < columnSize; i++) {
                    List<String> strings = rowCells.get(i);
                    if (rowNumber > strings.size() - 1) {
                        sb.append(SPACE.repeat(bytesSizes[i]));
                        continue;
                    }
                    sb.append(strings.get(rowNumber));
                }
                result.add(sb.toString());
                rowNumber++;
            }
        }
        return result;
    }

    /**
     * 两端对齐 如果 label + value 超长 则value 换行 居右展示
     * 两端对齐
     * ---------eg1----------
     * 实付             ￥2.00
     * ---------eg2----------
     * 商品数量           x322
     *
     * @param expansion 字节放大倍数
     * @param size      打印机小票纸张尺寸
     * @param label     值描述
     * @param value     值
     * @return 两端对齐后的字符串
     */
    static String spaceBetween(int expansion, FeieTicketSize size, String label, Object value) {
        if (value instanceof BigDecimal decimal) {
            value = decimal.toPlainString();
        }
        expansion = Math.max(expansion, 1);
        int totalByteSize = size.getByteSize();
        int labelByteSize = gbkBytesSize(label);
        int valueByteSize = gbkBytesSize(value.toString());

        //内容总长度
        int contentTotalSize = (labelByteSize + valueByteSize) * expansion;
        //如果总长度大于内容长度
        if (totalByteSize > contentTotalSize) {
            return label + SPACE.repeat(Math.max(0, (totalByteSize - contentTotalSize) / expansion)) + value;
        }
        return label + BR +
                SPACE.repeat(totalByteSize - valueByteSize) + value;

    }


    static String wrapStyle(List<PrintFontStyle> styles, String wrapContent) {
        if (CollUtil.isEmpty(styles)) {
            return wrapContent;
        }
        String wrappedContent = wrapContent;
        for (int cur = styles.size() - 1; cur >= 0; cur--) {
            FeieDirective directive = styles.get(cur).getDirective();
            if (directive != null) {
                wrappedContent = directive.isSingle() ?
                        (directive.render() + wrappedContent) :
                        directive.render(wrappedContent);
            }
        }
        return wrappedContent;
    }

    static String num(int num) {
        return "x" + num;
    }

    static String price(boolean symbol, Long price) {
        return (symbol ? "￥" : StrUtil.EMPTY) + AmountCalculateHelper.toYuan(price).stripTrailingZeros().toPlainString();
    }


    static String fxml(FeieTicketSize size, Fxml... fxml) {
        StringBuilder result = new StringBuilder();
        for (Fxml item : fxml) {
            if (item == null) {
                continue;
            }
            result.append(item.fxml(size));
        }
        return result.toString();
    }
}
