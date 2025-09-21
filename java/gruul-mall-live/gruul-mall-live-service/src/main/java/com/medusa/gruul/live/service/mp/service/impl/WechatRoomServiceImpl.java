package com.medusa.gruul.live.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.live.api.entity.WechatRoom;
import com.medusa.gruul.live.service.mp.mapper.WechatRoomMapper;
import com.medusa.gruul.live.service.mp.service.WechatRoomService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * @author miskw
 * @date 2022/11/8
 */
@Service
public class WechatRoomServiceImpl extends ServiceImpl<WechatRoomMapper, WechatRoom> implements WechatRoomService {
    @Resource
    private WechatRoomMapper wechatRoomMapper;

}
