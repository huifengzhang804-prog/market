package com.medusa.gruul.addon.seckill.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.seckill.model.vo.SeckillRoundProductVO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/5/31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillRoundProductPageDTO extends Page<SeckillRoundProductVO> {

    /**
     * 店铺 id 指定店铺查询
     */
    private Long shopId;

    /**
     * 活动开始时间 作为活动场次的唯一标识
     */
    @NotNull
    private LocalDateTime start;
}
