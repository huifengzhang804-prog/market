package com.medusa.gruul.carrier.pigeon.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShop;

/**
 * <p>
 * 店铺信息 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
public interface IMessageShopService extends IService<MessageShop> {

    /**
     * 用户分页查询 消息页  店铺列表
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<MessageShopVO> messageShopPage(MessageShopPageQueryDTO query);
}
