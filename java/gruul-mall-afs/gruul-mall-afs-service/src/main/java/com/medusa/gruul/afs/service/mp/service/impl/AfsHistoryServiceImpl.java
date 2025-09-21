package com.medusa.gruul.afs.service.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.medusa.gruul.afs.service.mp.entity.AfsHistory;
import com.medusa.gruul.afs.service.mp.mapper.AfsHistoryMapper;
import com.medusa.gruul.afs.service.mp.service.IAfsHistoryService;
import com.medusa.gruul.common.mp.model.TenantShop;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 售后历史 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
@Service
public class AfsHistoryServiceImpl extends ServiceImpl<AfsHistoryMapper, AfsHistory> implements
    IAfsHistoryService {

  @Override
  public Map<String, AfsHistory> lastAfsHistory(Set<String> afsNos) {

    List<AfsHistory> historyList = TenantShop.disable(() -> lambdaQuery()
        .eq(AfsHistory::getDeleted, Boolean.FALSE)
        .in(AfsHistory::getAfsNo, afsNos).list());
    HashMap<String, AfsHistory> result = Maps.newHashMap();
    if (!CollectionUtil.isEmpty(historyList)) {
      for (AfsHistory history : historyList) {
        result.put(history.getAfsNo(), history);
      }
    }

    return result;

  }
}
