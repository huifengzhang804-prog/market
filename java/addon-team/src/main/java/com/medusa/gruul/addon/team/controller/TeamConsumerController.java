package com.medusa.gruul.addon.team.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.model.dto.TeamProductOrderPageDTO;
import com.medusa.gruul.addon.team.model.vo.TeamActivityProductVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.service.TeamConsumerService;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张治保
 * date 2023/3/15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/team/product")
public class TeamConsumerController {

    private final TeamConsumerService teamConsumerService;

    /**
     * 获取商品拼团状态与数据
     */
    @GetMapping("/{shopId}/{productId}")
    public Result<TeamProductVO> productTeamStatus(@PathVariable Long shopId, @PathVariable Long productId) {
        return Result.ok(teamConsumerService.productTeamStatus(shopId, productId));
    }


    /**
     * 分页查询商品凑团订单列表
     */
    @GetMapping("/{shopId}/{productId}/orders")
    public Result<IPage<TeamOrderPageVO>> teamProductOrderPage(@Valid TeamProductOrderPageDTO query) {
        query.setUserId(null);
        ISecurity.match()
                .ifUser(secureUser -> query.setUserId(secureUser.getId()));
        return Result.ok(teamConsumerService.teamProductOrderPage(query));
    }


    /**
     * 分页查询拼团活动商品列表
     */
    @GetMapping
    public Result<IPage<TeamActivityProductVO>> activityProductPage(Page<TeamActivityProductVO> query) {
        return Result.ok(teamConsumerService.activityProductPage(query));
    }

}
