package com.medusa.gruul.shop.api.model.dto;

import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 复制平台装修模板的参数
 *
 * @author 张治保
 * @since 2024/3/1
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DecorationCopyTemplateParamDTO implements Serializable {

    /**
     * 目标 id 可为空
     */
    private Long id;

    /**
     * 业务类型
     */
    @NotNull
    private TemplateBusinessTypeEnum business;

    /**
     * 终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpoint;

}
