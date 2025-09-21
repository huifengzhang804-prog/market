package com.medusa.gruul.addon.bargain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainQueryDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainShopIdDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainDetailVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainInfoVO;

import com.medusa.gruul.addon.bargain.vo.BargainVo;
import java.util.List;
import java.util.Set;

/**
 * @author wudi
 */
public interface BargainManageService {

    /**
     * 添加砍价活动
     *
     * @param bargainActivity 砍价活动
     */
    void addBargain(BargainDTO bargainActivity);

    /**
     * 查询砍价活动详情
     *
     * @param shopId     店铺id
     * @param activityId 砍价活动id
     * @return 砍价活动详情
     */
    BargainDetailVO getBargainDetail(Long shopId, Long activityId);

    /**
     * 分页查询砍价活动
     *
     * @param bargainQuery 砍价活动查询条件
     * @return 砍价活动分页
     */
    IPage<BargainInfoVO> bargainInfoPage(BargainQueryDTO bargainQuery);

    /**
     * 批量删除砍价活动
     *
     * @param bargainShopId 砍价活动id、店铺id集合
     */
    void deleteBatchBargain(List<BargainShopIdDTO> bargainShopId);

    /**

     * 违规下架砍价活动
     * @param bargainVo

     */
    void sellOfBargain(BargainVo bargainVo);

    /**
     * 归还砍价活动库存
     *
     * @param shopId     店铺id
     * @param activityId 活动id
     * @param productIds 商品ids
     */

//    void returnStorageSku(Long shopId, Long activityId);

    /**
     * 店铺下架砍价活动
     * @param bargainVo
     */
    void shopSellOfBargain(BargainVo bargainVo);

    /**
     * 查询砍价活动下架的原因
     * @param bargainId
     * @return 违规下架的原因
     */
    String queryIllegalReason(Long bargainId);

    void returnStorageSku(Long shopId, Long activityId, Set<Long> productIds);

}
