package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ShopFollow;
import com.medusa.gruul.goods.api.model.dto.ShopFollowDTO;
import com.medusa.gruul.goods.api.model.enums.MyShopFollowStatus;
import com.medusa.gruul.goods.api.model.param.ApiShopFollowParam;
import com.medusa.gruul.goods.api.model.vo.ApiFollowShopProductVO;
import com.medusa.gruul.goods.service.model.vo.MyShopFollowVO;
import com.medusa.gruul.goods.service.model.vo.ShopFollowVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;

/**
 * 店铺关注 服务类
 *
 * @author WuDi
 * @since 2022-08-03
 */
public interface IShopFollowService extends IService<ShopFollow> {

    /**
     * 关注/取消关注店铺
     *
     * @param shopFollowDTO 店铺关注DTO
     */
    void follow(ShopFollowDTO shopFollowDTO);

    /**
     * 判断用户是否关注当前店铺
     *
     * @param shopId 店铺id
     * @return Boolean
     */
    Boolean isFollow(Long shopId);

    /**
     * 获取关注的店铺
     *
     * @param shopFollowParam 店铺查询参数
     * @return 关注的店铺
     */
    IPage<ShopFollowVO> shopInfo(ApiShopFollowParam shopFollowParam);

    /**
     * 我的关注的店铺
     *
     * @param shopFollowParam    店铺查询参数
     * @param myShopFollowStatus 我的关注枚举
     * @return 我的关注
     */
    IPage<MyShopFollowVO> myFollow(ApiShopFollowParam shopFollowParam, MyShopFollowStatus myShopFollowStatus);

    /**
     * 首页我的关注全部店铺
     *
     * @param shopFollowParam 店铺查询参数
     * @return 我的关注
     */
    IPage<MyShopFollowVO> homePageMyFollow(ApiShopFollowParam shopFollowParam);

    /**
     * pc端-关注店铺
     *
     * @param shopFollowParam 店铺参数
     * @return 我的关注
     */
    IPage<ApiFollowShopProductVO> shopFollowProducts(ApiShopFollowParam shopFollowParam);

    /**
     * 更新关注店铺信息
     *
     * @param shopInfoVO 店铺信息
     */
    void updateShopFollowInfo(ShopInfoVO shopInfoVO);

    /**
     * 获取店铺关注人数
     *
     * @param shopId 店铺id
     * @return 关注人数
     */
    Long followCount(Long shopId);
}
