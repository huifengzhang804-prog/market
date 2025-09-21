package com.medusa.gruul.addon.platform.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>装修页面modify dto</p>
 *
 * @author An.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DecorationPageModifyDTO extends DecorationPageCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3762448023648263959L;

    /**
     * 页面名称
     */
    @NotNull
    private Long id;
}
