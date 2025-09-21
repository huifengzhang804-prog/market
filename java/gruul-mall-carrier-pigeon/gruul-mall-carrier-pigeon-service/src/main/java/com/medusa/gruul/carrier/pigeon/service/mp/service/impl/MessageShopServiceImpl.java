package com.medusa.gruul.carrier.pigeon.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShop;
import com.medusa.gruul.carrier.pigeon.service.mp.mapper.MessageShopMapper;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageShopService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 店铺信息 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
@Service
public class MessageShopServiceImpl extends ServiceImpl<MessageShopMapper, MessageShop> implements IMessageShopService {

    @Override
    public IPage<MessageShopVO> messageShopPage(MessageShopPageQueryDTO query) {
        return baseMapper.messageShopPage(query);
    }
}
