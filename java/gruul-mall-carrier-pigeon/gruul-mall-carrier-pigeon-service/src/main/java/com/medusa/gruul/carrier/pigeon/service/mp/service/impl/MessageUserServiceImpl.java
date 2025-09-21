package com.medusa.gruul.carrier.pigeon.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageUser;
import com.medusa.gruul.carrier.pigeon.service.mp.mapper.MessageUserMapper;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
@Service
public class MessageUserServiceImpl extends ServiceImpl<MessageUserMapper, MessageUser> implements IMessageUserService {

    @Override
    public IPage<MessageUserVO> messageUserPage(Long adminId, MessageUserPageQueryDTO query) {
        return baseMapper.messageUserPage(adminId, query);
    }
}
