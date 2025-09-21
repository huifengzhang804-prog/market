package com.medusa.gruul.shop.api.model.dto;

import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/3/1
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DecorationCopyPageParamDTO extends DecorationCopyTemplateParamDTO {

    /**
     * 页面类型
     */
    @NotNull
    private PageTypeEnum pageType;

}
