package com.medusa.gruul.shop.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 *  商家端装修模板Mapper接口  
 * @author An.Yan
 */
public interface ShopDecorationTemplateMapper extends BaseMapper<ShopDecorationTemplate> {

    /**
     * 获取与{@code shopModes}匹配的所有店铺已经启用的装修模板集合
     * @param shopModes 店铺运营模式集合,参考{@link ShopMode}
     * @return {@link ShopDecorationTemplate}
     */
    List<ShopDecorationTemplate> queryEnabledTemplatesOfAllShop(@Param("shopModes") List<ShopMode> shopModes,
                                                                @Param("businessType") TemplateBusinessTypeEnum templateBusinessTypeEnum);
}
