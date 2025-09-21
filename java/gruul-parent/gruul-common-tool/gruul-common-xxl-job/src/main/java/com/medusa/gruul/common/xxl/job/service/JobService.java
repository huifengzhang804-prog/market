package com.medusa.gruul.common.xxl.job.service;


import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;

/**
 * @author 张治保
 * date 2023/3/17
 */
public interface JobService {

    String URL_PREFIX = "/jobinfo/2b";
    String ADD_TASK_URL = URL_PREFIX + "/add";
    /**
     * 编辑xxl任务请求路径
     */
    String UPDATE_TASK_URL = URL_PREFIX + "/update";
    /**
     * 删除任务
     */
    String REMOVE_TASK_URL = URL_PREFIX + "/remove";
    /**
     * 启动xxl任务请求路径
     */
    String START_TASK_URL = URL_PREFIX + "/start";
    /**
     * 停止xxl任务请求路径
     */
    String STOP_TASK_URL = URL_PREFIX + "/stop";

    /**
     * 新增任务
     *
     * @param jobParam 任务参数
     * @return 任务id
     */
    int add(XxlJobInfo jobParam);

    /**
     * 更新任务
     *
     * @param jobParam 任务参数
     */
    void update(XxlJobInfo jobParam);

    /**
     * 删除任务
     *
     * @param jobId 任务id
     */
    void remove(int jobId);

    /**
     * 启动任务
     *
     * @param jobId 任务id
     */
    void start(int jobId);

    /**
     * 停止任务
     *
     * @param jobId 任务id
     */
    void stop(int jobId);

}
