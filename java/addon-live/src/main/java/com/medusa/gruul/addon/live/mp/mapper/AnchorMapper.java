package com.medusa.gruul.addon.live.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.live.mp.entity.Anchor;
import com.medusa.gruul.addon.live.param.AnchorParam;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface AnchorMapper extends BaseMapper<Anchor> {
    IPage<AnchorVO> anchorPage(@Param("anchorParam") AnchorParam anchorParam);

    AnchorVO anchorMessage(@Param("userId") Long userId);
}
