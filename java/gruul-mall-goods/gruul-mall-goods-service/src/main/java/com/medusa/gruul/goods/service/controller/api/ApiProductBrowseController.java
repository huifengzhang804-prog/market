package com.medusa.gruul.goods.service.controller.api;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.model.dto.ProductBrowseDTO;
import com.medusa.gruul.goods.service.mp.service.IShopFollowNewProductsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 浏览商品
 *
 * @author : WuDi
 * @date : 2022/9/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/product/browse")
public class ApiProductBrowseController {

    private final IShopFollowNewProductsService shopFollowNewProductsService;

    /**
     * 浏览商品记录
     *
     * @param productBrowseDTO 浏览商品DTO
     */
    @PostMapping
    @Idem
    @Log("浏览商品记录")
    public Result<Void> productBrowse(@RequestBody @Valid ProductBrowseDTO productBrowseDTO) {
        shopFollowNewProductsService.productBrowse(productBrowseDTO);
        return Result.ok();

    }


}
