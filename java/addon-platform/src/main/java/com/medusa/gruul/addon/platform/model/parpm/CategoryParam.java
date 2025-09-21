package com.medusa.gruul.addon.platform.model.parpm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 类目查询 param
 * @Author: xiaoq
 * @Date : 2022-04-24 13:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryParam  extends Page<PlatformCategory> {
    /**
     * 分类级别：0->1级；1->2级 2>3级
     */
    private CategoryLevel level;

    /**
     * 父级id
     */
    private Long parentId;

}
