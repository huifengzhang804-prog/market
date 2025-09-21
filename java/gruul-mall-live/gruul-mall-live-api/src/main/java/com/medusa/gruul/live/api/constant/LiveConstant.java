package com.medusa.gruul.live.api.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author miskw
 * @date 2023/1/4
 */

public interface LiveConstant {
    String GOODS_URL = "basePackage/pages/commodityInfo/InfoEntrance?goodId={}&shopId={}";

    String JSON_NICKNAME = "nickname";
    String JSON_HEADING_IMG = "headingimg";
}
