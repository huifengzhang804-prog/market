package com.medusa.gruul.carrier.pigeon.service.im.constants;

/**
 * IM Redis Key常量类
 * @author An.Yan
 */
public class IMRedisConstant {

    /**
     * 用户信息Key
     */
    public static final String IM_USERINFO_KEY = "im:userinfo";

    /**
     * 店铺信息Key
     */
    public static final String IM_SHOP_INFO_KEY = "im:shopinfo";

    /**
     * 指向某家店铺所拥有的聊天室的Key
     */
    public static final String IM_GROUP_CHAT_ROOM_KEY = "im:group:chat:room:%d:rooms";

    /**
     * 指向存储聊天记录的聊天室的Key
     */
    public static final String IM_ROOM_KEY = "im:room:%d:%d";

    /**
     * 指向平台/店铺所拥有的聊天室
     */
    public static final String IM_PLATFORM_CHATROOM_KEY = "im:platform:chat:room:%d:rooms";

    public static final String IM_PLATFORM_ROOM_KEY = "im:platform:room:%d:%d";


}
