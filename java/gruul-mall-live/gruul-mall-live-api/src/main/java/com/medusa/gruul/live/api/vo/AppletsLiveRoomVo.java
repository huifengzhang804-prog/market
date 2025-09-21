package com.medusa.gruul.live.api.vo;

import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author miskw
 * @date 2022/11/22
 * @describe 小程序直播间
 */
@Data
@Accessors(chain = true)
public class AppletsLiveRoomVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播间Id
     */
    private Long id;
    /**
     * 微信直播间Id
     */
    private Long wechatRoomId;
    /**
     * 直播间名称
      */
    private String roomName;
    /**
     * 背景图
     */
    private String coverImg;
    /**
     * shopId
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 店铺头像
     */
    private String shopLogo;
    /**
     * 直播状态
     */
    private RoomStatus status;
    /**
     * 直播间商品
     */
    private List<LiveGoodsExamine> examineList;
}
