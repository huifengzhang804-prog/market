package com.medusa.gruul.user.service.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.service.model.vo.UserBalanceHistoryQueryVo;
import com.medusa.gruul.user.service.mp.entity.UserBalanceHistory;
import com.medusa.gruul.user.service.mp.mapper.UserBalanceHistoryMapper;
import com.medusa.gruul.user.service.mp.service.IUserBalanceHistoryService;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @description: 用户储值流水Service实现类
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.mp.service.impl
 * @author:jipeng
 * @createTime:2024/1/19 13:31
 * @version:1.0
 */
@Service
public class UserBalanceHistoryService extends
    ServiceImpl<UserBalanceHistoryMapper, UserBalanceHistory> implements
    IUserBalanceHistoryService {

  @Override
  public Page<UserBalanceHistory> queryList(UserBalanceHistoryQueryVo queryVo) {

      return lambdaQuery()
        .in(CollectionUtil.isNotEmpty(queryVo.getExportIds()),UserBalanceHistory::getId,queryVo.getExportIds())
        .like(StringUtils.isNotBlank(queryVo.getNo()), UserBalanceHistory::getNo,
            queryVo.getNo())
        .like(StringUtils.isNotBlank(queryVo.getUserNickName()),
            UserBalanceHistory::getUserNickName, queryVo.getUserNickName())
        .like(StringUtils.isNotBlank(queryVo.getUserPhone()), UserBalanceHistory::getUserPhone,
            queryVo.getUserPhone())
        .eq(Objects.nonNull(queryVo.getOperatorType()), UserBalanceHistory::getOperatorType,
            queryVo.getOperatorType())
        .like(StringUtils.isNotBlank(queryVo.getOrderNo()), UserBalanceHistory::getOrderNo,
            queryVo.getOrderNo())
        .ge(Objects.nonNull(queryVo.getOperatorStartTime()), UserBalanceHistory::getCreateTime,
            queryVo.getOperatorStartTime())
        .le(Objects.nonNull(queryVo.getOperatorEndTime()), UserBalanceHistory::getCreateTime,
            queryVo.getOperatorEndTime())
        .orderByDesc(UserBalanceHistory::getId)
        .page(new Page<>(queryVo.getCurrent(), queryVo.getSize()));
  }

  @Override
  public void remark(List<Long> ids, String remark) {
    lambdaUpdate().set(UserBalanceHistory::getRemark, remark)
        .in(UserBalanceHistory::getId, ids)
        .update();
  }
}
