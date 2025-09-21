package com.medusa.gruul.addon.supplier.model.bo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderTimeout implements Serializable {

    /**
     * 支付超时时间 单位/秒
     * 最少3分钟 最长30天
     */
    @NotNull
    @Min(15 * 60)
    @Max(30 * 24 * 60 * 60)
    private Long payTimeout = 15 * 60L;

}
