package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.service.model.dto.UserFootMarkQueryDTO;
import com.medusa.gruul.user.service.mp.entity.UserFootMark;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 用户足迹 Mapper 接口
 *
 * @author Wudi
 * @since 2022-07-29
 */
public interface UserFootMarkMapper extends BaseMapper<UserFootMark> {

    /**
     * 获取用户足迹
     *
     * @param userFootMarkQueryDTO 用户查询条件
     * @return 返回结果
     */
    IPage<UserFootMarkVO> userFootMarkPage(@Param("userFootMarkQueryDTO") UserFootMarkQueryDTO userFootMarkQueryDTO);
    

    /**
     * 分组查询最新的3个平台三级类目id
     *
     * @param userId 用户id
     * @return 平台三级类目id集合
     */
    List<UserFootMark> selectThreePlatformCategoryId(@Param("userId") Long userId);

    /**
     * 获取店铺浏览量
     *
     * @param shopIds 店铺id数组
     * @return 分组店铺，浏览量
     */
    List<UserFootMarkVO> getFootMarkCount(@Param("shopIds") Set<Long> shopIds);
}
