package com.medusa.gruul.service.uaa.service.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionNoPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.*;
import com.medusa.gruul.service.uaa.service.mp.mapper.SalesSectionMapper;
import com.medusa.gruul.service.uaa.service.service.SalesSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收货地址服务
 *
 * @author 张治保
 * date 2022/5/31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesSectionServiceImpl implements SalesSectionService {

    @Autowired
    private SalesSectionMapper mapper;


    @Override
    public IPage<ProductVO> getSalesSectionList(SalesSectionPageDTO dto) {
        // 1. 分页查询商品基础信息
        IPage<ProductInfoVO> productInfoListPage = mapper.getProductInfoList(dto);
        List<ProductInfoVO> productInfoList = productInfoListPage.getRecords();


        String sectionName=mapper.getSectionNameBySectionId(dto.getSalesSectionId());

        List<ProductVO> result = new ArrayList<>();

        //获取当前分区 购买方式基础信息
        List<PurchaseMethodResultVO> purchaseMethodResultVOS=mapper.getPurchaseMethodResultList(dto.getSalesSectionId());
        Map<Integer, List<PurchaseMethodResultVO> > methodGroupMap=new HashMap<>();
        for (PurchaseMethodResultVO purchaseMethodResultVO:purchaseMethodResultVOS) {
            int groupId=purchaseMethodResultVO.getGroupId();
            if(methodGroupMap.containsKey(groupId)){
                methodGroupMap.get(groupId).add(purchaseMethodResultVO);
            }else{
                List<PurchaseMethodResultVO> list=new ArrayList<>();
                list.add(purchaseMethodResultVO);
                methodGroupMap.put(groupId,list);
            }
        }
        log.info("methodGroupMapSize:{}",methodGroupMap.size());

        //获取当前分区 赠送积分方式信息
        List<GiveAwayResultVO> giveAwayResultList=mapper.getGiveAwayResultList(dto.getSalesSectionId());



        for(ProductInfoVO productInfoVO:productInfoList){
            ProductVO vo=new ProductVO();

            /**
             *  结合当前分区的购买方式，计算各个商品 所应支付的各项积分
             *  注意，一个分区可能有多种购买方式组合
             */
            List<PurchaseMethodGroupVO> purchaseMethodGroupList =new ArrayList<>();
            List<GiveAwayListVO> giveAwayListVOS =new ArrayList<>();

            for (Map.Entry<Integer, List<PurchaseMethodResultVO>> entry : methodGroupMap.entrySet()) {
                PurchaseMethodGroupVO  purchaseMethodGroupVO=new PurchaseMethodGroupVO();
                purchaseMethodGroupVO.setGroupId(entry.getKey());
                List<PurchaseMethodVO> list=new ArrayList<>();
                for(PurchaseMethodResultVO purchaseMethodResultVO:entry.getValue()){
                    PurchaseMethodVO  purchaseMethodVO=new PurchaseMethodVO();
                    BeanUtils.copyProperties(purchaseMethodResultVO,purchaseMethodVO);
                    purchaseMethodVO.setPrice(productInfoVO.getSalePrice()*purchaseMethodResultVO.getPriceRatio()/100);
                    list.add(purchaseMethodVO);
                }
                purchaseMethodGroupVO.setPurchaseMethodGroupList(list);
                purchaseMethodGroupList.add(purchaseMethodGroupVO);
            }

            vo.setPurchaseMethodList(purchaseMethodGroupList);

            //结合当前分区的赠送方式，计算当前商品需要赠送的积分
            for(GiveAwayResultVO giveAwayResultVO:giveAwayResultList){
                GiveAwayListVO giveAwayListVO=new GiveAwayListVO();
                BeanUtils.copyProperties(giveAwayResultVO,giveAwayListVO);
                giveAwayListVO.setCount(productInfoVO.getSalePrice()*giveAwayResultVO.getPriceRatio()/100);
                giveAwayListVOS.add(giveAwayListVO);
            }

            vo.setGiveAwayList(giveAwayListVOS);
            vo.setPresaleState("0");
            vo.setOther("0");
            vo.setProductId(productInfoVO.getProductId());
            vo.setPicUrl(productInfoVO.getPicUrl());
            vo.setProductName(productInfoVO.getProductName());
            vo.setShopId(productInfoVO.getShopId());

            result.add(vo);
        };



        Page<ProductVO> resultPage = new Page<>();
        resultPage.setRecords(result);
        resultPage.setTotal(productInfoListPage.getTotal());
        resultPage.setCurrent(productInfoListPage.getCurrent());
        resultPage.setSize(productInfoListPage.getSize());


        return resultPage;
    }

    @Override
    public List<UserPointVO> getUserPointsInfo(Long userId) {
        return mapper.getUserPointsInfo(userId);
    }

    @Override
    public String getSectionName(SalesSectionNoPageDTO dto) {
        return mapper.getSectionNameBySectionId(dto.getSalesSectionId());
    }
}
