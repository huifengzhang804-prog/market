package com.medusa.gruul.live.service.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author miskw
 * @date 2022/11/4
 */
@Data
@Accessors(chain = true)
public class BroadcastDto implements Serializable {
    private static final long serialVersionUID = 42L;
    /**
     * 小程序appid
     */
    private String appid;
    /**
     * 小程序密钥
     */
    private String secret;
    /**
     * 起始拉取视频，0表示从第一个视频片段开始拉取
     */
    private Number start;
    /**
     * 每次拉取的数量，建议100以内
     */
    private Number limit;
    /**
     * 只能填"get_replay"，表示获取回放。
     */
    private String action;
    /**
     * 当 action 有值时该字段必填，直播间ID
     */
    private Number room_id;

}
