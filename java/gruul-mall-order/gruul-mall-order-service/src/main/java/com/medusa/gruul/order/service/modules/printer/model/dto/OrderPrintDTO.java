package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * 订单打印 mq消息体
 * 暂时只支持同城和门店配送小票
 *
 * @author 张治保
 * @since 2024/9/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPrintDTO implements Serializable {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 小票业务类型
     */
    @NotNull
    private PrintMode mode;

    /**
     * 打印场景
     */
    private PrintScene scene;

    /**
     * 打印联
     */
    private PrintLink link;

    /**
     * 指定打印的店铺id 集合 为空时查询该订单下的全部店铺订单
     */
    private Set<Long> shopIds;

}
