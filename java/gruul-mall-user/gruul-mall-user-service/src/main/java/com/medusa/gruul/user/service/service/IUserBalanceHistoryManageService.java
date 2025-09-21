package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import com.medusa.gruul.user.service.model.dto.UserBalanceHistoryDTO;
import com.medusa.gruul.user.service.model.vo.UserBalanceHistoryQueryVo;
import java.util.List;

/**
 * @description: 用户余额历史管理服务
 * @projectName:gruul-mall-user
 * @author:jipeng
 * @createTime:2024/1/19 14:14
 * @version:1.0
 */
public interface IUserBalanceHistoryManageService {

  /**
   * 异步保存用户储值的流水记录
   * @param userBalanceHistoryDTO 余额变化DTO
   */
  void asyncSaveUserBalanceHistory(BalanceChangeDTO userBalanceHistoryDTO,Long sysSeqNo,Long personSeqNo);

  /**
   * 用户储值流水列表
   * @param memberPurchaseQueryVo 用户储值流水vo
   * @return 用户储值流水
   */
  Page<UserBalanceHistoryDTO> list(UserBalanceHistoryQueryVo memberPurchaseQueryVo);

  /**
   * 为储值流水备注
   * @param ids 储值流水ids
   * @param remark 备注
   */
  void remark(List<Long> ids, String remark);

  /**
   * 导出
   * @param queryVo 订单导出vo
   * @return 导出id
   */
  Long export(UserBalanceHistoryQueryVo queryVo);
}
