package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PrintOrderStatistic {
    private Integer productNum;
    private Long totalPrice;
    private Long freight;
    private Long platformDiscount;
    private Long shopDiscount;
    private Long totalDiscount;
    private Long payPrice;
}