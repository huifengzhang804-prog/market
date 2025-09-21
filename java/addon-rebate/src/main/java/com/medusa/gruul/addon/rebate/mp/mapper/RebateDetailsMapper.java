package com.medusa.gruul.addon.rebate.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.rebate.model.dto.RebateDetailsQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 返利明细表 Mapper 接口
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
public interface RebateDetailsMapper extends BaseMapper<RebateDetails> {


    /**
     * 统计收入和支出
     * @param rebateDetailsQuery 查询参数
     * @return 明细统计
     */
    RebateDetailsQueryDTO.RebateDetailStatistic rebateDetailsStatistic(@Param("rebateDetailsQuery") RebateDetailsQueryDTO rebateDetailsQuery);
}
