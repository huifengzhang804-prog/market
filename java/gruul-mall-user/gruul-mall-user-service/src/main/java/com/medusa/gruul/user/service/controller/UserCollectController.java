package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.api.model.UserCollectVO;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.service.model.dto.CollectDTO;
import com.medusa.gruul.user.service.model.dto.UserCollectQueryDTO;
import com.medusa.gruul.user.service.service.UserCollectManageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收藏 前端控制器
 *
 * @author WuDi
 * @since 2022-08-01
 */
@Validated
@RestController
@RequestMapping("/user/collect")
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
@RequiredArgsConstructor
public class UserCollectController {


    private final UserCollectManageService userCollectService;

    /**
     * 获取用户收藏
     *
     * @param userCollectQuery 分页参数
     */
    @Log("获取用户收藏")
    @GetMapping("/getUserCollectInfo")
    public Result<IPage<UserCollectVO>> getUserCollectPage(UserCollectQueryDTO userCollectQuery) {
        return Result.ok(
                userCollectService.getUserCollectPage(userCollectQuery)
        );
    }

    /**
     * 用户收藏、取消收藏
     *
     * @param collectDTO 用户收藏集合
     */
    @Idem(1000)
    @Log("用户收藏、取消收藏")
    @PostMapping
    public Result<Void> addUserCollect(@RequestBody @Valid CollectDTO collectDTO) {
        userCollectService.userCollect(collectDTO);
        return Result.ok();
    }

    /**
     * 取消用户收藏
     *  //TODO 2023 3 月底 该接口废弃  2.0.9版本将删除该接口
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @deprecated 标记过期 前端接口排除后去除代码
     */
    @Log("取消用户收藏")
    @Idem
    @GetMapping("cancel")
    public Result<Void> cancelUserCollect(@RequestParam("shopId") Long shopId,
                                          @RequestParam("productId") Long productId) {
        userCollectService.cancelUserCollect(shopId, productId);
        return Result.ok();
    }

    /**
     * 查询用户是否对该商品进行收藏
     *
     * @param shopId    店铺id
     * @param productId 商品id
     */
    @Log("查询用户是否对该商品进行收藏")
    @GetMapping("findUserIsCollect")
    public Result<Boolean> findUserIsCollect(@RequestParam("shopId") Long shopId,
                                             @RequestParam("productId") Long productId) {
        return Result.ok(
                userCollectService.findUserIsCollect(shopId, productId, ISecurity.userOpt().get().getId())
        );
    }

    @Log("用户收藏数量包含商品与店铺")
    @GetMapping("/getUserCollectInfo/count")
    public Result<Long> getUserCollectInfoCount() {
        return Result.ok(
                userCollectService.getUserCollectInfoCount(ISecurity.userMust().getId())
        );
    }



}
