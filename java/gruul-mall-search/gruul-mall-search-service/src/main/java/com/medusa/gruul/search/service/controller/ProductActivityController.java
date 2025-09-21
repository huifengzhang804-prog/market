package com.medusa.gruul.search.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.search.api.model.ProductActivityVO;
import com.medusa.gruul.search.service.service.ProductActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品活动控制器
 *
 * @author 张治保 date 2023/3/23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/search/product")
public class ProductActivityController {


    private final ProductActivityService productActivityService;

    /**
     * 查询商品最近的一场活动
     */
    @Log("查询商品最近的一场活动")
    @GetMapping("/activity/{shopId}/{productId}")
    public Result<ProductActivityVO> getRecentActivity(@PathVariable Long shopId, @PathVariable Long productId) {
        return Result.ok(
                productActivityService.getRecentActivity(shopId, productId)
        );
    }
}
