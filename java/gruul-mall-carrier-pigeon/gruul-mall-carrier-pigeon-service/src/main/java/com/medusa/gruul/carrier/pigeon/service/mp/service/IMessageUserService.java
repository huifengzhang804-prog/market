package com.medusa.gruul.carrier.pigeon.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageUser;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
public interface IMessageUserService extends IService<MessageUser> {

    /**
     * 分页查询消息用户列表
     * @param adminId 当前发起查询的管理员id
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    IPage<MessageUserVO> messageUserPage(Long adminId,MessageUserPageQueryDTO query);

}
