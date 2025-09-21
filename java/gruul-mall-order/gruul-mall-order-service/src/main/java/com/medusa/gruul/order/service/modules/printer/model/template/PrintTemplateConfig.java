package com.medusa.gruul.order.service.modules.printer.model.template;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.FeieDirective;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/15
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class PrintTemplateConfig implements Fxml {

    /**
     * 头部内容配置
     */
    @NotNull
    private Title title;

    /**
     * 店铺名称配置
     */
    private ShopName shopName;

    /**
     * 订单备注配置
     */
    private OrderRemark orderRemark;

    /**
     * 商品信息配置
     */
    private Products products;

    /**
     * 结算信息配置
     */
    private OrderStatistic orderStatistic;

    /**
     * 订单信息配置
     */
    private OrderInfo orderInfo;

    /**
     * 收货人或门店信息配置
     * 同城配送时为收货人信息 自提时为门店
     */
    private TargetInfo targetInfo;

    /**
     * 条码信息配置
     */
    private PrintCode code;

    /**
     * 底部信息
     */
    @Size(max = 100)
    private String desc;


    @Override
    public String fxml(FeieTicketSize size) {
        String fxml = PrintFormat.fxml(
                size,
                title,
                shopName,
                orderRemark,
                products,
                orderStatistic,
                orderInfo,
                targetInfo,
                code
        );
        if (StrUtil.isNotBlank(desc)) {
            fxml += FeieDirective.C.render(desc.trim());
        }
        // 移除除最后一个分割符
        String divide = size.getDivide();
        if (fxml.endsWith(divide)) {
            fxml = fxml.substring(0, fxml.length() - divide.length());
        }
        return fxml +
                //切纸
                FeieDirective.CUT.render();
    }
}
