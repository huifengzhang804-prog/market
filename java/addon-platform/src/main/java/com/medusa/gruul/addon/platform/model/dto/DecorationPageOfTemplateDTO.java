package com.medusa.gruul.addon.platform.model.dto;

import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>装修页面creat dto</p>
 *
 * @author An.Yan
 */
@Data
@Accessors(chain = true)
public class DecorationPageOfTemplateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8912217961740837026L;
    /**
     * 页面所属的模版的类型
     */
    @NotNull
    private TemplateTypeEnum templateType;

    /**
     * 页面对应的业务类型,当{@link DecorationPage#templateType}等于{@link TemplateTypeEnum#PLATFORM}时,
     * 该字段为NULL
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;

    private List<PageTypeEnum> typeEnumList;

}
