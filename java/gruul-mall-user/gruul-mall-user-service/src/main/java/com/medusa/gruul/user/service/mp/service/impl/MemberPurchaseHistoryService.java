package com.medusa.gruul.user.service.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.service.model.dto.MemberPurchaseQueryDTO;
import com.medusa.gruul.user.service.mp.entity.MemberPurchaseHistory;
import com.medusa.gruul.user.service.mp.mapper.MemberPurchaseHistoryMapper;
import com.medusa.gruul.user.service.mp.service.IMemberPurchaseHistoryService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author jipeng
 * @Description 会员流水记录
 * @date 2022-11-15 15:12
 */
@Service
@Slf4j
public class MemberPurchaseHistoryService extends ServiceImpl<MemberPurchaseHistoryMapper, MemberPurchaseHistory> implements
    IMemberPurchaseHistoryService {


  @Override
  public Page<MemberPurchaseHistory> queryList(MemberPurchaseQueryDTO queryDTO) {
      return lambdaQuery()
        .in(CollectionUtil.isNotEmpty(queryDTO.getExportIds()), MemberPurchaseHistory::getId,queryDTO.getExportIds())
        .like(StringUtils.isNotBlank(queryDTO.getNo()), MemberPurchaseHistory::getNo,
            queryDTO.getNo())
        .like(StringUtils.isNotBlank(queryDTO.getNickName()),
            MemberPurchaseHistory::getUserNickName, queryDTO.getNickName())
        .like(StringUtils.isNotBlank(queryDTO.getUserPhone()), MemberPurchaseHistory::getUserPhone,
            queryDTO.getUserPhone())
        .eq(StringUtils.isNotBlank(queryDTO.getLevel()), MemberPurchaseHistory::getRankCode,
            queryDTO.getLevel())
        .ge(Objects.nonNull(queryDTO.getBuyStartTime()), MemberPurchaseHistory::getCreateTime,
            queryDTO.getBuyStartTime())
        .le(Objects.nonNull(queryDTO.getBuyEndTime()), MemberPurchaseHistory::getCreateTime,
            queryDTO.getBuyEndTime())
        .ge(Objects.nonNull(queryDTO.getExpireStartTime()), MemberPurchaseHistory::getExpireTime,
            queryDTO.getExpireStartTime())
        .le(Objects.nonNull(queryDTO.getExpireEndTime()), MemberPurchaseHistory::getExpireTime,
            queryDTO.getExpireEndTime())

        .orderByDesc(MemberPurchaseHistory::getId)
        .page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()));
  }
}
