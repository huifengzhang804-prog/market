package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.service.mp.entity.UserDealDetail;
import com.medusa.gruul.user.service.mp.mapper.UserDealDetailMapper;
import com.medusa.gruul.user.service.mp.service.IUserDealDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-28 16:31
 */
@Service
@RequiredArgsConstructor
public class UserDealDetailServiceImpl extends ServiceImpl<UserDealDetailMapper, UserDealDetail> implements IUserDealDetailService {
}
