package com.medusa.gruul.common.custom.aggregation.decoration.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.common.custom.aggregation.decoration.enums.AggregationPlatform;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.system.model.model.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author xiaoq
 * @description 装修通用entity
 * @date 2022-10-31 19:06
 */
@Getter
@Setter
@Accessors(chain = true)
public class DecorateEntity extends BaseEntity {



    @TableField(value = "platforms")
    private AggregationPlatform platforms;
}
