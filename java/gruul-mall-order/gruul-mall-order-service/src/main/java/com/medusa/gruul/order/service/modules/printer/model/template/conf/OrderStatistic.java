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

/**
 * 订单统计信息模板
 *
 * @author 张治保
 * @since 2024/8/15
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderStatistic implements Fxml {

    /**
     * 商品总数
     */
    private boolean productNum;

    /**
     * 商品总金额
     */
    private boolean totalPrice;

    /**
     * 运费
     */
    private boolean freight;

    /**
     * 平台折扣
     */
    private boolean platformDiscount;

    /**
     * 店铺折扣
     */
    private boolean shopDiscount;

    /**
     * 总折扣
     */
    private boolean totalDiscount;

    /**
     * 实付款
     */
    private PayPrice payPrice;

    @Override
    public String fxml(FeieTicketSize size) {
        if (productNum ||
                totalPrice ||
                freight ||
                platformDiscount ||
                shopDiscount ||
                totalDiscount ||
                (payPrice != null && payPrice.isSelected())) {
            return PrintPlaceHolder.orderStatistic.placeholder() + size.getDivide();
        }
        return StrUtil.EMPTY;
    }

    public String fxml(FeieTicketSize size, PrintOrderStatistic statistic) {
        String result = StrUtil.EMPTY;
        if (productNum) {
            result += PrintFormat.spaceBetween(
                    1,
                    size,
                    "商品数",
                    PrintFormat.num(statistic.getProductNum())
            ) + PrintFormat.BR;
        }
        if (totalPrice) {
            result += PrintFormat.spaceBetween(
                    1,
                    size,
                    "商品金额",
                    PrintFormat.price(false, statistic.getTotalPrice())
            ) + PrintFormat.BR;
        }

        if (freight) {
            result += PrintFormat.spaceBetween(
                    1,
                    size,
                    "运费",
                    PrintFormat.price(false, statistic.getFreight())
            ) + PrintFormat.BR;
        }

        if (platformDiscount) {
            result += PrintFormat.spaceBetween(
                    1,
                    size,
                    "平台优惠",
                    PrintFormat.price(false, statistic.getPlatformDiscount())
            ) + PrintFormat.BR;
        }

        if (shopDiscount) {
            result += PrintFormat.spaceBetween(
                    1,
                    size,
                    "店铺优惠",
                    PrintFormat.price(false, statistic.getShopDiscount())
            ) + PrintFormat.BR;
        }

        if (totalDiscount) {
            result += PrintFormat.spaceBetween(
                    1,
                    size,
                    "总优惠",
                    PrintFormat.price(false, statistic.getTotalDiscount())
            ) + PrintFormat.BR;
        }

        if (payPrice != null && payPrice.isSelected()) {
            List<PrintFontStyle> style = payPrice.getStyle();
            result += (result.isEmpty() ? StrUtil.EMPTY : size.getDivide()) +
                    PrintFormat.wrapStyle(
                            style,
                            PrintFormat.spaceBetween(
                                    PrintFontStyle.toExpansion(style),
                                    size,
                                    "实付",
                                    PrintFormat.price(true, statistic.getPayPrice())
                            )
                    );
        }
        if (result.endsWith(PrintFormat.BR)) {
            result = result.substring(0, result.length() - PrintFormat.BR.length());
        }
        return result;
    }
}