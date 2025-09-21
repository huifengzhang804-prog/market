package com.medusa.gruul.common.xxl.job;

import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;

/**
 * @author 张治保
 * @since 2024/6/17
 */
public interface JobParent<T> {

    String DEFAULT_AUTHOR = "system";
    String DEFAULT_SCHEDULE_TYPE = "CRON";


    /**
     * 任务描述
     *
     * @return 任务描述
     */
    String desc();

    /**
     * 任务处理器
     *
     * @return 任务处理器
     */
    String handler();

    /**
     * 任务执行cron生成器
     *
     * @param t 参数
     * @return cron表达式
     */
    String cronGenerator(T t);

    /**
     * 任务作者
     *
     * @return 作者
     */
    default String author() {
        return DEFAULT_AUTHOR;
    }

    /**
     * 任务调度类型
     *
     * @return 调度类型
     */
    default String scheduleType() {
        return DEFAULT_SCHEDULE_TYPE;
    }

    /**
     * 转换为xxl-job info
     *
     * @param t 参数
     * @return xxl-job info
     */
    default XxlJobInfo toXxlJobInfo(T t) {
        return new XxlJobInfo()
                .setJobDesc(desc())
                .setAuthor(author())
                .setScheduleType(scheduleType())
                .setScheduleConf(cronGenerator(t))
                .setExecutorHandler(handler());
    }


    /**
     * 转换为xxl-job info
     *
     * @param t             参数
     * @param executorParam 执行参数
     * @return xxl-job info
     */
    default XxlJobInfo toXxlJobInfo(T t, String executorParam) {
        return new XxlJobInfo()
                .setJobDesc(desc())
                .setAuthor(author())
                .setScheduleType(scheduleType())
                .setScheduleConf(cronGenerator(t))
                .setExecutorHandler(handler())
                .setExecutorParam(executorParam);
    }
}
