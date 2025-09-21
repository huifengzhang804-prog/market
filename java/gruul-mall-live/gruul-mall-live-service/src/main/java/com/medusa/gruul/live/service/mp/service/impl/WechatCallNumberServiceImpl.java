package com.medusa.gruul.live.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.live.api.entity.WechatCallNumber;
import com.medusa.gruul.live.service.mp.mapper.WechatCallNumberMapper;
import com.medusa.gruul.live.service.mp.service.WechatCallNumberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * @author miskw
 * @date 2022/11/14
 */
@Service
public class WechatCallNumberServiceImpl extends ServiceImpl<WechatCallNumberMapper, WechatCallNumber> implements WechatCallNumberService {
    @Resource
    private WechatCallNumberMapper wechatCallNumberMapper;

}
