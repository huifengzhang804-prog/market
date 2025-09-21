package com.medusa.gruul.addon.matching.treasure.controller;


import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealDetailVO;
import com.medusa.gruul.addon.matching.treasure.service.SetMealConsumerService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 套餐用户端
 * @author wudi
 */
@RestController
@RequestMapping("/consumer")
@RequiredArgsConstructor
public class SetMealConsumerController {

    private final SetMealConsumerService setMealConsumerService;

    /**
     * 商品详情页套餐基本信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 套餐基本信息
     */
    @Log("商品详情页套餐基本信息")
    @GetMapping("/setMealBasicInfo/{shopId}/{productId}")
    @PreAuthorize("permitAll()")
    public Result<List<SetMealBasicInfoVO>> getSetMealBasicInfo(@PathVariable Long shopId,
                                                                @PathVariable Long productId) {
        return Result.ok(
                setMealConsumerService.getSetMealBasicInfo(shopId, productId)
        );
    }

    /**
     * 套餐详情
     *
     * @param setMealId 套餐id
     * @return 套餐详情
     */
    @Log("套餐详情")
    @GetMapping("/setMealDetail/{shopId}/{setMealId}")
    @PreAuthorize("permitAll()")
    public Result<SetMealDetailVO> setMealDetail(@PathVariable Long shopId,
                                                 @PathVariable Long setMealId) {
        return Result.ok(
                setMealConsumerService.setMealDetail(shopId, setMealId)
        );
    }


}
