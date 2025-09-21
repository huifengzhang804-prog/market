package com.medusa.gruul.addon.live.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;
import org.apache.ibatis.annotations.Param;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 描述
 */
public interface ProhibitedMapper extends BaseMapper<Prohibited> {
    Prohibited banReason(@Param("sourceId") Long sourceId, @Param("type") ProhibitedType type);
}
