package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.service.mp.entity.UserTag;
import com.medusa.gruul.user.service.mp.mapper.UserTagMapper;
import com.medusa.gruul.user.service.mp.service.IUserTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * 会员标签 服务实现类
 *
 *
 * @author WuDi
 * @since 2022-09-14
 */
@Service
@RequiredArgsConstructor
public class UserTagServiceImpl extends ServiceImpl<UserTagMapper, UserTag> implements IUserTagService {


}
