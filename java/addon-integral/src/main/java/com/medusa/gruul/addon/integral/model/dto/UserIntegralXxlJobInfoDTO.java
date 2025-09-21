package com.medusa.gruul.addon.integral.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分定时任务dto
 *
 * @author shishuqian
 * date 2023/2/15
 * time 10:40
 **/
@Data
public class UserIntegralXxlJobInfoDTO {
    /**
     * 任务id 编辑时传参
     */
    private Integer id;
    /**
     * 描述
     */
    private String jobDesc;
    /**
     * cron表达式
     */
    private String jobCron;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * JobHandler
     */
    private String executorHandler;

    /**
     * executorParam
     */
    private String executorParam;
    /**
     * 执行人
     */
    private String author;
    /**
     * 是否编辑
     * true 为编辑
     * false 保存
     */
    private Boolean editFlag;

}
