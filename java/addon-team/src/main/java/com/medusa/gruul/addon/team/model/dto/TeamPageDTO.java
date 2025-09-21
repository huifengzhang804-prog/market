package com.medusa.gruul.addon.team.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.model.enums.TeamStatus;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamPageDTO extends Page<TeamPageVO> {

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动状态
     */
    private TeamStatus status;

    /**
     * 关键词
     */
    private String keyword;

}
