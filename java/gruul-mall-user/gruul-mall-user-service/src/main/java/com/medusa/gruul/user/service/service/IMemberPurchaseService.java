package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.service.model.dto.MemberPurchaseQueryDTO;
import com.medusa.gruul.user.service.model.vo.MemberPurchaseHistoryVo;

/**
 * @description: 会员流水
 * @projectName:gruul-mall-user
 * @author:jipeng
 * @date 2024/1/17 15:16
 * @version:1.0
 */
public interface IMemberPurchaseService {

  /**
   * 会员流水列表查询
   * @param memberPurchaseQuery 会员流水
   * @return Page 会员结果
   */
  Page<MemberPurchaseHistoryVo> list(MemberPurchaseQueryDTO memberPurchaseQuery);

  /**
   * 数据导出
   * @param query 导出vo
   * @return 导出id
   */
  Long export(MemberPurchaseQueryDTO query);
}
