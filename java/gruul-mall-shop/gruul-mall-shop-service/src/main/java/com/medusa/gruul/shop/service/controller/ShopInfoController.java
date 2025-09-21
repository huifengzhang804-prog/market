package com.medusa.gruul.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.model.OrderEvaluateCountDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.model.dto.ShopInfoDTO;
import com.medusa.gruul.shop.api.model.vo.O2OShopQueryVo;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.shop.service.model.dto.ShopDetailDTO;
import com.medusa.gruul.shop.service.model.dto.ShopDetailVO;
import com.medusa.gruul.shop.service.model.dto.ShopSearchParamDTO;
import com.medusa.gruul.shop.service.model.vo.ShopSearchVO;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import com.medusa.gruul.shop.service.service.ShopInfoService;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 店铺基本信息控制器
 *
 * @author xiaoq Description date 2022-05-26 11:26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/info")
public class ShopInfoController {

    private final IShopService shopService;

    private final ShopInfoService shopInfoService;

    private final ShopRpcService shopRpcService;

    private final OrderRpcService orderRpcService;


    /**
     * 获取店铺设置信息
     *
     * @return 店铺信息
     */
    @Log("获取店铺设置信息")
    @PreAuthorize("""
                    @S.matcher()
                    .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                    .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'mall:general:setting'))
                    .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).eq(@S.PERMS,'mall:general:setting')).match()
            """)
    @GetMapping
    public Result<Shop> getShopInfo() {

        Shop shop = shopService.getById(ISecurity.userMust().getShopId());
        if (shop != null) {
            Map<Long, OrderEvaluateCountDTO> orderEvaluateCount = orderRpcService.getOrderEvaluateCount(
                    Collections.singleton(shop.getId()));
            shop.setScore(Option.of(orderEvaluateCount.get(shop.getId())).map(OrderEvaluateCountDTO::score)
                    .getOrElse(OrderEvaluateCountDTO::defaultScore));

        }
        return Result.ok(shop);
    }


    /**
     * 获取店铺审核状态信息
     *
     * @return 店铺审核状态信息
     */
    @Log("获取店铺状态/根据用户")
    @GetMapping("audit/info/{shopModel}")
//    @PreAuthorize("@S.USER")
    public Result getShopAuditInfo(@PathVariable(value = "shopModel") ShopMode shopMode) {
        return Result.ok(shopInfoService.getShopAuditInfo(ISecurity.userMust().getId(), shopMode));
    }


    /**
     * 店铺设置信息修改
     *
     * @param shopInfo 店铺信息
     */
    @Idem
    @Log("店铺设置信息修改")
    @PostMapping("update")
    @PreAuthorize("""
                    @S.matcher()
                    .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                    .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'mall:general:setting'))
                    .match()
            """)
    public Result<Void> updateShopInfo(@RequestBody ShopInfoDTO shopInfo) {
        shopInfoService.updateShopInfo(shopInfo);
        return Result.ok();
    }

    /**
     * 获取店铺基础信息
     *
     * @param shopId 店铺id
     * @return 店铺基本信息
     */
    @Log("获取店铺基础信息")
    @GetMapping("/base/{shopId}")
    public Result<ShopInfoVO> getShopBaseInfo(@PathVariable Long shopId) {
        ShopInfoVO shopInfoByShopId = shopRpcService.getShopInfoByShopId(shopId);
        return Result.ok(
                shopInfoByShopId
        );
    }

    /**
     * 获取店铺关注信息
     *
     * @param shopId 店铺id
     * @return 店铺关注信息
     */
    @Log("获取店铺关注信息")
    @GetMapping("/follow/{shopId}")
    public Result<ShopInfoVO> getShopInfoFollow(@PathVariable Long shopId) {
        ShopInfoVO shopInfoByShopId = shopInfoService.getShopInfoFollow(shopId);
        return Result.ok(
                shopInfoByShopId
        );
    }

