package com.medusa.gruul.live.service.model.vo;

import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.api.entity.LiveRoom;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/11
 * @Describe 直播详情返回对象
 */
@Data
public class LiveInfoDetails extends LiveRoom {
    /**
     * 主播信息
     */
    private LiveMember liveMember;
}
