package com.medusa.gruul.shop.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.shop.api.entity.ShopLogisticsAddress;
import com.medusa.gruul.shop.api.enums.AddressDefaultEnum;
import com.medusa.gruul.shop.api.enums.AddressTypeEnum;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import com.medusa.gruul.shop.service.model.dto.ShopLogisticsAddressDTO;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import com.medusa.gruul.shop.service.model.param.ShopLogisticsAddressParam;
import com.medusa.gruul.shop.service.mp.service.IShopLogisticsAddressService;
import com.medusa.gruul.shop.service.service.ShopLogisticsAddressManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * @author xiaoq
 * @Description 店铺物流地址管理 ServiceImpl
 * @date 2023-05-08 13:57
 */
@Service
@RequiredArgsConstructor
public class ShopLogisticsAddressManageServiceImpl implements ShopLogisticsAddressManageService {

    private final IShopLogisticsAddressService shopLogisticsAddressService;

    /**
     * 是否平台管理员
     *
     * @return true
     */
    private static boolean isSuperAdmin() {
        return ISecurity.matcher().any(SecureUser::getRoles, Roles.SUPER_ADMIN).match();
    }

    private static boolean isDefAddress(ShopLogisticsAddress shopLogisticsAddress) {
        AddressDefaultEnum defSend = shopLogisticsAddress.getDefSend();
        AddressDefaultEnum defReceive = shopLogisticsAddress.getDefReceive();
        return AddressDefaultEnum.YES.equals(defSend) || AddressDefaultEnum.YES.equals(defReceive);
    }

    /**
     * 设置或修改物流地址
     *
     * @param logisticsAddress 物流地址dto
     */
    @Override
    public void editAddress(ShopLogisticsAddressDTO logisticsAddress) {
        ShopLogisticsAddress shopLogisticsAddress = logisticsAddress.coverLogisticsAddress();
        //相同地址信息重复添加判断
        long sameAddressCount = countSameLogisticsAddress(shopLogisticsAddress);
        ShopError.SHOP_LOGISTICS_ADDRESS_EXISTED.trueThrow(sameAddressCount > CommonPool.NUMBER_ZERO);
        //当前店铺地址数量
        Long currentShopAddressCount = shopLogisticsAddressService.lambdaQuery()
                .eq(ShopLogisticsAddress::getDeleted, CommonPool.NUMBER_ZERO).count();
        boolean isSuperAdmin = isSuperAdmin();
        //商家端添加地址，地址类型必须勾选一个
        ShopError.SHOP_LOGISTICS_ADDRESS_VALID_PARAM_FAIL.trueThrow(validPlatFormParam(isSuperAdmin, shopLogisticsAddress));
        if (shopLogisticsAddress.getId() == null) {
            //新增
            addAddress(isSuperAdmin, currentShopAddressCount, shopLogisticsAddress);
        } else {
            //更新地址
            updateAddress(isSuperAdmin, currentShopAddressCount, shopLogisticsAddress);
        }
    }

    /**
     * 新增地址
     *
     * @param isSuperAdmin            是否超级管理员
     * @param currentShopAddressCount 当前地址计数
     * @param shopLogisticsAddress    地址数据
     */
    private void addAddress(boolean isSuperAdmin, Long currentShopAddressCount, ShopLogisticsAddress shopLogisticsAddress) {
        //处理当前地址数据默认地址类型和应用商家,新增地址时设置为默认收发货地址
        if (currentShopAddressCount == CommonPool.NUMBER_ZERO) {
            shopLogisticsAddress.setDefSend(AddressDefaultEnum.YES);
            shopLogisticsAddress.setDefReceive(AddressDefaultEnum.YES);
            //平台地址，第一条数据默认为自营商家
            shopLogisticsAddress.setDefSelfShop(
                    isSuperAdmin ? SelfShopEnum.YES : shopLogisticsAddress.getDefSelfShop()
            );
            //平台地址，第一条数据默认为自营供应商
            shopLogisticsAddress.setDefSelfSupplier(
                    isSuperAdmin ? SelfShopEnum.YES : shopLogisticsAddress.getDefSelfSupplier()
            );
        }
        //当前数据库已有地址信息情况
        if (currentShopAddressCount > CommonPool.NUMBER_ZERO) {
            updateDefAddressAndShopType(isSuperAdmin, shopLogisticsAddress);
        }
        //保存
        SystemCode.DATA_ADD_FAILED.falseThrow(shopLogisticsAddressService.save(shopLogisticsAddress));
    }

