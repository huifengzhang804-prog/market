package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.api.enums.UserFootMarkStatus;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.service.model.dto.UserFootMarkQueryDTO;
import com.medusa.gruul.user.service.service.UserFootMarkManageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 用户足迹 前端控制器
 *
 * @author WuDi
 * @since 2022-07-29
 */
@RestController
@RequestMapping("/user/FootMark")
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
@RequiredArgsConstructor
public class UserFootMarkController {

    private final UserFootMarkManageService userFootMarkService;

    /**
     * 获取用户足迹
     *
     * @param userFootMarkQueryDTO 用户足迹查询条件
     */
    @Log("获取用户足迹")
    @GetMapping("/list")
    public Result<IPage<UserFootMarkVO>> userFootMarkPage(@Valid UserFootMarkQueryDTO userFootMarkQueryDTO) {
        return Result.ok(
                userFootMarkService.userFootMarkPage(userFootMarkQueryDTO)
        );
    }

    /**
     * 根据月份获取有足迹的日期
     *
     * @param month 月份
     */
    @Log("根据月份获取有足迹的日期")
    @GetMapping("/getFootMarkBrowsDates")
    public Result<List<LocalDate>> getFootMarkBrowsDates(@RequestParam("month") Integer month) {
        return Result.ok(
                userFootMarkService.getFootMarkBrowsDates(month));
    }

    /**
     * 添加用户足迹
     *
     * @param userFootMark 用户足迹
     */
    @Log("添加用户足迹")
    @Idem
    @PostMapping
    public Result<Void> addUserFootMark(@RequestBody @Valid UserFootMarkDTO userFootMark) {
        userFootMarkService.addUserFootMark(userFootMark, ISecurity.userMust().getId());
        return Result.ok();
    }

    /**
     * 足迹批量删除
     *
     * @param userFootMarkIds    用户足迹id集合
     * @param userFootMarkStatus 用户状态枚举
     */
    @Idem(500)
    @Log("足迹批量删除")
    @DeleteMapping
    public Result<Void> batchDeleteUserFootMark(@RequestBody @Length(min = 1) Set<Long> userFootMarkIds, @RequestParam UserFootMarkStatus userFootMarkStatus) {
        userFootMarkService.batchDeleteUserFootMark(userFootMarkIds, userFootMarkStatus);
        return Result.ok();
    }


}
