package com.medusa.gruul.goods.api.enums;

import lombok.Getter;

/**
 * @author miskw
 * @date 2023/1/30
 *
 */
@Getter
public enum CopyGoodsType {
    /**
     * 淘宝
     */
    TaoBao("taobao"),
    /**
     * 京东
     */
    JD("jd"),
    /**
     * 阿里
     */
    AliBaBa("alibaba");

    CopyGoodsType(String type) {
        this.type = type;

    }

    private final String type;

}
