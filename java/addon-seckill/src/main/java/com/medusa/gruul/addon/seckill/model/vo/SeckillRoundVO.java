package com.medusa.gruul.addon.seckill.model.vo;

import com.medusa.gruul.addon.seckill.model.enums.SeckillQueryStatus;
import com.medusa.gruul.global.model.o.RangeDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillRoundVO {

    /**
     * 活动时间端
     */
    private RangeDateTime time;

    /**
     * 活动状态
     */
    private SeckillQueryStatus status;

}
