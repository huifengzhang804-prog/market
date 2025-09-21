package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.user.api.model.UserCollectVO;
import com.medusa.gruul.user.service.mp.entity.UserCollect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 用户收藏 Mapper 接口
 * 
 *
 * @author
 * @since 2022-08-01
 */
public interface UserCollectMapper extends BaseMapper<UserCollect> {

    /**
     * 查看店铺的商品收藏量
     * @param  shopId 店铺id
     * @return 商品和收藏量Map
     */
   List<UserCollectVO> getShopProductCollection(@Param("shopId") Long shopId);
}
