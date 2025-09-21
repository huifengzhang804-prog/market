package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.mp.entity.MemberRights;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-09 15:20
 */
public interface MemberRightsMapper extends BaseMapper<MemberRights> {
    /**
     * 查询权益基础信息 by ids
     *
     * @param ids 权益ids
     * @return 权益基础信息
     */
    List<RelevancyRightsVO> queryRightByIds(@Param("ids") Set<Long> ids);

    /**
     * 查询会员权益是否在使用中
     * @param rightId
     * @return
     */
    Integer checkRightInUse(@Param("rightId") Long rightId);
}
