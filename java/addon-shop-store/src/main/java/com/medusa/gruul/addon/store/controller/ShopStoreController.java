package com.medusa.gruul.addon.store.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.store.model.dto.ShopStoreDTO;
import com.medusa.gruul.addon.store.model.dto.ShopStoreDistanceDTO;
import com.medusa.gruul.addon.store.model.dto.ShopStoreOperateDTO;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import com.medusa.gruul.addon.store.model.param.ShopStoreParam;
import com.medusa.gruul.addon.store.model.vo.ShopStoreAssistantVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreDistanceVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreListVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.service.CommonShopStoreService;
import com.medusa.gruul.addon.store.service.ManageShopStoreService;
import com.medusa.gruul.addon.store.service.MobileShopStoreService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.Final;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaoq
 * @Description 店铺门店端控制层
 * @date 2023-03-07 10:59
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class ShopStoreController {
    private final ManageShopStoreService manageShopStoreService;

    private final CommonShopStoreService commonShopStoreService;

    private final MobileShopStoreService mobileShopStoreService;

    /**
     * 店铺门店新增
     *
     * @param shopStoreDTO 店铺门店DTO
     * @return void
     */
    @Idem(1000)
    @Log("新增店铺门店")
    @PostMapping("issue")
    @PreAuthorize("@S.shopPerm('shop:store')")
    public Result<Void> issueShopStore(@RequestBody @Validated ShopStoreDTO shopStoreDTO) {
        manageShopStoreService.issueShopStore(shopStoreDTO, ISecurity.userMust().getShopId());
        return Result.ok();
    }


    /**
     * 店铺门店删除
     *
     * @param id     门店id
     * @param shopId 店铺id
     * @return void
     */
    @Idem(1000)
    @Log("店铺门店删除")
    @DeleteMapping("del/{shopId}/{id}")
    @PreAuthorize("@S.shopPerm('shop:store')")
    @Deprecated(since = "2.1.0 需求变动不允许删除店铺 预留一版",forRemoval = true)
    public Result<Void> deletedShopStore(@PathVariable(name = "shopId") Long shopId, @PathVariable(name = "id") Long id) {
        Final<Long> box = new Final<>(shopId);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> box.set(secureUser.getShopId()));
        manageShopStoreService.deletedShopStore(box.get(), id);
        return Result.ok();
    }


    /**
     * 修改店铺门店信息
     *
     * @param shopStoreDTO 店铺门店DTO
     * @return void
     */
    @Idem(1000)
    @Log("修改店铺门店")
    @PostMapping("update")
    @PreAuthorize("@S.anyPerm('store','shop:store')")
    public Result<Void> updateShopStore(@RequestBody @Validated ShopStoreDTO shopStoreDTO) {
        manageShopStoreService.updateShopStore(shopStoreDTO);
        return Result.ok();
    }


    /**
     * 店铺门店列表
     *
     * @param shopStoreParam param查询数据
     * @return 分页列表VO
     */
    @Log("店铺门店列表")
    @GetMapping("list")
    @PreAuthorize("@S.anyPerm('store','shop:store')")
    public Result<IPage<ShopStoreListVO>> getShopStoreListVO(ShopStoreParam shopStoreParam) {
        return Result.ok(manageShopStoreService.getShopStoreListVO(shopStoreParam));
    }

    /**
     * 店铺门店详情
     *
     * @param id     门店id
     * @param shopId 店铺id
     * @return 门店详细信息
     */
    @Log("店铺门店详情")
    @GetMapping("info/{shopId}")
    @PreAuthorize("@S.anyPerm('store','shop:store')")
    public Result<ShopStoreAssistantVO> getShopStoreInfoById(Long id, @PathVariable(name = "shopId") Long shopId) {
        Final<Long> box = new Final<>(shopId);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> box.set(secureUser.getShopId()));
        return Result.ok(commonShopStoreService.getShopStoreInfoById(id, box.get()));
    }


    /**
     * 店铺门店状态修改
     *
     * @param shopStoreOperate 店铺门店操作dto
     * @param status           修改状态
     * @return void
     */
    @Log("店铺门店状态修改")
    @PutMapping("update/{status}")
    @PreAuthorize("@S.anyPerm('store','shop:store')")
    public Result<Void> updateShopStoreStatus(@RequestBody List<ShopStoreOperateDTO> shopStoreOperate, @PathVariable("status") StoreStatus status) {
        manageShopStoreService.updateShopStoreStatus(shopStoreOperate, status);
        return Result.ok();
    }


    /**
     * 店铺门店详情/移动门店端
     *
     * @param shopAssistantPhone 门店店员手机号
     * @return 门店信息
     */
    @Log("店铺门店详情/门店端")
    @GetMapping("info/byShopAssistantPhone")
    public Result<ShopStore> getShopStoreByShopAssistantPhone(String shopAssistantPhone) {
        return Result.ok(mobileShopStoreService.getShopStoreByShopAssistantPhone(shopAssistantPhone));
    }


    /**
     * 获取门店 按距离排序
     *
     * @return 门店信息 按距离
     */
    @Log("获取当前门店 按距离排序")
    @PostMapping("distance/list")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<List<ShopStoreDistanceVO>> getStoreDistance(@RequestBody @Validated ShopStoreDistanceDTO shopStoreDistance) {
        return Result.ok(mobileShopStoreService.getStoreDistance(shopStoreDistance));
    }


    /**
     * 获取提货时间
     *
     * @param id     门店id
     * @param shopId 店铺id
     * @return 门店提货时间
     */
    @GetMapping("optional/delivery/time/{shopId}")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<ShopStore> getStoreOptionalDeliveryTime(Long id, @PathVariable(name = "shopId") Long shopId) {
        return Result.ok(mobileShopStoreService.getStoreOptionalDeliveryTime(id, shopId));
    }

}
