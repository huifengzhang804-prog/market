package com.medusa.gruul.user.service.service;

import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import com.medusa.gruul.user.service.model.dto.UserIntegralChangeDTO;

/**
 * @author shishuqian
 * date 2023/2/8
 * time 11:46
 **/
public interface UserIntegralService {

    /**
     * 根据用户id，查询用户剩余积分
     *
     * @param userId 用户id
     * @return 用户当前积分值
     */
    Long getIntegralTotalByUserId(Long userId);


    /**
     * 积分处理
     *
     * @param integralChangeDTO 积分变化dto
     *                          处理用户积分变化消息
     *                          加积分：增加用户积分，并生成明细
     *                          减积分：只生成明细数据
     */
    void handlerIntegralChangeMessage(IntegralChangeDTO integralChangeDTO);


    /**
     * 系统给用户 增加/减少 积分
     *
     * @param userIntegralChangeDTO 用户id、积分变化值、变化类型（增加、减少）
     * @return 是否成功
     */
    boolean changeIntegralBySystem(UserIntegralChangeDTO userIntegralChangeDTO);


    /**
     * 清除用户积分，通过系统时间
     */
    void clearUserIntegralBySystemTime();

}
