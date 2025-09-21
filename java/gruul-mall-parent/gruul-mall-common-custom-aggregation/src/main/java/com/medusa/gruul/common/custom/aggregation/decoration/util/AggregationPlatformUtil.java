package com.medusa.gruul.common.custom.aggregation.decoration.util;

import com.medusa.gruul.common.custom.aggregation.decoration.enums.AggregationPlatform;
import com.medusa.gruul.common.system.model.model.Platform;

/**
 * @author xiaoq
 * @Description 平台工具类
 * @date 2022-11-02 18:51
 */
public class AggregationPlatformUtil {

    private AggregationPlatformUtil() {

    }



    public static AggregationPlatform getAggregationPlatform(Platform platform) {
        switch (platform) {
            case ANDROID:
            case HARMONY:
            case H5:
            case WECHAT_MP:
            case IOS:
                return AggregationPlatform.OTHERS;
            case PC:
                return AggregationPlatform.PC;
            case WECHAT_MINI_APP:
                return AggregationPlatform.WECHAT_MINI_APP;
            default:
        }
        return null;
    }
}
