package com.medusa.gruul.user.service.service;

import com.medusa.gruul.user.api.model.UserTagVo;
import com.medusa.gruul.user.service.model.dto.UserTagDTO;

import java.util.List;

/**
 * @author: WuDi
 * @date: 2022/9/20
 */
public interface UserTagManageService {

    /**
     * 查询会员所有标签
     *
     * @param shopId 店铺id
     * @param bound  是否只查询被用户绑定的标签
     * @return 会员标签集合
     */
    List<UserTagVo> getUserTagList(Boolean bound);

    /**
     * 设置会员标签
     *
     * @param userTag 会员标签
     */
    void updateUserTag(UserTagDTO userTag);
}
