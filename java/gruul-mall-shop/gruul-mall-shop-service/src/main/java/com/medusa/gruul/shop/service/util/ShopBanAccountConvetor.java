package com.medusa.gruul.shop.service.util;

import com.medusa.gruul.shop.api.entity.ShopBankAccount;
import com.medusa.gruul.shop.service.model.vo.ShopBankAccountVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jipeng
 * @since 2024/9/5
 */
@Mapper
public interface ShopBanAccountConvetor {
    ShopBanAccountConvetor INSTANCE = Mappers.getMapper(ShopBanAccountConvetor.class);

    ShopBankAccountVO toShopBankAccountVO(ShopBankAccount shopBankAccount);

}
