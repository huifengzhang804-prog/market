package com.medusa.gruul.user.service.model.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import com.medusa.gruul.user.service.mp.entity.UserIntegralDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 用户积分明细查询
 *
 * @author miskw
 * @date 2023/7/21
 * @describe 用户积分明细查询
 */
@Getter
@Setter
@ToString
public class IntegralDetailQueryDTO extends Page<UserIntegralDetail>{
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 获取积分类型
     */
    private GainIntegralType gainIntegralType;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginTime;
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

}
