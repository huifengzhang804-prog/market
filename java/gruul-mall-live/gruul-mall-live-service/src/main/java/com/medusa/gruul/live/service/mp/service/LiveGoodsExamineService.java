package com.medusa.gruul.live.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.enums.AuditStatus;
import com.medusa.gruul.live.service.model.dto.AddGoodsDto;
import com.medusa.gruul.live.service.model.dto.AddLiveGoodsDto;
import com.medusa.gruul.live.service.model.dto.GetGoodsDto;

import java.util.List;

/**
 * @author miskw
 * @date 2022/11/8
 */
public interface LiveGoodsExamineService extends IService<LiveGoodsExamine> {
    /**
     * 获取直播商品
     *
     * @param getGoodsDto
     * @return
     */
    IPage<LiveGoodsExamine> getGoods(GetGoodsDto getGoodsDto);


    /**
     * 根据微信商品GoodsId修改直播商品状态
     *
     * @param goodsId
     * @param auditStatus
     */
    void updateByGoodsStatus(Integer goodsId, AuditStatus auditStatus);

    /**
     * 修改直播商品
     * @param dto
     */
    void goodsUpdate(AddGoodsDto dto);

    /**
     * 获取所有审核中的直播商品goodsId
     * @return
     */
    List<Long> getUnderReview();

}