    /**
     * 新增、修改数据更新默认地址和应用商家类型
     *
     * @param isSuperAdmin         是否超级管理员
     * @param shopLogisticsAddress 地址信息
     */
    private void updateDefAddressAndShopType(boolean isSuperAdmin, ShopLogisticsAddress shopLogisticsAddress) {
        //非平台新增数据有默认地址情况下，更新原有数据的默认地址为NO
        if (!isSuperAdmin) {
            updateDefAddress(shopLogisticsAddress);
        }
        //平台下更新
        //逻辑设计有问题，暂时不校验，后期设计优化后再进行修改
    }

    /**
     * 非平台更新默认地址
     *
     * @param shopLogisticsAddress 地址入参
     */
    private void updateDefAddress(ShopLogisticsAddress shopLogisticsAddress) {
        Long id = shopLogisticsAddress.getId();
        AddressDefaultEnum defSend = shopLogisticsAddress.getDefSend();
        AddressDefaultEnum defReceive = shopLogisticsAddress.getDefReceive();
        if (isDefAddress(shopLogisticsAddress)) {
            shopLogisticsAddressService.lambdaUpdate()
                    .set(AddressDefaultEnum.YES.equals(defSend)
                            , ShopLogisticsAddress::getDefSend
                            , AddressDefaultEnum.NO)
                    .set(AddressDefaultEnum.YES.equals(defReceive)
                            , ShopLogisticsAddress::getDefReceive
                            , AddressDefaultEnum.NO)
                    .ne(id != null, ShopLogisticsAddress::getId, id)
                    .update();
        }
        //修改的时候，不允许本身默认变未默认
        if (id != null) {
            ShopLogisticsAddress address = shopLogisticsAddressService.lambdaQuery()
                    .eq(ShopLogisticsAddress::getId, id).one();
            shopLogisticsAddress.setDefSend(AddressDefaultEnum.YES.equals(address.getDefSend()) ? AddressDefaultEnum.YES : defSend);
            shopLogisticsAddress.setDefReceive(AddressDefaultEnum.YES.equals(address.getDefReceive()) ? AddressDefaultEnum.YES : defReceive);
        }
    }

    /**
     * 修改地址
     *
     * @param isSuperAdmin            是否超级管理员
     * @param currentShopAddressCount 当前地址计数
     * @param shopLogisticsAddress    地址数据
     */
    private void updateAddress(boolean isSuperAdmin, Long currentShopAddressCount, ShopLogisticsAddress shopLogisticsAddress) {
        if (currentShopAddressCount > CommonPool.NUMBER_ONE) {
            updateDefAddressAndShopType(isSuperAdmin, shopLogisticsAddress);
        } else {
            //只有一条数据默认地址类型和应用商品默认
            shopLogisticsAddress.setDefSend(AddressDefaultEnum.YES);
            shopLogisticsAddress.setDefReceive(AddressDefaultEnum.YES);
            if (isSuperAdmin) {
                shopLogisticsAddress.setDefSelfShop(SelfShopEnum.YES);
                shopLogisticsAddress.setDefSelfSupplier(SelfShopEnum.YES);
            }
        }
        //修改
        SystemCode.DATA_UPDATE_FAILED.falseThrow(shopLogisticsAddressService.updateById(shopLogisticsAddress));
    }

    /**
     * 校验参数
     *
     * @param shopLogisticsAddress 物流地址DTO
     * @param isSuperAdmin         是否超级管理员
     * @return false：校验通过 true：校验失败
     */
    private boolean validPlatFormParam(boolean isSuperAdmin, ShopLogisticsAddress shopLogisticsAddress) {
        if (!isSuperAdmin) {
            return false;
        }
        return shopLogisticsAddress.getDefSelfShop() == null && shopLogisticsAddress.getDefSelfSupplier() == null;
    }

