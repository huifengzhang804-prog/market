package com.medusa.gruul.afs.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.service.mp.entity.AfsHistory;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 售后历史 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
public interface IAfsHistoryService extends IService<AfsHistory> {

  /**
   * 批量查询工单列表的最新一条售后记录
   * @param afsNos 工单号集合
   * @return 工单map
   */
  Map<String, AfsHistory> lastAfsHistory(Set<String> afsNos);
}
