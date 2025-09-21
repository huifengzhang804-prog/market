package com.medusa.gruul.overview.service.mp.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewUser;
import com.medusa.gruul.overview.service.mp.base.mapper.OverviewUserMapper;
import com.medusa.gruul.overview.service.mp.base.service.IOverviewUserService;
import org.springframework.stereotype.Service;

/**
 * 用户提现信息服务实现层
 *
 * @author xiaoq
 * @Description OverviewUserServiceImpl.java
 * @date 2023-09-18 17:07
 */
@Service
public class OverviewUserServiceImpl extends ServiceImpl<OverviewUserMapper, OverviewUser> implements IOverviewUserService {
}
