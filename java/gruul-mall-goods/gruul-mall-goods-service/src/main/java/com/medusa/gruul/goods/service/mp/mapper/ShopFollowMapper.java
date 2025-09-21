package com.medusa.gruul.goods.service.mp.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.ShopFollow;
import com.medusa.gruul.goods.api.model.param.ApiShopFollowParam;
import com.medusa.gruul.goods.service.model.vo.MyShopFollowVO;
import com.medusa.gruul.goods.service.model.vo.ShopFollowVO;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 店铺关注 Mapper 接口
 *
 * @author WuDi
 * @since 2022-08-03
 */
public interface ShopFollowMapper extends BaseMapper<ShopFollow> {

    /**
     * 获取关注的店铺
     * @param shopFollowParam 店铺查询参数
     * @return 关注的店铺
     */
    @InterceptorIgnore(tenantLine = "true")
    IPage<ShopFollowVO> pageShopFollow(@Param("shopFollowParam") ApiShopFollowParam shopFollowParam);

    /**
     * 获取关注的店铺
     * @param  shopFollowParam 店铺查询参数
     * @param  userId 用户id
     * @return 关注的店铺
     */
    IPage<MyShopFollowVO> getShopFollowPage(@Param("shopFollowParam") ApiShopFollowParam shopFollowParam,
                                               @Param("userId") Long userId);


    /**
     * 获取上新的商品
     *
     * @param shopFollowParam 店铺查询参数
     * @param userId 用户id
     * @return 有上新的店铺
     */
    IPage<MyShopFollowVO> getHasNewProducts(@Param("shopFollowParam") ApiShopFollowParam shopFollowParam,
                                            @Param("userId") Long userId);

    /**
     * 首页我的关注全部店铺
     * @param   shopFollowParam 店铺查询参数
     * @return 首页我的关注全部店铺
     */
    IPage<MyShopFollowVO> homePageMyFollow(@Param("shopFollowParam") ApiShopFollowParam shopFollowParam);
}
