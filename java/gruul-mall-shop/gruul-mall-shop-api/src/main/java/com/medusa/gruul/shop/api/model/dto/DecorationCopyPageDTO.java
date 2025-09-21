package com.medusa.gruul.shop.api.model.dto;

import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 平台的店铺装修装修页面副本
 *
 * @author Andy.Yan
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DecorationCopyPageDTO implements Serializable {

    /**
     * 页面名称
     */
    @NotNull
    private String name;

    /**
     * 页面json
     */
    @NotNull
    private String properties;

    /**
     * 页面类型枚举
     */
    @NotNull
    private PageTypeEnum type;
}