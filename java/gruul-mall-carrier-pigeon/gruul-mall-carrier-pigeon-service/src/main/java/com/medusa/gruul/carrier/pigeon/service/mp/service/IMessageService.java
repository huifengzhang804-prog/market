package com.medusa.gruul.carrier.pigeon.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageAndShopAdminVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;

/**
 * <p>
 * 店铺用户聊天消息 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
public interface IMessageService extends IService<Message> {

    /**
     * 分页查询聊天记录
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     * @deprecated 分页结果有BUG 分开两次查询 不在使用这个方法
     */
    IPage<MessageAndShopAdminVO> messagePage(MessagePageQueryDTO query);


    /**
     *
     * 移动端商家端后台获取用户未读消息数量
     * @param adminId  当前发起查询的管理员userId
     * @param shopId 店铺id
     * @param userId 用户id
     * @return 未读消息数量
     */
    Integer getShopUnRead(Long adminId, Long shopId, Long userId);
}
