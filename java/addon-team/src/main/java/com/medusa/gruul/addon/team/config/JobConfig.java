package com.medusa.gruul.addon.team.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.team.model.constant.TeamConst;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.service.TeamService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2023/3/17
 */
@Configuration
@RequiredArgsConstructor
public class JobConfig {

	private final TeamService teamService;

	/**
	 * 拼团活动开启
	 */
	@XxlJob(TeamConst.TEAM_OPEN_JOB_HANDLER)
	public void teamOpenJobHandler() {
		String jobParam = XxlJobHelper.getJobParam();
		if (StrUtil.isEmpty(jobParam)) {
			return;
		}
		teamService.openTeamActivity(JSON.parseObject(jobParam, TeamKey.class));
	}

	/**
	 * 拼团活动关闭 定时任务回调
	 */
	@XxlJob(TeamConst.TEAM_CLOSE_JOB_HANDLER)
	public void teamCloseJobHandler() {
		String jobParam = XxlJobHelper.getJobParam();
		if (StrUtil.isEmpty(jobParam)) {
			return;
		}
		teamService.closeTeamActivity(JSON.parseObject(jobParam, TeamKey.class));
	}

	/**
	 * 拼团订单关闭定时任务回调
	 */
	@XxlJob(TeamConst.TEAM_ORDER_CLOSE_JOB_HANDLER)
	public void teamOrderCloseJobHandler() {
		String jobParam = XxlJobHelper.getJobParam();
		if (StrUtil.isEmpty(jobParam)) {
			return;
		}
		teamService.closeTeamOrder(jobParam);
	}


}
