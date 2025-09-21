package com.medusa.gruul.addon.platform.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>变更装修模板状态DTO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
public class DecorationTemplateStatusChangeDTO implements Serializable {

    /**
     * 模板ID
     */
    @NotNull
    private Long templateId;
}
