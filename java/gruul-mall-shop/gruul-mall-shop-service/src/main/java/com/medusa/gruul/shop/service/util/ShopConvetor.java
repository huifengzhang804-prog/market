package com.medusa.gruul.shop.service.util;

import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jipeng
 * @since 2024/9/5
 */
@Mapper
public interface ShopConvetor {

    ShopConvetor INSTANCE = Mappers.getMapper(ShopConvetor.class);
//    @Mappings(
//            @Mapping(source = "id", target = "id" )
//    )
    ShopVO toShopVo(Shop shop);
}
