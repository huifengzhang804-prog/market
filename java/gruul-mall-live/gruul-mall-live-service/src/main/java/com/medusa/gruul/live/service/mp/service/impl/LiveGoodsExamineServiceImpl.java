package com.medusa.gruul.live.service.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.enums.AuditStatus;
import com.medusa.gruul.live.service.model.dto.AddGoodsDto;
import com.medusa.gruul.live.service.model.dto.GetGoodsDto;
import com.medusa.gruul.live.service.mp.mapper.LiveGoodsExamineMapper;
import com.medusa.gruul.live.service.mp.service.LiveGoodsExamineService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miskw
 * @date 2022/11/8
 */
@Service
public class LiveGoodsExamineServiceImpl extends ServiceImpl<LiveGoodsExamineMapper, LiveGoodsExamine> implements LiveGoodsExamineService {
    @Resource
    private LiveGoodsExamineMapper liveGoodsExamineMapper;

    @Override
    public IPage<LiveGoodsExamine> getGoods(GetGoodsDto getGoodsDto) {
        Long shopId = ISecurity.userMust().getShopId();
        IPage<LiveGoodsExamine> page = new Page<>(getGoodsDto.getCurrent(), getGoodsDto.getSize());
        LambdaQueryChainWrapper<LiveGoodsExamine> lambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(this.baseMapper);
        //判断价格是否是最小值
        Boolean leftFlag = checkMinPrice(getGoodsDto.getSalePrice());
        //判断价格是否是最大值
        Boolean rightFlag = checkMaxPrice(getGoodsDto.getSalePrice());
        //判断价格是否两边都有值
        Boolean sectionFlag = checkSectionPrice(getGoodsDto.getSalePrice());
        long minPrice = 0L;
        long maxPrice = 0L;
        if (leftFlag || sectionFlag) {
            minPrice = Long.parseLong(getGoodsDto.getSalePrice().split(StrUtil.UNDERLINE)[0]);
        }
        if (rightFlag || sectionFlag) {
            maxPrice = Long.parseLong(getGoodsDto.getSalePrice().split(StrUtil.UNDERLINE)[1]);
        }

        lambdaQueryChainWrapper
                .eq(LiveGoodsExamine::getShopId, shopId)
                .eq(getGoodsDto.getAuditStatus() != null, LiveGoodsExamine::getAuditStatus, getGoodsDto.getAuditStatus())
                .ge((leftFlag || sectionFlag), LiveGoodsExamine::getPrice, minPrice)
                .le((rightFlag || sectionFlag), LiveGoodsExamine::getPrice, maxPrice)
                .like(StrUtil.isNotBlank(getGoodsDto.getProductName()), LiveGoodsExamine::getProductName, getGoodsDto.getProductName())
                .orderByDesc(LiveGoodsExamine::getCreateTime);

        lambdaQueryChainWrapper.page(page);
        return page;
    }

    /**
     * 判断价格是否两边都有值
     *
     * @param salePrice
     * @return
     */
    private Boolean checkSectionPrice(String salePrice) {
        if (StrUtil.isBlank(salePrice)) {
            return Boolean.FALSE;
        }
        List<String> collect = Arrays.stream(salePrice.split(StrUtil.UNDERLINE)).filter(item -> !item.equals(StrUtil.EMPTY)).collect(Collectors.toList());
        int length = collect.size();
        if (length != CommonPool.NUMBER_TWO) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 判断价格是否是最大值
     *
     * @param salePrice
     * @return
     */
    private Boolean checkMaxPrice(String salePrice) {
        if (StrUtil.isBlank(salePrice)) {
            return Boolean.FALSE;
        }
        List<String> collect = Arrays.stream(salePrice.split(StrUtil.UNDERLINE)).filter(item -> !item.equals(StrUtil.EMPTY)).collect(Collectors.toList());
        int length = collect.size();
        if (length == CommonPool.NUMBER_TWO) {
            return Boolean.FALSE;
        }

        if (salePrice.endsWith(StrUtil.UNDERLINE)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 判断价格是否是最小值
     *
     * @param salePrice
     * @return
     */
    private Boolean checkMinPrice(String salePrice) {
        if (StrUtil.isBlank(salePrice)) {
            return Boolean.FALSE;
        }
        List<String> collect = Arrays.stream(salePrice.split(StrUtil.UNDERLINE)).filter(item -> !item.equals(StrUtil.EMPTY)).collect(Collectors.toList());
        int length = collect.size();
        if (length == CommonPool.NUMBER_TWO) {
            return Boolean.FALSE;
        }
        if (salePrice.startsWith(StrUtil.UNDERLINE)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 根据微信商品GoodsId修改直播商品状态
     *
     * @param goodsId
     * @param auditStatus
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByGoodsStatus(Integer goodsId, AuditStatus auditStatus) {
        LambdaUpdateChainWrapper<LiveGoodsExamine> lambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(this.baseMapper);
        boolean update = lambdaUpdateChainWrapper
                .eq(LiveGoodsExamine::getGoodsId, Long.valueOf(goodsId))
                .set(LiveGoodsExamine::getAuditStatus, auditStatus)
                .set(auditStatus.getCode() == AuditStatus.UNDER_REVIEW.getCode(), LiveGoodsExamine::getVerifyTime, LocalDateTime.now())
                .update();
        if (!update) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "直播商品状态修改失败!");
        }
    }

    /**
     * 修改直播商品
     *
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void goodsUpdate(AddGoodsDto dto) {
        //微信商品Id
        Long goodsId = dto.getGoodsId();
        LiveGoodsExamine goodsExamine = new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(LiveGoodsExamine::getGoodsId, goodsId).one();
        if (goodsExamine == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "查找不到对应的直播商品!");
        }
        BeanUtil.copyProperties(dto, goodsExamine);
        goodsExamine.setProductName(dto.getProductName());
        boolean flag = this.updateById(goodsExamine);
        if (!flag) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "同步修改直播商品失败！");
        }
    }

    /**
     * 获取所有审核中的直播商品goodsId
     *
     * @return
     */
    @Override
    public List<Long> getUnderReview() {
        AuditStatus underReview = AuditStatus.UNDER_REVIEW;
        List<LiveGoodsExamine> list = this.lambdaQuery().eq(LiveGoodsExamine::getAuditStatus, underReview).list();
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(LiveGoodsExamine::getGoodsId).collect(Collectors.toList());
    }
}
