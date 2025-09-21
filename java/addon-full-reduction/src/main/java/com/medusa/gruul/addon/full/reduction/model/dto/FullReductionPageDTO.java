package com.medusa.gruul.addon.full.reduction.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.full.reduction.model.enums.FullQueryStatus;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReduction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/6/17
 */

@Getter
@Setter
@Accessors(chain = true)
public class FullReductionPageDTO extends Page<FullReduction> {

    /**
     * 店铺 id
     */
    @JSONField(serialize = false, deserialize = false)
    private Long shopId;


    /**
     * 活动状态
     */
    private FullQueryStatus status;

    /**
     * 活动名称
     */
    private String name;

}
