package com.medusa.gruul.addon.seckill.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.seckill.model.vo.SeckillRoundVO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * @since 2024/7/4
 */
@Getter
@Setter
public class SeckillRoundPageDTO extends Page<SeckillRoundVO> {
    /**
     * 店铺 id
     */
    private Long shopId;
}
