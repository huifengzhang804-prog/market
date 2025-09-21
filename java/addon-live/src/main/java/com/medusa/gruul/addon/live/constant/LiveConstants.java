package com.medusa.gruul.addon.live.constant;


/**
 * @author miskw
 * @date 2023/6/3
 * @describe 描述
 */
public interface LiveConstants {
    String RTMP = "rtmp://";
    String HTTP = "http://";
    String WEBRTC = "webrtc://";
    /**
     * 推流与拉流生成地址
     */
    String CONCATENATED_ADDRESS = "{}txSecret={}&txTime={}";
    /**
     * 推流回调地址
     * 120.46.209.211:8111
     */
    String PUSH_ADDRESS = "http://{}/api/notify?action=record";
    /**
     * 断流回调
     * 120.46.209.211:8111
     */
    String CUTOFF_ADDRESS = "http://{}/api/notify?action=snapshot";
    /**
     * 直播互动
     */
    String LIVE_INTERACTION = "gruul:mall:live:interaction";
    /**
     * 直播间点赞key
     */
    String LIVE_LIKE = "gruul:mall:live:like";
    /**
     * 直播间观看人数
     */
    String LIVE_VIEWERSHIP = "gruul:mall:live:viewership";

    String LIVE_SHOP = "gruul:mall:live:shop";

    /**
     * 直播间观看人数lock锁
     */
    String LIVE_VIEWERSHIP_LOCK = "gruul:mall:live:viewership:lock";

}
