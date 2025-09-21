package com.medusa.gruul.shop.service.util;

import com.medusa.gruul.shop.service.model.vo.ShopRegisterInfoVO;
import com.medusa.gruul.shop.service.mp.entity.ShopRegisterInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author jipeng
 * @since 2024/9/5
 */
@Mapper
public interface ShopRegisterInfoConvert {
  ShopRegisterInfoConvert INSTANCE = Mappers.getMapper(ShopRegisterInfoConvert.class);

   ShopRegisterInfoVO toShopRegisterInfoVo(ShopRegisterInfo shopRegisterInfo);


}
