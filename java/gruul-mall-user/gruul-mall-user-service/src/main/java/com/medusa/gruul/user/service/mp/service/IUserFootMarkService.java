package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.service.mp.entity.UserFootMark;

import java.util.List;
import java.util.Set;

/**
 * 
 * 用户足迹 服务类
 * 
 *
 * @author 
 * @since 2022-07-29
 */
public interface IUserFootMarkService extends IService<UserFootMark> {

    /**
     * 获取店铺浏览量
     * @param shopIds 店铺id数组
     * @return 分组店铺，浏览量
     */
    List<UserFootMarkVO> getFootMarkCount(Set<Long> shopIds);
}
