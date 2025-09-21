package com.medusa.gruul.live.service.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.live.service.model.dto.BroadcastDto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author miskw
 * @date 2022/11/3
 */
public class LiveBroadcastUtils {
    /**
     * 接口调用凭证
     */
    private static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    /**
     * 获取直播间列表和回放
     */
    private static String BROAD_URL = "https://api.weixin.qq.com/wxa/business/getliveinfo?access_token=";

    /**
     * 获取token凭证
     * @param dto
     * @return
     */
    public static String getToken(BroadcastDto dto) {
        Map map = new HashMap<>(8);
        map.put("grant_type", "client_credential");
        map.put("appid", dto.getAppid());
        map.put("secret", dto.getSecret());
        return HttpUtil.get(TOKEN_URL, map);
    }

    /**
     * 获取直播列表
     * @param token
     * @param dto
     * @return
     */
    public static String getBroad(String token, BroadcastDto dto) {
        StringBuilder sb = new StringBuilder();
        String url = sb.append(BROAD_URL).append(token).toString();
        Map map = new HashMap(8);
        if (StrUtil.isNotBlank(dto.getAction())) {
            map.put("action", dto.getAction());
        }
        if (dto.getRoom_id() != null) {
            map.put("room_id", dto.getRoom_id());
        }
        map.put("start", dto.getStart());
        map.put("limit", dto.getLimit());
        return HttpUtil.post(url, JSONUtil.toJsonStr(map));

    }


}
