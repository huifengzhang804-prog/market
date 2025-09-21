package com.medusa.gruul.addon.matching.treasure.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SetMealQueryDTO extends Page<SetMealVO> {

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动状态
     */
    private SetMealStatus setMealStatus;



}
