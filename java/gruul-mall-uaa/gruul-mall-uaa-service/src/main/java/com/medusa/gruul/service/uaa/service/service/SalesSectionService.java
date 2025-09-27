package com.medusa.gruul.service.uaa.service.service;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionNoPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.account.Account;
import com.medusa.gruul.service.uaa.service.model.dto.account.AccountDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ProductVO;
import com.medusa.gruul.service.uaa.service.model.vo.SalesSectionListVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserPointVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;

import java.util.List;

/**
 * @author 张治保
 * date 2022/5/31
 */
public interface SalesSectionService {


    IPage<ProductVO> getSalesSectionList(SalesSectionPageDTO dto);
    List<UserPointVO> getUserPointsInfo(Long userId);
   String getSectionName(SalesSectionNoPageDTO dto);



}
