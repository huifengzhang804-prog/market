package com.medusa.gruul.global.model.express;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/8/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ExpressCompanyDTO implements Serializable {

    /**
     * 快递公司名称
     */
    @NotBlank
    private String expressCompanyName;
    /**
     * 快递公司代码
     */
    @NotBlank
    private String expressCompanyCode;

    /**
     * 快递单号
     */
    private String expressNo;

}
