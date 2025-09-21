package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.service.model.dto.IntegralDetailQueryDTO;
import com.medusa.gruul.user.service.model.vo.UserIntegralDetailVO;
import com.medusa.gruul.user.service.mp.entity.UserIntegralDetail;

/**
 * @author shishuqian
 * date 2023/2/8
 * time 13:28
 **/
public interface IUserIntegralDetailService extends IService<UserIntegralDetail> {

    /**
     *  分页获取用户积分详情
     * @param integralDetailQuery 用户积分明细查询
     * @return 用户积分详情分页列表
     */
    UserIntegralDetailVO detailPage(IntegralDetailQueryDTO integralDetailQuery);
}
