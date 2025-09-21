package com.medusa.gruul.addon.platform.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>装修模板复制DTO</p>
 *
 * @author Andy.Yan
 */
@Data
public class DecorationTemplateCloneDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4369853415972319934L;

    @NotNull
    private Long templateId;
}
