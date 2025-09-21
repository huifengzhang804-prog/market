package com.medusa.gruul.shop.api.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 平台的店铺装修模板
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class PlatformShopDecorationTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = -6888578468400763677L;

    /**
     * 模板名称
     */
    @NotNull
    private String name;

    /**
     * 模版说明
     */
    private String description;

    /**
     * 组成模版的页面
     */
    @NotNull
    private List<DecorationCopyPageDTO> pages;

}
