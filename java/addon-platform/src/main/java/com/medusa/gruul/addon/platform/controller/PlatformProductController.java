package com.medusa.gruul.addon.platform.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.service.PlatformCategoryService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.param.PlatformCategoryParam;
import com.medusa.gruul.goods.api.model.param.PlatformProductParam;
import com.medusa.gruul.goods.api.model.vo.ApiPlatformProductVO;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.goods.api.model.vo.PlatformProductVO;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 平台商品控制器
 *
 * @Author: xiaoq
 * @Date : 2022-04-21 18:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/platform/product")
public class PlatformProductController {


    private final PlatformCategoryService categoryService;

    private final ShopRpcService shopRpcService;

    private final GoodsRpcService goodsRpcService;

    /**
     * 平台获取商品信息
     *
     * @param platformProductParam 查询param
     * @return 符合条件得商品信息
     */
    @GetMapping("/get/all")
    public Result<IPage<PlatformProductVO>> getPlatformProductInfo(PlatformProductParam platformProductParam) {

        Page<PlatformProductVO> platformProductVoPage = goodsRpcService.queryProductInfoByParam(platformProductParam);
        List<PlatformProductVO> platformProducts = platformProductVoPage.getRecords();
        if (CollUtil.isNotEmpty(platformProducts)) {
            Set<Long> shopIds = platformProducts.stream()
                    .map(PlatformProductVO::getShopId)
                    .collect(Collectors.toSet());
            /* 获取所属店铺名称
             */
            List<ShopInfoVO> shopInfoList = shopRpcService.getShopInfoByShopIdList(shopIds);
            Map<Long, String> shopInfoMap = shopInfoList.stream()
                    .collect(Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getName));
            platformProductVoPage.getRecords().forEach(bean -> {
                String shopName = shopInfoMap.get(bean.getShopId());
                bean.setShopName(shopName);
            });
        }
        return Result.ok(platformProductVoPage);
    }

    /**
     * 根据平台类目一级Id 获取所有商铺下的产品信息
     *
     * @param platformCategoryParam 平台类目参数
     */
    @Log("根据平台类目一级Id 获取所有商铺下的产品信息")
    @GetMapping("/by/category/list/")
    public Result<Page<ApiPlatformProductVO>> getProductInfoByPlatformCategoryId(PlatformCategoryParam platformCategoryParam) {
        List<Long> levelCategoryList = categoryService.getLevelCategoryList(platformCategoryParam.getPlatformCategoryId());
        if (CollUtil.isEmpty(levelCategoryList)) {
            return Result.ok(new Page<>());
        }
        return Result.ok(goodsRpcService.getProductInfoByPlatformCategoryId(levelCategoryList, platformCategoryParam));

    }


    @Log("平台获取审核商品")
    @GetMapping("get/audit/all")
    public Result<IPage<AuditProductVO>> getAuditProduct(AuditProductParam auditProductParam) {
        IPage<AuditProductVO> auditProductList = goodsRpcService.getAuditProductList(auditProductParam);
        if (CollUtil.isNotEmpty(auditProductList.getRecords())) {
            Set<Long> shopIds = auditProductList.getRecords().stream()
                    .map(AuditProductVO::getShopId)
                    .collect(Collectors.toSet());
            /* 获取所属店铺名称
             */
            List<ShopInfoVO> shopInfoList = shopRpcService.getShopInfoByShopIdList(shopIds);
            Map<Long, String> shopInfoMap = shopInfoList.stream()
                    .collect(Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getName));
            auditProductList.getRecords().forEach(bean -> {
                String shopName = shopInfoMap.get(bean.getShopId());
                bean.setShopName(shopName);
            });
        }
        return Result.ok(auditProductList);
    }

}
