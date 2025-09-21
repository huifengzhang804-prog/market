package com.medusa.gruul.common.custom.aggregation.classify.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author xiaoq
 * @Description CategoryRankDTO.java
 * @date 2022-10-18 17:48
 */
@Getter
@Setter
@ToString
public class CategoryRankDTO extends Page {

    /**
     * 一级类目ids
     */
    private Set<Long> ids;


    /**
     * 查询得类目级别
     */
    private CategoryLevel categoryLevel;
}
