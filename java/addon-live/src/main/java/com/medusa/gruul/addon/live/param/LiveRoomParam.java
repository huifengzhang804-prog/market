package com.medusa.gruul.addon.live.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author miskw
 * @date 2023/6/7
 * @describe 直播间列表参数
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class LiveRoomParam extends Page<LiveRoomVO> {
    /**
     * 直播标题
     */
    private String liveTitle;
    /**
     * 主播昵称
     */
    private String anchorNickname;
    /**
     * 直播id
     */
    private Long liveId;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 搜索开始时间
     */
    private LocalDate beginTime;
    /**
     * 搜素结束时间
     */
    private LocalDate endTime;
    /**
     * 直播间状态
     */
    private LiveRoomStatus status;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;


}
