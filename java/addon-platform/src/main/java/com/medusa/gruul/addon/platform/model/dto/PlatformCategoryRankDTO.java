package com.medusa.gruul.addon.platform.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author xiaoq
 * @Description PlatformCategoryRankDTO.java
 * @date 2022-11-04 09:40
 */
@Getter
@Setter
@ToString
public class PlatformCategoryRankDTO extends Page {
    /**
     * 类目ids
     */
    @Size(min = 1)
    private Set<Long> ids;

    /**
     * 查询得类目级别
     */
    private CategoryLevel categoryLevel;
}
