package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.service.model.vo.UserBalanceHistoryQueryVo;
import com.medusa.gruul.user.service.mp.entity.UserBalanceHistory;
import java.util.List;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.mp.service
 * @author:jipeng
 * @createTime:2024/1/19 13:30
 * @version:1.0
 */
public interface IUserBalanceHistoryService extends IService<UserBalanceHistory>  {

  /**
   * 分页查询
   * @param queryVo 查询vo
   * @return UserBalanceHistory
   */
  Page<UserBalanceHistory> queryList(UserBalanceHistoryQueryVo queryVo);

  /**
   * 储值流水备注
   *
   * @param ids ids
   * @param remark 备注
   */
  void remark(List<Long> ids, String remark);
}
