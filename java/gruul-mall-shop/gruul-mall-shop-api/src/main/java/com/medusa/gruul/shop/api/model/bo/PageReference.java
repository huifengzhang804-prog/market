package com.medusa.gruul.shop.api.model.bo;

import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 由模板引用的页面
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class PageReference implements Serializable {

    @Serial
    private static final long serialVersionUID = 4126138386057070140L;

    @NotNull
    private Long pageId;

    @NotNull
    private PageTypeEnum pageType;

}