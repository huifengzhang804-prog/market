package com.medusa.gruul.carrier.pigeon.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShop;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 店铺信息 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
public interface MessageShopMapper extends BaseMapper<MessageShop> {

    /**
     * 用户分页查询 消息页  店铺列表
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<MessageShopVO> messageShopPage(@Param("query") MessageShopPageQueryDTO query);
}
