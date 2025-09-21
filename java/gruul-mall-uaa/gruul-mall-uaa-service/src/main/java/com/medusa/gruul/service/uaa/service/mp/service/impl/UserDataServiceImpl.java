package com.medusa.gruul.service.uaa.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.service.uaa.service.mp.entity.UserData;
import com.medusa.gruul.service.uaa.service.mp.mapper.UserDataMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IUserDataService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-03-08
 */
@Service
public class UserDataServiceImpl extends ServiceImpl<UserDataMapper, UserData> implements IUserDataService {

}
