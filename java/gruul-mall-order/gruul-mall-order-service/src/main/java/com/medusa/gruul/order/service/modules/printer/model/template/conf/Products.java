package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat.BR;
import static com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat.SPACE;

/**
 * 打印模板 商品信息
 *
 * @author 张治保
 * @since 2024/8/17
 */
@Getter
@Setter
@Accessors(chain = true)
public class Products implements Fxml {


    /**
     * 商品打印样式
     */
    private PrintProductType type;

    /**
     * skuId
     */
    private boolean skuId;

    /**
     * 商品名称
     */
    private boolean productName;

    /**
     * 规格
     */
    private boolean specs;

    @Override
    public String fxml(FeieTicketSize size) {
        String content = PrintProductType.SPACE_BETWEEN == type ?
                size.getProductDivide() :
                size.getDivide() +
                        String.join(
                                StrUtil.EMPTY,
                                PrintFormat.formatRows(
                                        size, List.of(List.of("商品名称", "数量", "单价"))
                                )
                        )
                        + BR
                        + size.getDivide();

        return content + PrintPlaceHolder.productListInf.placeholder()
                + BR
                + size.getDivide();
    }

    public String fxml(FeieTicketSize size, List<PrintProduct> products) {
        Function<PrintProduct, String> productNameRender = product -> {
            String productName = StrUtil.EMPTY;
            if (skuId) {
                productName += product.getSkuId();
            }
            if (this.productName) {
                productName += (productName.isEmpty() ? StrUtil.EMPTY : SPACE) + product.getName();
            }
            List<String> specs = product.getSpecs();
            if (this.specs && CollUtil.isNotEmpty(specs)) {
                productName += (productName.isEmpty() ? StrUtil.EMPTY : SPACE) + "(" + String.join(StrPool.COMMA, specs) + ")";
            }
            return productName;
        };
        String result;
        if (PrintProductType.SPACE_BETWEEN == type) {
            result = products.stream()
                    .map(product -> PrintFormat.spaceBetween(
                            1,
                            size,
                            productNameRender.apply(product),
                            (PrintFormat.num(product.getNum()) + SPACE + PrintFormat.price(false, product.getPrice()))
                    )).collect(Collectors.joining(BR));
        } else {
            result = String.join(BR, PrintFormat.formatRows(
                    size,
                    products.stream()
                            .map(
                                    product -> List.of(
                                            productNameRender.apply(product),
                                            PrintFormat.num(product.getNum()),
                                            PrintFormat.price(false, product.getPrice())
                                    )
                            )
                            .toList()
            ));
        }
        return result;
    }
}