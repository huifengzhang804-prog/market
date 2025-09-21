package com.medusa.gruul.addon.rebate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.rebate.model.dto.RebateDetailsQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;

public interface RebateDetailsHandlerService {


    /**
     * 分页查询返利明细
     * @param rebateDetailsQuery 查询参数
     * @return 返利明细
     */
    IPage<RebateDetails> rebateDetailsPage(RebateDetailsQueryDTO rebateDetailsQuery);
}
