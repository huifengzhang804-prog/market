package com.medusa.gruul.live.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.live.api.entity.LiveRoomGoods;
import com.medusa.gruul.live.service.model.dto.AddLiveGoodsDto;
import com.medusa.gruul.live.service.mp.mapper.LiveRoomGoodsMapper;
import com.medusa.gruul.live.service.mp.service.LiveRoomGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miskw
 * @date 2022/11/14
 */
@Service
public class LiveRoomGoodsServiceImpl extends ServiceImpl<LiveRoomGoodsMapper, LiveRoomGoods> implements LiveRoomGoodsService {
    @Resource
    private LiveRoomGoodsMapper liveRoomGoodsMapper;

    @Override
    public List<LiveRoomGoods> getRoomGoods(Long roomId) {
        LambdaQueryChainWrapper<LiveRoomGoods> liveRoomGoodsLambdaQueryChainWrapper = this.lambdaQuery();
        liveRoomGoodsLambdaQueryChainWrapper.eq(LiveRoomGoods::getLiveRoomId, roomId);
        return this.list(liveRoomGoodsLambdaQueryChainWrapper);
    }

    /**
     * @param addLiveGoodsDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoomGoods(AddLiveGoodsDto addLiveGoodsDto) {
        List<Long> goodsIds = addLiveGoodsDto.getGoodsIds();
        List<LiveRoomGoods> liveRoomGoodsList = new ArrayList<>();
        for (Long goodsId : goodsIds) {
            LiveRoomGoods liveRoomGoods = new LiveRoomGoods();
            liveRoomGoods.setLiveGoodsExamineId(goodsId)
                    .setLiveRoomId(addLiveGoodsDto.getRoomId());
            liveRoomGoodsList.add(liveRoomGoods);
        }
        boolean flag = this.saveBatch(liveRoomGoodsList);
        if (!flag) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "直播间添加商品入库失败!");
        }
    }
}
