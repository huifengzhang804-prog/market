package com.medusa.gruul.user.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.service.model.dto.ShopUserQueryDTO;
import com.medusa.gruul.user.service.model.vo.ShopUserVO;
import com.medusa.gruul.user.service.mp.entity.ShopUserAccount;
import com.medusa.gruul.user.service.mp.mapper.ShopUserAccountMapper;
import com.medusa.gruul.user.service.mp.service.IShopUserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author WuDi
 * @since 2023-05-17
 */
@Service
public class ShopUserAccountServiceImpl extends ServiceImpl<ShopUserAccountMapper, ShopUserAccount> implements IShopUserAccountService {

    @Override
    public IPage<ShopUserVO> getShopUserAccountList(ShopUserQueryDTO shopUserQuery) {
        return baseMapper.getShopUserAccountList(shopUserQuery);
    }

    @Override
    public ShopUserVO getShopUserAccountDetail(Long shopId, Long userId) {
        return baseMapper.getShopUserAccountDetail(shopId,userId);
    }
}
