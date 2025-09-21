package com.medusa.gruul.user.service.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.user.service.model.dto.IntegralDetailQueryDTO;
import com.medusa.gruul.user.service.model.vo.StatementStatisticsVO;
import com.medusa.gruul.user.service.model.vo.UserIntegralDetailVO;
import com.medusa.gruul.user.service.mp.entity.UserIntegralDetail;
import com.medusa.gruul.user.service.mp.mapper.UserIntegralDetailMapper;
import com.medusa.gruul.user.service.mp.service.IUserIntegralDetailService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-02-01 17:42
 */
@Service
@RequiredArgsConstructor
public class UserIntegralDetailServiceImpl extends ServiceImpl<UserIntegralDetailMapper, UserIntegralDetail> implements IUserIntegralDetailService {


    @Override
    public UserIntegralDetailVO detailPage(IntegralDetailQueryDTO integralDetailQuery) {
        IPage<UserIntegralDetail> userIntegralDetailIPage = baseMapper.detailPage(integralDetailQuery);
        UserIntegralDetailVO userIntegralDetail = UserIntegralDetailVO.toUserIntegralDetailVo(userIntegralDetailIPage);
        List<UserIntegralDetail> records = userIntegralDetail.getRecords();
        if (CollUtil.isEmpty(records)){
            return userIntegralDetail.setStatistics(new StatementStatisticsVO());
        }
        Map<ChangeType, List<UserIntegralDetail>> collect = records.stream().collect(Collectors.groupingBy(UserIntegralDetail::getChangeType));
        StatementStatisticsVO statementStatistics = new StatementStatisticsVO();
        statementStatistics.setEarnIntegral(Option.of(collect.get(ChangeType.INCREASE)).getOrElse(new ArrayList<>()).stream().mapToLong(UserIntegralDetail::getVariationIntegral).sum());
        statementStatistics.setExchangeIntegral(Option.of(collect.get(ChangeType.REDUCE)).getOrElse(new ArrayList<>()).stream().mapToLong(UserIntegralDetail::getVariationIntegral).sum());
        userIntegralDetail.setStatistics(statementStatistics);
        return userIntegralDetail;
    }
}
