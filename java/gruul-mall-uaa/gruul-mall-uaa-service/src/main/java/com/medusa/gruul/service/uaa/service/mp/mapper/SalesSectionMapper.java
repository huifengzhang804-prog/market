package com.medusa.gruul.service.uaa.service.mp.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.GiveAwayResultVO;
import com.medusa.gruul.service.uaa.service.model.vo.ProductInfoVO;
import com.medusa.gruul.service.uaa.service.model.vo.PurchaseMethodResultVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserPointVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalesSectionMapper {
    IPage<ProductInfoVO> getProductInfoList(SalesSectionPageDTO dto);
    List<PurchaseMethodResultVO> getPurchaseMethodResultList( Long salesSectionId);
    List<GiveAwayResultVO> getGiveAwayResultList(Long salesSectionId);
    String getSectionNameBySectionId(Long salesSectionId);
    List<UserPointVO> getUserPointsInfo(Long userId);

}
