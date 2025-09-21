package com.medusa.gruul.addon.integral.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 积分行为vo
 *
 * @author shishuqian
 * date 2023/2/10
 * time 17:03
 **/

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class IntegralBehaviorVO {

    /**
     * 签到/登录的连续天数
     */
    private Integer continueDays;

    /**
     * 今天是否已经 签到/登录过
     */
    private Boolean todayState;
}
