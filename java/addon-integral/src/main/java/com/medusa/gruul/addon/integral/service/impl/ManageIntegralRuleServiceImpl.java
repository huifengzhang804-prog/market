package com.medusa.gruul.addon.integral.service.impl;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.dto.IntegralRulesDTO;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;
import com.medusa.gruul.addon.integral.mp.service.IIntegralRulesService;
import com.medusa.gruul.addon.integral.service.ManageIntegralRuleService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.xxl.job.model.ScheduleTypeEnum;
import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端积分规则数据实现层
 *
 * @author xiaoq
 * Description 管理端积分规则数据实现层
 * date 2023-02-03 16:45
 */
@Service
@RequiredArgsConstructor
public class ManageIntegralRuleServiceImpl implements ManageIntegralRuleService {

    private final JobService jobService;

    private final IIntegralRulesService integralRulesService;


    /**
     * 积分规则批量保存
     *
     * @param integralRules 积分规则信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveIntegralRules(IntegralRulesDTO integralRules) {
        List<IntegralRules> rulesList = new ArrayList<>();
        Long count = integralRulesService.lambdaQuery().count();
        if (count >= CommonPool.NUMBER_FOUR) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "重复保存");
        }
        integralRules.validParam();
        integralRules.getIntegralGainRule().forEach(bean -> {
            IntegralRules rules = new IntegralRules();
            rules.setRuleInfo(integralRules.getRuleInfo())
                    .setUseRule(integralRules.getUseRule())
                    .setIndate(integralRules.getIndate())
                    .setRulesParameter(bean.getRulesParameter())
                    .setTaskId(CommonPool.NUMBER_ZERO)
                    .setOpen(bean.getOpen())
                    .setGainRuleType(bean.getGainRuleType());
            rulesList.add(rules);
        });
        integralRulesService.saveBatch(rulesList);
        LocalDate now = LocalDate.now();
        int taskId = jobService.add(
                new XxlJobInfo()
                        .setJobDesc(IntegralConstant.JOB_DESC)
                        .setAuthor(IntegralConstant.AUTHOR)
                        .setScheduleType(ScheduleTypeEnum.CRON.name())
                        .setScheduleConf(StrUtil.format(IntegralConstant.CRON_TEMPLATE, now.getDayOfMonth(), now.getMonthValue(), integralRules.getIndate()))
                        .setExecutorHandler(IntegralConstant.EXECUTOR_HANDLER)
        );
        integralRulesService.lambdaUpdate()
                .set(IntegralRules::getTaskId, taskId)
                .update();
    }


    /**
     * 积分规则批量修改 by GainRuleType
     *
     * @param integralRules 积分规则修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIntegralRules(IntegralRulesDTO integralRules) {
        integralRules.validParam();
        IntegralRules rules = Option.of(
                integralRulesService.lambdaQuery()
                        .select(IntegralRules::getId, IntegralRules::getTaskId)
                        .last(SqlHelper.SQL_LIMIT_1)
                        .one()
        ).getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "请先保存积分规则"));

        List<IntegralRules> integralRulesList = integralRules.getIntegralGainRule().stream().map(bean ->
                Option.of(
                                integralRulesService.lambdaQuery()
                                        .eq(IntegralRules::getGainRuleType, bean.getGainRuleType())
                                        .one()
                        ).getOrElse(new IntegralRules())
                        .setRuleInfo(integralRules.getRuleInfo())
                        .setUseRule(integralRules.getUseRule())
                        .setIndate(integralRules.getIndate())
                        .setRulesParameter(bean.getRulesParameter())
                        .setTaskId(rules.getTaskId())
                        .setOpen(bean.getOpen())
                        .setGainRuleType(bean.getGainRuleType())
        ).collect(Collectors.toList());
        integralRulesService.saveOrUpdateBatch(integralRulesList);


        //修改cron表达式
        LocalDate now = LocalDate.now();
        jobService.update(
                new XxlJobInfo()
                        .setId(rules.getTaskId())
                        .setJobDesc(IntegralConstant.JOB_DESC)
                        .setAuthor(IntegralConstant.AUTHOR)
                        .setScheduleType(ScheduleTypeEnum.CRON.name())
                        .setScheduleConf(StrUtil.format(IntegralConstant.CRON_TEMPLATE, now.getDayOfMonth(), now.getMonthValue(), integralRules.getIndate()))
                        .setExecutorHandler(IntegralConstant.EXECUTOR_HANDLER)
        );
    }

    @Override
    public IntegralRulesVO getIntegralRule() {
        return integralRulesService.getIntegralRule();
    }
}
