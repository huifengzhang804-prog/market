package com.medusa.gruul.addon.bargain.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.service.BargainHelpPeopleManageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帮砍表 前端控制器
 *
 * @author WuDi
 * @since 2023-03-14
 */
@RestController
@RequestMapping("/bargainHelpPeople")
@RequiredArgsConstructor
public class BargainHelpPeopleController {


    private final BargainHelpPeopleManageService bargainHelpPeopleService;

    /**
     * 分页查询砍价帮砍列表
     *
     * @param bargainHelpPeopleQuery 砍价帮砍人查询条件
     * @return 砍价帮砍人分页数据
     */
    @Log("分页查询砍价帮砍列表")
    @GetMapping
    @PreAuthorize("@S.shopPerm('marketingApp:bargain')")
    public Result<IPage<BargainHelpPeopleVO>> getBargainHelpPeoplePage(@Validated BargainHelpPeopleQueryDTO bargainHelpPeopleQuery) {
        return Result.ok(
                bargainHelpPeopleService.getBargainHelpPeoplePage(bargainHelpPeopleQuery)
        );
    }


}
