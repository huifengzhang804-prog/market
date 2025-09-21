package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.service.mp.entity.UserMemberCard;
import com.medusa.gruul.user.service.mp.mapper.UserMemberCardMapper;
import com.medusa.gruul.user.service.mp.service.IUserMemberCardService;
import org.springframework.stereotype.Service;

/**
 * 用户会员卡服务实现层
 *
 * @author xiaoq
 * @Description UserMemberCardServiceImpl.java
 * @date 2022-11-17 10:40
 */
@Service
public class UserMemberCardServiceImpl extends ServiceImpl<UserMemberCardMapper, UserMemberCard> implements IUserMemberCardService {
}
