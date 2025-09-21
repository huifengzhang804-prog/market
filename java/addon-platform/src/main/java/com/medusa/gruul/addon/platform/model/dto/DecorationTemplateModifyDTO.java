package com.medusa.gruul.addon.platform.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>平台装修模板修改DTO</p>
 *
 * @author Andy.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DecorationTemplateModifyDTO extends DecorationTemplateCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1164336670268151398L;
    /**
     * 模板ID
     */
    @NotNull
    private Long id;
}
