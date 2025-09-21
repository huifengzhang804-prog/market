package com.medusa.gruul.search.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandInitialVO {


    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌ids
     */
    private String brandIds;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 检索首字母
     */
    private String searchInitials;
}