    /**
     * 店铺信息聚合接口 查询店铺信息 (暂时用于 C端查询[商家首页，商品详情])
     *
     * @param param 查询参数
     * @return 店铺信息
     */
    @PostMapping("/detail")
    public Result<ShopDetailVO> shopDetail(@RequestBody @Valid ShopDetailDTO param) {
        //用户id 如果为空表示未登录
        Long userId = ISecurity.userOpt().map(SecureUser::getId).getOrNull();
        return Result.ok(
                shopInfoService.shopDetail(userId, param)
        );
    }

    /**
     * 获取店铺基础信息
     *
     * @param shopId 店铺id
     * @return 店铺基本信息
     */
    @Log("获取店铺基础信息")
    @GetMapping("/{shopId}")
    public Result<Shop> getShopInfo(@PathVariable Long shopId) {
        return Result.ok(
                shopInfoService.getShopById(shopId).getOrElseThrow(() -> new GlobalException("当前店铺不存在"))
        );
    }


    /**
     * 根据店铺ids 获取店铺数据
     *
     * @param shopIds 店铺ids
     * @return List<店铺信息>
     */
    @Log("批量获取店铺信息")
    @PostMapping("/batch")
    public Result<List<Shop>> getShopList(@RequestBody @Size(min = 1) Set<Long> shopIds) {
        return Result.ok(shopInfoService.getBatchShop(shopIds));
    }


    /**
     * C端店铺搜索
     *
     * @param param 搜索条件
     * @return 查询店铺结果
     */
    @Log("C端店铺搜索")
    @PostMapping("/search/shop")
    public Result<IPage<ShopSearchVO>> searchShop(@RequestBody ShopSearchParamDTO param) {
        return Result.ok(shopInfoService.searchShop(param));
    }


    /**
     * 根据销量查询店铺
     *
     * @param sortAsc 是否升序
     * @return 查询店铺结果
     */
    @Log("根据销量查询店铺")
    @GetMapping("/search/shop/sales")
    public Result<List<ShopInfoVO>> searchShopBySales(@RequestParam Boolean sortAsc) {
        return Result.ok(
                shopInfoService.searchShopBySales(sortAsc)
        );
    }


    /**
     * 根据距离查询店铺
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param sortAsc   是否升序
     * @return 查询店铺结果
     */
    @Log("根据距离查询店铺")
    @PostMapping("/search/shop/distance")
    public Result<List<ShopInfoVO>> searchShopByDistance(
            @RequestBody O2OShopQueryVo param) {
        Long userId = ISecurity.userOpt().map(SecureUser::getId).getOrNull();
        return Result.ok(
                shopInfoService.searchShopByDistance(param.getLongitude(), param.getLatitude(),
                        param.getSortAsc(), userId, param.getShowHeaderShopIds(), param.getMoreCount()
                )
        );
    }

    /**
     * 获取指定店铺的距离、销量
     *
     * @param shopId    店铺id
     * @param longitude 经度
     * @param latitude  纬度
     */
    @GetMapping("/shopSaleAndDistance")
    public Result<ShopInfoVO> getShopSaleAndDistance(
            @RequestParam Long shopId,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "latitude", required = false) Double latitude) {
        return Result.ok(
                shopInfoService.getShopSaleAndDistance(shopId, longitude, latitude)
        );
    }


    /**
     * 获取供应商信息
     *
     * @param supplierName 供应商名称
     * @return List<Shop>
     */
    @GetMapping("getSupplierInfo")
    public Result<List<Shop>> getSupplierInfo(String supplierName) {
        List<Shop> supplierInfoList = shopInfoService.getSupplierInfo(supplierName);
        return Result.ok(supplierInfoList);
    }

    @GetMapping("getShopInfo")
    public Result<List<Shop>> getShopInfo(String shopName, @RequestParam(required = false, defaultValue = "false") Boolean includeSupplier) {
        List<Shop> shopInfoList = shopInfoService.getShopInfo(shopName, includeSupplier);
        return Result.ok(shopInfoList);
    }


    /**
     * 客户端 关注页面 查询推荐店铺
     *
     * @return IPage ShopInfoVO
     */
    @PostMapping("/searchRecommendationShop")
    public Result<IPage<ShopInfoVO>> searchRecommendationShop(@RequestBody ShopSearchParamDTO param) {
        return Result.ok(shopInfoService.searchRecommendationShop(param));
    }
}
