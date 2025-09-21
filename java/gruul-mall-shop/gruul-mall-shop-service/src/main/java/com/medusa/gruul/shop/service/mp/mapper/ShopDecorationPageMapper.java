package com.medusa.gruul.shop.service.mp.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationPage;
import org.apache.ibatis.annotations.Param;


/**
 * 店铺装修页面Mapper接口
 */
public interface ShopDecorationPageMapper extends BaseMapper<ShopDecorationPage> {

    /**
     * 返回当前页面被模版引用的数量
     *
     * @param shopId 店铺ID
     * @param id     页面ID
     * @return 被模版引用的数量
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer referencedCount(@Param("shopId") Long shopId, @Param("id") Long id);
}
