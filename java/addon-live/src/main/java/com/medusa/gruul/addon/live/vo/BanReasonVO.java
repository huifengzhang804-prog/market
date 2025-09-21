package com.medusa.gruul.addon.live.vo;

import com.medusa.gruul.addon.live.enums.CategoryType;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 违禁原因
 */
@Getter
@Setter
@Accessors(chain = true)
public class BanReasonVO {
    private Long id;
    /**
     * 检察员
     */
    private String qualityInspector;
    /**
     * 禁播类型
     */
    private ProhibitedType type;
    /**
     * 禁播类别
     */
    private List<CategoryType> categoryTypes;
    /**
     * 禁播原因
     */
    private String reason;
    /**
     * 相关证据 逗号拼接图片
     */
    private String relevantEvidence;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
