package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.service.model.dto.ShopUserQueryDTO;
import com.medusa.gruul.user.service.model.vo.ShopUserVO;
import com.medusa.gruul.user.service.mp.entity.ShopUserAccount;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author WuDi
 * @since 2023-05-17
 */
public interface ShopUserAccountMapper extends BaseMapper<ShopUserAccount> {

    /**
     * 分页查询店铺客户列表
     * @param shopUserQuery 店铺客户查询参数
     * @return 店铺客户列表
     */
    IPage<ShopUserVO> getShopUserAccountList(@Param("query") ShopUserQueryDTO shopUserQuery);

    /**
     * 获取店铺客户详情
     *
     * @param shopId 店铺id
     * @param userId 店铺客户id
     * @return 店铺客户详情
     */
    ShopUserVO getShopUserAccountDetail(@Param("shopId") Long shopId,
                                        @Param("userId") Long userId);
}
