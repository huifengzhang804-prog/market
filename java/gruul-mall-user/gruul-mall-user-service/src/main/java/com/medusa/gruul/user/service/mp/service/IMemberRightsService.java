package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.mp.entity.MemberRights;

import java.util.List;
import java.util.Set;

/**
 * 服务层
 *
 * @author xiaoq
 * @Description IMemberRightsService.java
 * @date 2022-11-09 15:18
 */
public interface IMemberRightsService extends IService<MemberRights> {
    /**
     * 获取基础权益
     *
     * @param ids 权益ids
     * @return List<RelevancyRightsVO>
     */
    List<RelevancyRightsVO> getRightByIds(Set<Long> ids);

    /**
     * 查询会员权益是否正在使用中
     * @param rightId
     * @return
     */
    Boolean checkRightInUse(Long rightId);


}
