package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 积分设置
 *
 * @author shishuqian
 * date 2023/2/9
 * time 11:09
 **/

@Getter
@Setter
@Accessors(chain = true)
@TableName("t_integral_setting")
public class IntegralSetting extends BaseEntity {

    private Long ratio;

    private Long ceiling;


}
