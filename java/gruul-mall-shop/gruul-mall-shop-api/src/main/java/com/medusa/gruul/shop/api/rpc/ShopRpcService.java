package com.medusa.gruul.shop.api.rpc;

import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.model.vo.ShopAddressVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopLogisticsAddressVO;
import io.vavr.control.Option;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wudi
 */
public interface ShopRpcService {

    /**
     * 店铺地址和定位信息
     *
     * @param shopIds 店铺 id 集合
     * @return 店铺地址和定位信息
     */
    Map<Long, ShopAddressVO> shopAddress(Set<Long> shopIds);

    /**
     * 根据店铺id查询店铺基本信息
     *
     * @param shopId 店铺id
     * @return 店铺基本信息
     */
    ShopInfoVO getShopInfoByShopId(@NotNull Long shopId);


    /**
     * 根据店铺id集合批量获取店铺基本信息
     *
     * @param shopIds 店铺id集合
     * @return 店铺基本信息
     */
    List<ShopInfoVO> getShopInfoByShopIdList(@NotNull @Size(min = 1) Set<Long> shopIds);


    /**
     * 根据店铺id查询店铺与店铺银行账号信息
     *
     * @param shopId 店铺id
     * @return 店铺与其银行账号信息
     */
    Option<Shop> getShopAndShopBankInfo(@NotNull Long shopId);

    /**
     * 获取 默认的发货地址/退货地址
     *
     * @param shopId 店铺id
     * @param isSend 是否是收货地址
     * @return 发货地址/收货地址
     */
    ShopLogisticsAddressVO getSendOrReceiveAddress(@NotNull Long shopId, @NotNull Boolean isSend);

}
