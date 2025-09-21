package com.medusa.gruul.addon.live.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.live.mp.entity.Anchor;
import com.medusa.gruul.addon.live.mp.mapper.AnchorMapper;
import com.medusa.gruul.addon.live.mp.service.AnchorService;
import com.medusa.gruul.addon.live.param.AnchorParam;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Service
public class AnchorServiceImpl extends ServiceImpl<AnchorMapper, Anchor> implements AnchorService {
    @Override
    public IPage<AnchorVO> anchorPage(AnchorParam anchorParam) {
        return baseMapper.anchorPage(anchorParam);
    }

    @Override
    public AnchorVO anchorMessage(Long userId) {
        return baseMapper.anchorMessage(userId);
    }
}
