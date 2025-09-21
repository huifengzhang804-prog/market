package com.medusa.gruul.addon.bargain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainSponsorDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainSponsorVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductSkuVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductVO;

/**
 * @author wudi
 */
public interface BargainConsumerService {


    /**
     * 获取砍价商品sku详情和我的砍价信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 砍价商品sku详情
     */
    BargainSponsorProductVO getBargainProductSku(Long shopId, Long productId, Long userId);


    /**
     * 发起砍价
     *
     * @param bargainSponsor 砍价参数
     * @return 砍价结果
     */
    BargainSponsorVO bargainSponsor(BargainSponsorDTO bargainSponsor);

    /**
     * 帮好友砍价
     *
     * @param bargainHelpPeople 砍价参数
     * @return 砍价结果
     */
    Long helpBargain(BargainHelpPeopleDTO bargainHelpPeople);

    /**
     * 获取砍价商品sku详情和我的砍价信息
     *
     * @param sponsorId      发起砍价id
     * @param activityId     活动id
     * @param shopId         店铺id
     * @param productId      商品id
     * @param skuId          skuId
     * @param bargainOrderId 订单id
     * @return 砍价商品sku详情
     */
    BargainSponsorProductSkuVO getBargainSponsorProductSku(Long sponsorId,
                                                           Long activityId,
                                                           Long shopId,
                                                           Long productId,
                                                           Long skuId,
                                                           Long bargainOrderId);

    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价帮助列表
     */
    IPage<BargainHelpPeopleVO> bargainHelpPeoplePage(BargainHelpPeopleQueryDTO bargainHelpPeopleQuery);
}
