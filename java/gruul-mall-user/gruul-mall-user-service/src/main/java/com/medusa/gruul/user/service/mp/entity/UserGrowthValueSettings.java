package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.service.model.dto.ConsumeJsonDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 会员成长值设置
 *
 * @author WuDi
 * @since 2023-05-15
 */
@Getter
@Setter
@TableName(value = "t_user_growth_value_settings", autoResultMap = true)
@Accessors(chain = true)
public class UserGrowthValueSettings extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 是否开启注册
     */
    @TableField("register_enabled")
    private Boolean registerEnabled;

    /**
     * 注册成长值
     */
    @TableField("register_growth_value")
    private Long registerGrowthValue;

    /**
     * 消费成长值
     */
    @TableField("consume_enabled")
    private Boolean consumeEnabled;

    /**
     * 消费成长值Json
     */
    @TableField(value = "consume_json", typeHandler = Fastjson2TypeHandler.class)
    private List<ConsumeJsonDTO> consumeJson;

}
