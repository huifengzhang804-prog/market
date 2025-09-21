package com.medusa.gruul.common.xxl.job.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Getter
@Setter
@Accessors(chain = true)
public class XxlJobInfo {

    /**
     * 任务主键
     */
    private int id;
    /*------------------- 基础配置 ------------------------*/
    /**
     * 执行器主键ID 必填
     */
    private int jobGroup;

    /**
     * 任务描述 必填
     */
    private String jobDesc;

    /**
     * 负责人 必填
     */
    private String author;

    /**
     * 报警邮件
     */
    private String alarmEmail;

    /*------------------- 调度配置 ------------------------*/
    /**
     * 调度类型 ScheduleTypeEnum 必填
     */
    private String scheduleType = ScheduleTypeEnum.CRON.name();

    /**
     * 调度配置，值含义取决于调度类型
     */
    private String scheduleConf;


    /*------------------- 任务配置 ------------------------*/
    /**
     * 执行器路由策略  ExecutorRouteStrategyEnum 必填
     */
    private String executorRouteStrategy = ExecutorRouteStrategyEnum.RANDOM.name();

    /**
     * 执行器，任务Handler名称
     */
    private String executorHandler;

    /**
     * 执行器，任务参数
     */
    private String executorParam;


    /*------------------- 基础配置 ------------------------*/
    /**
     * 调度过期策略 MisfireStrategyEnum 必填
     */
    private String misfireStrategy = MisfireStrategyEnum.DO_NOTHING.name();

    /**
     * 阻塞处理策略 ExecutorBlockStrategyEnum 必填
     */
    private String executorBlockStrategy = ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name();

    /**
     * 任务执行超时时间，单位秒
     */
    private int executorTimeout = 0;

    /**
     * 失败重试次数
     */
    private int executorFailRetryCount;

    /**
     * GLUE类型 GlueTypeEnum 必填
     */
    private String glueType = GlueTypeEnum.BEAN.name();

    /**
     * GLUE源代码
     */
    private String glueSource;

    /**
     * GLUE备注
     */
    private String glueRemark;

    /**
     * 子任务ID，多个逗号分隔
     */
    private String childJobId;

    /**
     * 调度状态：0-停止，1-运行 默认 运行
     */
    private int triggerStatus = TriggerStatusEnum.START.getCode();

}
