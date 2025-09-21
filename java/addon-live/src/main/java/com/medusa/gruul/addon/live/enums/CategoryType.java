package com.medusa.gruul.addon.live.enums;

import lombok.Getter;

/**
 * @author miskw
 * @date 2023/6/10
 * @describe 描述
 */
@Getter
public enum CategoryType {
    /**
     * 涉黄
     */
    YELLOW_INVOLVEMENT,
    /**
     * 涉毒
     */
    DRUG_RELATED,
    /**
     * 敏感话题
     */
    SENSITIVE_TOPIC,
    /**
     * 其它
     */
    OTHER;

    CategoryType() {
    }
}

