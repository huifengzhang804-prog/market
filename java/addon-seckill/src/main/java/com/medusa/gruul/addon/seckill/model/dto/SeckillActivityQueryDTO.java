package com.medusa.gruul.addon.seckill.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.seckill.model.enums.SeckillQueryStatus;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillActivity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Getter
@Setter
@ToString
public class SeckillActivityQueryDTO extends Page<SeckillActivity> {
    /**
     * 活动状态
     */
    private SeckillQueryStatus status;

    /**
     * 活动名称
     */
    private String name;
}
