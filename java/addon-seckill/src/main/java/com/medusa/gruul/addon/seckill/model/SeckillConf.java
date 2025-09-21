package com.medusa.gruul.addon.seckill.model;

import com.medusa.gruul.common.web.valid.annotation.Limit;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillConf implements Serializable {

    /**
     * 秒杀活动时长,单位小时
     * 可选值 1，2，3，4 ，6，8，12，24
     */
    @NotNull
    @Limit({"1", "2", "3", "4", "6", "8", "12", "24"})
    private Integer duration;

    /**
     * 秒杀活动描述 富文本
     */
    private String desc;

}
