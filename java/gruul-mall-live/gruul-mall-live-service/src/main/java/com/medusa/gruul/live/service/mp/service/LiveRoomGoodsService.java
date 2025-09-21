package com.medusa.gruul.live.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.live.api.entity.LiveRoomGoods;
import com.medusa.gruul.live.service.model.dto.AddLiveGoodsDto;

import java.util.List;

/**
 * @author miskw
 * @date 2022/11/14
 */
public interface LiveRoomGoodsService extends IService<LiveRoomGoods> {

    /**
     * 获取直播间商品Ids
     * @param roomId
     * @return
     */
    List<LiveRoomGoods> getRoomGoods(Long roomId);

    /**
     * 直播间商品，数据添加到数据库
     * @param addLiveGoodsDto
     */
    void addRoomGoods(AddLiveGoodsDto addLiveGoodsDto);
}
