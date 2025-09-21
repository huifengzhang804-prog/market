package com.medusa.gruul.user.service.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.service.mp.entity.UserIntegralDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/7/21
 * @describe 积分明细
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserIntegralDetailVO extends Page<UserIntegralDetail> {
    /**
     * 积分统计
     */
    private StatementStatisticsVO statistics;
    public static UserIntegralDetailVO toUserIntegralDetailVo(IPage<UserIntegralDetail> userIntegralDetailPage){
        UserIntegralDetailVO userIntegralDetail = new UserIntegralDetailVO();
        userIntegralDetail.setCurrent(userIntegralDetailPage.getCurrent());
        userIntegralDetail.setRecords(userIntegralDetailPage.getRecords());
        userIntegralDetail.setSize(userIntegralDetailPage.getSize());
        userIntegralDetail.setTotal(userIntegralDetailPage.getTotal());
        userIntegralDetail.setPages(userIntegralDetailPage.getPages());
        return userIntegralDetail;
    }
}
