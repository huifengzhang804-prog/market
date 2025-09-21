package com.medusa.gruul.addon.integral.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author miskw
 * @date 2023/7/22
 * @describe 订单自动完成
 */
@Data
@Accessors(chain = true)
public class IntegralCompletionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String integralOrderNo;
}
