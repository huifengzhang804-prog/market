package com.medusa.gruul.live.service.task;

import cn.binarywang.wx.miniapp.api.WxMaLiveGoodsService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveGoodsServiceImpl;
import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.live.api.enums.AuditStatus;
import com.medusa.gruul.live.api.enums.WechatErrorCode;
import com.medusa.gruul.live.service.mp.service.LiveGoodsExamineService;
import com.xxl.job.core.handler.IJobHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miskw
 * date 2022/11/15
 * Describe 定时更新直播商品审核状态
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LiveProductReviewJob extends IJobHandler {

    private final WxMaService wxMaService;
    private final LiveGoodsExamineService liveGoodsExamineService;


    /**
     * 每十分钟获取直播商品审核状态
     */
    @Override
    public void execute() {
        //获取所有审核中的直播商品GoodsId
        List<Long> goodsIds = liveGoodsExamineService.getUnderReview();
        if (CollUtil.isNotEmpty(goodsIds)) {
            log.debug("时间:{},定时更新直播商品审核状态,{}个,正在查询中", DateUtil.now(), goodsIds.size());
            WxMaLiveGoodsService wxMaLiveGoodsService = new WxMaLiveGoodsServiceImpl(wxMaService);
            List<Integer> collect = goodsIds.stream().map(Long::intValue).collect(Collectors.toList());
            WxMaLiveResult goodsWareHouse;
            try {
                goodsWareHouse = wxMaLiveGoodsService.getGoodsWareHouse(collect);
            } catch (WxErrorException e) {
                if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                    throw new GlobalException(WechatErrorCode.getDescribe(e.getError().getErrorCode()));
                }
                throw new GlobalException(e.getMessage());
            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
            }
            List<WxMaLiveResult.Goods> goods = goodsWareHouse.getGoods();
            int sucCount = 0;
            for (WxMaLiveResult.Goods good : goods) {
                Integer auditStatus = good.getAuditStatus();
                int code = AuditStatus.UNDER_REVIEW.getCode();
                if (auditStatus != code) {
                    Integer goodsId = good.getGoodsId();
                    AuditStatus status = AuditStatus.getByCode(auditStatus);
                    liveGoodsExamineService.updateByGoodsStatus(goodsId, status);
                    sucCount += 1;
                }
            }
            log.debug("时间:{},定时更新直播商品审核状态,一共完成{}个", DateUtil.now(), sucCount);

        }

    }


}
