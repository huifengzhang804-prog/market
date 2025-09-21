package com.medusa.gruul.service.uaa.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.mp.mapper.ShopUserDataMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理员(平台/店铺)资料查询表 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-04-27
 */
@Service
public class ShopUserDataServiceImpl extends ServiceImpl<ShopUserDataMapper, ShopUserData> implements IShopUserDataService {

    @Override
    public IPage<ShopUserDataVO> shopUserDataPage(ShopCustomAdminPageDTO shopCustomAdminPage) {
        return baseMapper.shopUserDataPage(shopCustomAdminPage);
    }

    @Override
    public ShopUserDataVO shopUserDataById(Long dataId) {
        Set<Integer> clientRoleValues = Arrays.stream(Roles.values())
                .filter(role -> role.getClientType() == ISystem.clientTypeMust())
                .map(Roles::getValue).collect(Collectors.toSet());
        return baseMapper.shopUserDataById(dataId,clientRoleValues);
    }
}
