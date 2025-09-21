package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.service.model.dto.ShopUserQueryDTO;
import com.medusa.gruul.user.service.model.vo.ShopUserVO;
import com.medusa.gruul.user.service.service.ShopUserAccountManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  店铺客户列表前端控制器
 *
 * @author WuDi
 * @since 2023-05-17
 */
@RestController
@RequestMapping("/user/shopUserAccount")
@RequiredArgsConstructor
public class ShopUserAccountController {


    private final ShopUserAccountManageService shopUserAccountManageService;

    /**
     * 分页查询店铺客户列表
     *
     * @param shopUserQuery 店铺客户查询参数
     * @return 店铺客户列表
     */
    @Log("查询店铺客户列表")
    @GetMapping
    public Result<IPage<ShopUserVO>> getShopUserAccountList(ShopUserQueryDTO shopUserQuery) {
        return Result.ok(
                shopUserAccountManageService.getShopUserAccountList(shopUserQuery.setShopId(ISecurity.userMust().getShopId()))
        );

    }
    /**
     * 查询店铺客户详情
     *
     * @param userId 店铺客户id
     * @return 店铺客户详情
     */
    @Log("查询店铺客户详情")
    @GetMapping("/detail")
    public Result<ShopUserVO> getShopUserAccountDetail(Long userId) {
        return Result.ok(
                shopUserAccountManageService.getShopUserAccountDetail(userId)
        );
    }

}
