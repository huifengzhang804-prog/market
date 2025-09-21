package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PrintProduct {
    private Long skuId;
    private String name;
    private List<String> specs;
    private Integer num;
    private Long price;
}