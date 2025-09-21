package com.medusa.gruul.carrier.pigeon.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageAndShopAdminVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.mp.mapper.MessageMapper;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 店铺用户聊天消息 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Override
    public IPage<MessageAndShopAdminVO> messagePage(MessagePageQueryDTO query) {
        return baseMapper.messagePage(query);
    }

    /**
     *
     * 移动端商家端后台获取用户未读消息数量
     * @param adminId  当前发起查询的管理员userId
     * @param shopId 店铺id
     * @param userId 用户id
     * @return 未读消息数量
     */
    @Override
    public Integer getShopUnRead(Long adminId, Long shopId, Long userId) {
        return baseMapper.getShopUnRead(adminId,shopId,userId);
    }
}
