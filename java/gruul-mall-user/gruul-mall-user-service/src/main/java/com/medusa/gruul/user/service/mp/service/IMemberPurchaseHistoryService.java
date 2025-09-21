package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.service.model.dto.MemberPurchaseQueryDTO;
import com.medusa.gruul.user.service.mp.entity.MemberPurchaseHistory;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.mp.service
 * @author:jipeng
 * @createTime:2024/1/17 11:34
 * @version:1.0
 */
public interface IMemberPurchaseHistoryService extends IService<MemberPurchaseHistory> {

  /**
   * 列表查询
   */
  Page<MemberPurchaseHistory> queryList(MemberPurchaseQueryDTO queryDTO);
}
