package com.medusa.gruul.addon.bargain.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainSponsorDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainSponsorVO;
import com.medusa.gruul.addon.bargain.service.BargainConsumerService;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductSkuVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductVO;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author wudi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/consumer/bargain")
public class BargainConsumerController {


    private final BargainConsumerService bargainConsumerService;

    /**
     * 获取砍价商品sku详情和我的砍价信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     */
    @Log("获取砍价商品sku详情")
    @PreAuthorize("permitAll()")
    @GetMapping("/getBargainProductSku/{shopId}/{productId}")
    public Result<BargainSponsorProductVO> getBargainProductSku(@PathVariable Long shopId,
                                                                @PathVariable Long productId) {
        return Result.ok(
                bargainConsumerService.getBargainProductSku(shopId, productId, ISecurity.userOpt().map(SecureUser::getId).getOrNull())
        );
    }


    /**
     * 帮好友砍价
     *
     * @param bargainHelpPeople 砍价参数
     * @return 砍价结果
     */
    @Log("帮好友砍价")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    @PostMapping("/helpBargain")
    public Result<Long> helpBargain(@RequestBody @Valid BargainHelpPeopleDTO bargainHelpPeople) {
        return Result.ok(
                bargainConsumerService.helpBargain(bargainHelpPeople)
        );
    }


    /**
     * 发起砍价
     *
     * @param bargainSponsor 砍价参数
     * @return 砍价结果
     */

    @Log("发起砍价")
    @Idem(1000)
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    @PostMapping("/sponsor")
    public Result<BargainSponsorVO> bargainSponsor(@RequestBody @Valid BargainSponsorDTO bargainSponsor) {
        return Result.ok(
                bargainConsumerService.bargainSponsor(bargainSponsor)
        );
    }

    /**
     * 获取已发起砍价商品sku信息
     *
     * @param sponsorId  发起砍价id
     * @param activityId 活动id
     * @param shopId     店铺id
     * @param productId  商品id
     * @param skuId      skuId
     * @return 砍价结果
     */
    @Log("获取已发起砍价商品sku信息")
    @GetMapping("/getSponsorProductSku/{sponsorId}/{activityId}/{shopId}/{productId}/{skuId}/{bargainOrderId}")
    @PreAuthorize("permitAll()")
    public Result<BargainSponsorProductSkuVO> getBargainSponsorProductSku(@PathVariable("sponsorId") Long sponsorId,
                                                                          @PathVariable("activityId") Long activityId,
                                                                          @PathVariable("shopId") Long shopId,
                                                                          @PathVariable("productId") Long productId,
                                                                          @PathVariable("skuId") Long skuId,
                                                                          @PathVariable("bargainOrderId") Long bargainOrderId) {
        return Result.ok(
                bargainConsumerService.getBargainSponsorProductSku(sponsorId, activityId, shopId, productId, skuId,
                        bargainOrderId)
        );
    }

    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价好友列表
     */
    @Log("获取砍价好友列表")
    @GetMapping("/getBargainHelpPeopleList")
    @PreAuthorize("permitAll()")
    public Result<IPage<BargainHelpPeopleVO>> bargainHelpPeoplePage(
            @Valid BargainHelpPeopleQueryDTO bargainHelpPeopleQuery) {
        return Result.ok(
                bargainConsumerService.bargainHelpPeoplePage(bargainHelpPeopleQuery)
        );
    }


}
