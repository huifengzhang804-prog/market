package com.medusa.gruul.carrier.pigeon.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;

/**
 * @author 张治保
 * date 2022/10/10
 */
public interface MessageQueryService {

    /**
     * 分页查询消息用户列表
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    IPage<MessageUserVO> messageUserPage(MessageUserPageQueryDTO query);


    /**
     * 分页查询聊天记录
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    IPage<Message> messagePage(MessagePageQueryDTO query);

}
