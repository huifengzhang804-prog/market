package com.medusa.gruul.addon.platform.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import org.apache.ibatis.annotations.Param;

public interface DecorationPageMapper extends BaseMapper<DecorationPage> {

    /**
     * 查询页面被引用次数
     *
     * @param pageId 页面ID
     * @return 被引用次数
     */
    Integer referencedCount(@Param("pageId") Long pageId);
}
