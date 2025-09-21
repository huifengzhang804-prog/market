package com.medusa.gruul.addon.live.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.live.enums.CategoryType;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 描述
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_prohibited", autoResultMap = true)
public class Prohibited extends BaseEntity {
    /**
     * 质检员
     */
    private String qualityInspector;
    /**
     * 来源id 直播间 | 主播
     */
    private Long sourceId;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 直播 | 主播
     */
    @TableField("`type`")
    private ProhibitedType type;
    /**
     * 禁播类别  涉黄、涉毒...
     */
    @TableField(value = "category_types", typeHandler = Fastjson2TypeHandler.class)
    private List<CategoryType> categoryTypes;
    /**
     * 禁播原因
     */
    private String reason;
    /**
     * 相关证据 逗号拼接图片
     */
    private String relevantEvidence;
}
