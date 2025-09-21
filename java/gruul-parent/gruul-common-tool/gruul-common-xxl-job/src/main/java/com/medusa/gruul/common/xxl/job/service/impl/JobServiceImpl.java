package com.medusa.gruul.common.xxl.job.service.impl;

import com.medusa.gruul.common.xxl.job.XxlJobProperties;
import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;

import java.util.Map;

/**
 * @author 张治保
 * date 2023/3/17
 */
public class JobServiceImpl implements JobService {

    private final String adminAddress;
    private final String accessToken;
    private final int groupId;


    public JobServiceImpl(XxlJobProperties xxlJobProperties) {
        this.adminAddress = xxlJobProperties.getAdmin().getAdminAddresses();
        this.accessToken = xxlJobProperties.getExecutor().getAccessToken();
        this.groupId = xxlJobProperties.getExecutor().getId();
    }


    @Override
    @SuppressWarnings("unchecked")
    public int add(XxlJobInfo jobParam) {
        if (jobParam.getJobGroup() == 0) {
            jobParam.setJobGroup(groupId);
        }
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + ADD_TASK_URL, accessToken, 6000, jobParam, String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }
        return Integer.parseInt(result.getContent());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(XxlJobInfo jobParam) {
        if (jobParam.getJobGroup() == 0) {
            jobParam.setJobGroup(groupId);
        }
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + UPDATE_TASK_URL, accessToken, 6000, jobParam, String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void remove(int jobId) {
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + REMOVE_TASK_URL, accessToken, 6000, Map.of("id", jobId), String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(int jobId) {
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + START_TASK_URL, accessToken, 6000, Map.of("id", jobId), String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void stop(int jobId) {
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + STOP_TASK_URL, accessToken, 6000, Map.of("id", jobId), String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }
    }
}
