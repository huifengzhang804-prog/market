package com.medusa.gruul.shop.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.service.service.ShopManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺访客控制层
 *
 * @author xiaoq
 * @Description ShopVisitorController.java
 * @date 2022-10-25 17:04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/visitor")
public class ShopVisitorController {
    private final ShopManageService shopManageService;


    /**
     * 添加用户访问
     */
    @PostMapping("/add")
    @Log("添加用户访问店铺数据")
    public Result<Void> addShopVisitor() {
        shopManageService.addShopVisitor();
        return Result.ok();
    }


    /**
     * 获取店铺访客量数据
     *
     * @return 店铺访客量
     */
    @GetMapping("/get")
    @Log("获取店铺访客量数据")
    public Result<Long> getShopVisitor(){
        return Result.ok(shopManageService.getShopVisitorNum());
    }
}