    /**
     * 地址信息删除
     *
     * @param id 物流地址id
     */
    @Override
    public void delAddress(Long id) {
        //非管理员校验默认地址不允许删除
        if (!isSuperAdmin()) {
            ShopLogisticsAddress address = this.shopLogisticsAddressService
                    .lambdaQuery()
                    .select(ShopLogisticsAddress::getDefReceive, ShopLogisticsAddress::getDefSend)
                    .eq(ShopLogisticsAddress::getId, id)
                    .one();
            SystemCode.DATA_NOT_EXIST.trueThrow(address == null);
            ShopError.SHOP_DEFAULT_LOGISTICS_ADDRESS_NOT_DEL.trueThrow(isDefaultAddress(address));
        }
        boolean removeResult = this.shopLogisticsAddressService.removeById(id);
        SystemCode.DATA_DELETE_FAILED.falseThrow(removeResult);
    }


    /**
     * 获取店铺地址信息
     *
     * @param logisticsAddressParam 分页param
     */
    @Override
    public IPage<ShopLogisticsAddress> getAddressListByPage(ShopLogisticsAddressParam logisticsAddressParam) {
        return this.shopLogisticsAddressService.lambdaQuery().page(logisticsAddressParam);
    }


    /**
     * 获取物流地址信息By id
     *
     * @param id 物流地址id
     * @return 物流地址
     */
    @Override
    public ShopLogisticsAddress getAddressById(Long id) {
        ShopLogisticsAddress logisticsAddressInfo = shopLogisticsAddressService.getById(id);
        SystemCode.DATA_NOT_EXIST.trueThrow(logisticsAddressInfo == null);
        return logisticsAddressInfo;
    }

    /**
     * 设置默认地址信息
     *
     * @param id   地址id
     * @param type 收发货类型
     */
    @Override
    public void setDefAddress(Long id, AddressTypeEnum type) {
        if (type == AddressTypeEnum.DEF_SEND) {
            updateDefaultAddress(id, ShopLogisticsAddress::getDefSend);
        } else {
            updateDefaultAddress(id, ShopLogisticsAddress::getDefReceive);
        }
    }

    private void updateDefaultAddress(Long id, SFunction<ShopLogisticsAddress, ?> defaultGetter) {
        boolean updateResult =
                this.shopLogisticsAddressService.lambdaUpdate()
                        .eq(defaultGetter, AddressDefaultEnum.YES)
                        .set(defaultGetter, AddressDefaultEnum.NO)
                        .update()
                        &&
                        this.shopLogisticsAddressService.lambdaUpdate()
                                .eq(BaseEntity::getId, id)
                                .set(defaultGetter, AddressDefaultEnum.YES)
                                .update();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(updateResult);
    }


    private long countSameLogisticsAddress(ShopLogisticsAddress shopLogisticsAddress) {
        shopLogisticsAddress.setShopId(ISecurity.secureUser().getShopId());
        return this.shopLogisticsAddressService.lambdaQuery()
                .eq(ShopLogisticsAddress::getContactName, shopLogisticsAddress.getContactName())
                .eq(ShopLogisticsAddress::getArea, JSON.toJSONString(shopLogisticsAddress.getArea()))
                .eq(ShopLogisticsAddress::getAddress, shopLogisticsAddress.getAddress())
                .eq(ShopLogisticsAddress::getContactPhone, shopLogisticsAddress.getContactPhone())
                .eq(ShopLogisticsAddress::getShopId, shopLogisticsAddress.getShopId())
                .eq(ShopLogisticsAddress::getDeleted, CommonPool.NUMBER_ZERO)
                .ne(shopLogisticsAddress.getId() != null, ShopLogisticsAddress::getId, shopLogisticsAddress.getId())
                .count();
    }

    private boolean isDefaultAddress(ShopLogisticsAddress shopLogisticsAddress) {
        return AddressDefaultEnum.YES == shopLogisticsAddress.getDefReceive()
                || AddressDefaultEnum.YES == shopLogisticsAddress.getDefSend();
    }
}
