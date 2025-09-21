package com.medusa.gruul.addon.live.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.enums.AnchorStatus;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/6/9
 * @describe 描述
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AnchorParam extends Page<AnchorVO> {
    /**
     * 主播id
     */
    private Long id;
    /**
     * 主播昵称
     */
    private String anchorNickname;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 主播状态
     */
    private AnchorStatus status;
    /**
     * 店铺id
     */
    private Long shopId;
}
