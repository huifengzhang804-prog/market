package com.medusa.gruul.addon.live.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/6/6
 * @describe 关注直播间列表查询参数
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FollowLiveParam extends Page<FollowLiveVO> {
    /**
     * 查询关键字
     */
    private String keyword;
    
    /**
     * 直播间状态
     */
    @NotNull(message = "直播间状态不能为空")
    private LiveRoomStatus status;

}
