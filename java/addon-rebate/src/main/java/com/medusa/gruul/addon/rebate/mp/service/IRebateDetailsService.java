package com.medusa.gruul.addon.rebate.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.rebate.model.dto.RebateDetailsQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;

/**
 * <p>
 * 返利明细表 服务类
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
public interface IRebateDetailsService extends IService<RebateDetails> {


    /**
     * 统计收入和支出
     * @param rebateDetailsQuery 查询参数
     * @return 明细统计
     */
    RebateDetailsQueryDTO.RebateDetailStatistic rebateDetailsStatistic(RebateDetailsQueryDTO rebateDetailsQuery);
}
