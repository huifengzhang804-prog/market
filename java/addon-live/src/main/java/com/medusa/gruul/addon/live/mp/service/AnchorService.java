package com.medusa.gruul.addon.live.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.live.mp.entity.Anchor;
import com.medusa.gruul.addon.live.param.AnchorParam;
import com.medusa.gruul.addon.live.vo.AnchorVO;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface AnchorService extends IService<Anchor> {
    IPage<AnchorVO> anchorPage(AnchorParam anchorParam);

    AnchorVO anchorMessage(Long userId);
}
