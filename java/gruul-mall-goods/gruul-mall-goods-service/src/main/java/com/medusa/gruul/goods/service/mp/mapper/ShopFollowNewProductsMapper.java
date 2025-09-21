package com.medusa.gruul.goods.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.ShopFollowNewProducts;
import com.medusa.gruul.goods.api.model.param.ApiShopFollowParam;
import com.medusa.gruul.goods.service.model.vo.MyShopFollowVO;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 浏览的上新商品 Mapper 接口
 *
 * @author WuDi
 * @since 2022-09-02
 */
public interface ShopFollowNewProductsMapper extends BaseMapper<ShopFollowNewProducts> {

    /**
     * 查询最近看过的店铺商品
     * @param shopFollowParam 店铺查询参数
     * @param userId 用户id
     * @return 店铺id集合
     */
    IPage<MyShopFollowVO> recentlyPage(@Param("shopFollowParam") ApiShopFollowParam shopFollowParam,
                                       @Param("userId") Long userId);
}
