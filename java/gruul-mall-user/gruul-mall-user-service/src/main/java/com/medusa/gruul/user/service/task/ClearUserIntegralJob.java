package com.medusa.gruul.user.service.task;

import com.medusa.gruul.user.api.model.vo.IntegralRulesVO;
import com.medusa.gruul.user.service.service.UserIntegralService;
import com.medusa.gruul.user.service.service.addon.IntegralAddonSupporter;
import com.xxl.job.core.handler.IJobHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shishuqian
 * date 2023/2/13
 * time 16:51
 * 执行清除用户积分操作
 **/

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearUserIntegralJob extends IJobHandler {

    private final UserIntegralService userIntegralService;

    private final IntegralAddonSupporter integralAddonSupporter;

    /**
     * todo 问题比较大 需要优化
     *  1. 应该用积分RPC用户清积分接口,而不是用户RPC积分插件
     *  2. 性能问题严重, 遍历了用户表, 且没有分页 服务器和数据库压力比较大
     *  3. 可是考虑批量处理, 但是批量处理的话, 可能会有数据不一致的问题
     *  优化方案: 1. 积分RPC用户接口 2. 分页处理 3.可考虑批量处理
     *  by 张治保
     */
    @Override
    public void execute() {
        log.debug("======================>执行任务...");
        //如果存在积分插件，才执行清理积分操作
        IntegralRulesVO integralRules = this.integralAddonSupporter.getIntegralRule();
        if (integralRules != null) {
            log.debug("==============>清除用户过期积分任务开始执行...");
            this.userIntegralService.clearUserIntegralBySystemTime();
            log.debug("==============>清除用户过期积分任务执行完成...");
        }

    }
}
