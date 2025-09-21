package com.medusa.gruul.addon.rebate.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.rebate.model.dto.RebateDetailsQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;
import com.medusa.gruul.addon.rebate.mp.mapper.RebateDetailsMapper;
import com.medusa.gruul.addon.rebate.mp.service.IRebateDetailsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 返利明细表 服务实现类
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Service
public class RebateDetailsServiceImpl extends ServiceImpl<RebateDetailsMapper, RebateDetails> implements IRebateDetailsService {


    /**
     * 统计收入和支出
     * @param rebateDetailsQuery 查询参数
     * @return 明细统计
     */
    @Override
    public RebateDetailsQueryDTO.RebateDetailStatistic rebateDetailsStatistic(RebateDetailsQueryDTO rebateDetailsQuery) {
        return baseMapper.rebateDetailsStatistic(rebateDetailsQuery);
    }
}
