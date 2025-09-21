package com.medusa.gruul.carrier.pigeon.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageAndShopAdminVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 店铺用户聊天消息 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
public interface MessageMapper extends BaseMapper<Message> {


    /**
     * 分页查询聊天记录
     *
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    IPage<MessageAndShopAdminVO> messagePage(@Param("query") MessagePageQueryDTO query);

    /**
     *
     * 移动端商家端后台获取用户未读消息数量
     * @param adminId  当前发起查询的管理员userId
     * @param shopId 店铺id
     * @param userId 用户id
     * @return 未读消息数量
     */
    Integer getShopUnRead(@Param("adminId") Long adminId, @Param("shopId") Long shopId,@Param("userId") Long userId);

}
