package com.medusa.gruul.addon.store.controller;

import com.medusa.gruul.addon.store.model.dto.ShopAssistantDTO;
import com.medusa.gruul.addon.store.model.vo.ShopAssistantVO;
import com.medusa.gruul.addon.store.service.ManageShopAssistantService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺店员控制层
 *
 * @author xiaoq
 * @Description 店铺店员控制层
 * @date 2023-03-14 14:28
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/assistant")
public class ShopAssistantController {

    private final ManageShopAssistantService manageShopAssistantService;

    /**
     * 店铺店员新增
     *
     * @return void
     */
    @Idem(1000)
    @Log("店铺店员新增")
    @PostMapping("issue")
    @PreAuthorize("@S.shopPerm('shop:store')")
    public Result<Void> issueShopAssistant(@RequestBody ShopAssistantDTO shopAssistant) {
        manageShopAssistantService.issueShopAssistant(shopAssistant, ISecurity.userMust().getShopId());
        return Result.ok();
    }

    /**
     * 店铺店员获取
     *
     * @return List<店铺店员信息>
     */
    @Log("店铺店员列表获取")
    @GetMapping("list")
    @PreAuthorize("@S.shopPerm('shop:store')")
    public Result<List<ShopAssistantVO>> getShopAssistantList() {
        return Result.ok(manageShopAssistantService.getShopAssistantList());
    }

    /**
     * 给店员设置门店
     *
     * @param storeId         门店id
     * @param shopAssistantId 店员id
     * @return void
     */
    @Log("给店员设置门店")
    @PutMapping("set/store/{shopAssistantId}")
    @PreAuthorize("@S.shopPerm('shop:store')")
    public Result<Void> setStore(@NotBlank Long storeId, @PathVariable(name = "shopAssistantId") Long shopAssistantId) {
        manageShopAssistantService.setStore(storeId, shopAssistantId);
        return Result.ok();
    }

    /**
     * 删除店员信息
     *
     * @param shopAssistantId 店员id
     * @return void
     */
    @Log("删除店员信息")
    @DeleteMapping("del/{shopAssistantId}")
    @PreAuthorize("@S.shopPerm('shop:store')")
    public Result<Void> delShopAssistant(@PathVariable(name = "shopAssistantId") Long shopAssistantId) {
        manageShopAssistantService.delShopAssistant(shopAssistantId);
        return Result.ok();
    }
}
