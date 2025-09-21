package com.medusa.gruul.goods.service.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.ConsignmentSetting;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.dto.PaveGoodsDTO;
import com.medusa.gruul.goods.api.model.dto.SinglePaveGoodsDTO;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierIssueProductListVO;
import com.medusa.gruul.goods.service.service.ConsignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 代销管理
 *
 * @author xiaoq
 * @since 2023/8/8
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/consignment")
public class ConsignmentController {
    private final ConsignmentService consignmentService;


    /**
     * 代销设置修改
     *
     * @param consignmentPriceSetting 代销设置参数
     */
    @Log("代销设置修改")
    @PostMapping("/config")
    public Result<Void> consignmentConfig(@RequestBody ConsignmentPriceSettingDTO consignmentPriceSetting) {
        Long shopId = ISecurity.userMust().getShopId();
        consignmentService.consignmentConfig(shopId, consignmentPriceSetting);
        return Result.ok();
    }


    /**
     * 查询代销设置
     *
     * @return 代销设置
     */
    @Log("查询代销设置")
    @GetMapping("/config")
    public Result<ConsignmentSetting> config() {
        Long shopId = ISecurity.userMust().getShopId();
        return Result.ok(
                consignmentService.config(shopId)
        );
    }

    /**
     * 批量一键铺货
     *
     * @param paveGoods 商品信息
     */
    @Log("批量一键铺货")
    @PostMapping("/pave/goods")
    public Result<Void> paveGoods(@RequestBody @Validated PaveGoodsDTO paveGoods) {
        Long shopId = ISecurity.userMust().getShopId();
        consignmentService.paveGoods(paveGoods, shopId);
        return Result.ok();
    }

    /**
     * 已铺货代销商品
     *
     * @param purchaseProductParam 查询参数
     */
    @Log("已铺货代销商品")
    @GetMapping("/pave/goods")
    public Result<IPage<SupplierIssueProductListVO>> getPaveGoods(PurchaseProductParam purchaseProductParam) {
        return Result.ok(consignmentService.getPaveGoods(purchaseProductParam));
    }

    /**
     * 代销商品上架
     *
     * @param productId 商品id集合
     */
    @Log("代销商品上架")
    @PutMapping("/product/update/status/{productId}")
    public Result<Void> productUpdateStatus(@PathVariable Long productId) {
        consignmentService.consignmentProductUpdateStatus(productId);
        return Result.ok();
    }

    /**
     * 单个一键铺货
     *
     * @param singlePaveGoods 单个一键铺货参数
     */
    @Log("单个一键铺货")
    @PostMapping("/single/pave/goods")
    public Result<Void> singlePaveGoods(@RequestBody @Valid SinglePaveGoodsDTO singlePaveGoods) {
        consignmentService.singlePaveGoods(singlePaveGoods);
        return Result.ok();
    }

}
