package com.medusa.gruul.live.api.vo;

import com.medusa.gruul.live.api.enums.LiveRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2022/11/17
 * @Describe 直播成员返回对象
 */
@Data
public class LiveUserVo {

    private Long id;
    /**
     * 店铺Id
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 主播昵称
     */
    private String userName ;

    /**
     *  微信号
     */
    private String wechatNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户角色
     */
    private LiveRole role;
}
