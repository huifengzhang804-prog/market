package com.medusa.gruul.cart.service.controller;

import com.medusa.gruul.cart.api.dto.ShopIdSkuIdsDTO;
import com.medusa.gruul.cart.service.model.dto.ProductSkuDTO;
import com.medusa.gruul.cart.service.model.vo.CartVO;
import com.medusa.gruul.cart.service.service.CartQueryService;
import com.medusa.gruul.cart.service.service.CartService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 * @author 张治保
 * date 2022/5/16
 */
@RestController
@Validated
@RequestMapping("/cart")
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
@RequiredArgsConstructor
public class CartController {

    private final CartQueryService cartQueryService;
    private final CartService cartService;

    /**
     * 查询购物车数据
     *
     * @return 购物车详情
     */
    @GetMapping
    @Log("查询购物车数据")
    public Result<CartVO> myCart(Long shopId) {
        return Result.ok(
                cartQueryService.myCart(shopId)
        );
    }

    /**
     * 添加购物车
     *
     * @param productSku 商品SKU
     */
    @PostMapping
    @Idem
    @Log("添加购物车")
    public Result<Void> addCart(@RequestBody @Valid ProductSkuDTO productSku) {
        cartService.addCart(productSku);
        return Result.ok();
    }

    /**
     * 修改购物车商品SKU
     *
     * @param uniqueId   购物车规格唯一id
     * @param productSku 商品SKU
     */
    @Idem(value = 200)
    @PutMapping("/{uniqueId}")
    @Log("修改购物车商品SKU")
    public Result<Void> editCartProductSku(@PathVariable String uniqueId, @RequestBody @Valid ProductSkuDTO productSku) {
        cartService.editCartProductSku(uniqueId, productSku);
        return Result.ok();
    }

    /**
     * 删除购物车部分商品
     *
     * @param shopProductSkus 店铺商品SKU
     * @param shopId          店铺id
     */
    @Log("删除购物车部分商品")
    @Idem
    @PutMapping
    public Result<Void> deleteCartProduct(Long shopId,
                                          @RequestBody @Valid @Size(min = 1) List<ShopIdSkuIdsDTO> shopProductSkus
    ) {
        cartService.deleteCartProduct(ISecurity.userMust().getId(), shopId, shopProductSkus);
        return Result.ok();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    @Idem
    @Log("清空购物车")
    public Result<Void> clear(Long shopId) {
        cartService.clear(shopId);
        return Result.ok();
    }

    /**
     * 清空失效商品
     */
    @Log("清空失效商品")
    @Idem
    @DeleteMapping("/invalid")
    public Result<Void> clearInvalid(Long shopId) {
        cartService.clearInvalid(shopId);
        return Result.ok();
    }

    /**
     * 统计购物车商品
     */
    @Log("统计购物车商品")
    @GetMapping("/count")
    public Result<Integer> cartCount(Long shopId) {
        return Result.ok(
                cartQueryService.cartCount(shopId)
        );
    }


    /**
     * 计算购物车总金额
     *
     * @param shopId 店铺id
     * @return 购物车金额
     */
    @Log("计算购物车总金额")
    @GetMapping("/money")
    public Result<Long> cartTotalMoney(@NotNull Long shopId) {
        return Result.ok(cartQueryService.cartTotalMoney(shopId));
    }
}
