package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.service.model.dto.IntegralDetailQueryDTO;
import com.medusa.gruul.user.service.mp.entity.UserIntegralDetail;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-02-01 17:44
 */
public interface UserIntegralDetailMapper extends BaseMapper<UserIntegralDetail> {
    /**
     *  detailPage
     * @param integralDetailQuery 积分明细查询
     * @return UserIntegralDetail
     */
    IPage<UserIntegralDetail> detailPage(@Param("integralDetailQuery") IntegralDetailQueryDTO integralDetailQuery);
}
