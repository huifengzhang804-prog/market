package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.service.mp.entity.UserFootMark;
import com.medusa.gruul.user.service.mp.mapper.UserFootMarkMapper;
import com.medusa.gruul.user.service.mp.service.IUserFootMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 
 * 用户足迹 服务实现类
 * 
 *
 * @author
 * @since 2022-07-29
 */
@Service
@RequiredArgsConstructor
public class UserFootMarkServiceImpl extends ServiceImpl<UserFootMarkMapper, UserFootMark> implements IUserFootMarkService {

    /**
     * 获取店铺浏览量
     *
     * @param shopIds 店铺id数组
     * @return 分组店铺，浏览量
     */
    @Override
    public List<UserFootMarkVO> getFootMarkCount(Set<Long> shopIds) {
        return this.baseMapper.getFootMarkCount(shopIds);
    }
}
