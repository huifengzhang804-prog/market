package com.medusa.gruul.addon.rebate.service.imipl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.rebate.model.dto.RebateDetailsQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;
import com.medusa.gruul.addon.rebate.mp.service.IRebateDetailsService;
import com.medusa.gruul.addon.rebate.service.RebateDetailsHandlerService;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RebateDetailsHandlerServiceImpl implements RebateDetailsHandlerService {

    private final IRebateDetailsService rebateDetailsService;


    /**
     * 分页查询返利明细
     *
     * @param rebateDetailsQuery 查询参数
     * @return 返利明细
     */
    @Override
    public IPage<RebateDetails> rebateDetailsPage(RebateDetailsQueryDTO rebateDetailsQuery) {
        RebateDetailsQueryDTO page = rebateDetailsService.lambdaQuery()
                .ge(rebateDetailsQuery.getStartTime() != null, RebateDetails::getCreateTime, rebateDetailsQuery.getStartTime())
                .le(rebateDetailsQuery.getEndTime() != null, RebateDetails::getCreateTime, rebateDetailsQuery.getEndTime())
                .eq(RebateDetails::getUserId, rebateDetailsQuery.getUserId())
                .orderByDesc(BaseEntity::getCreateTime)
                .page(rebateDetailsQuery);
        return page.setRebateDetailStatistic(
                rebateDetailsService.rebateDetailsStatistic(rebateDetailsQuery)
        );
    }
}
