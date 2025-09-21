package com.medusa.gruul.search.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 品牌关注表
 * </p>
 *
 * @author WuDi
 * @since 2023-02-02
 */
@Getter
@Setter
@TableName("t_search_brand_follow")
@Accessors(chain = true)
public class SearchBrandFollow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 品牌id
     */
    private Long brandId;
}
