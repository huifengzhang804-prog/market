package com.medusa.gruul.search.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandQueryDTO extends Page<SearchBrand> {


    /**
     * 品牌名称
     */
    private String brandName;
}
