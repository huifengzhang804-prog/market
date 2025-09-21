package com.medusa.gruul.addon.team.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamOrderPageDTO extends Page<TeamOrderPageVO> {

    /**
     * 活动状态
     */
    private TeamOrderStatus status;

    /**
     * 发起人（团长） 模糊查询
     */
    private String commander;

    /**
     * 商品名称模糊查询
     */
    private String productName;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 团员用户id
     */
    private Long userId;

    /**
     * 团号 传了团号 将该团排在第一位
     */
    private String teamNo;


}
