package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.service.model.dto.GuessYouLikeQueryDTO;
import com.medusa.gruul.user.api.model.GuessYouLikeVO;
import com.medusa.gruul.user.service.service.UserFootMarkManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 猜你喜欢 前端控制器
 * @author xiaoq
 */
@RestController
@RequestMapping("/user/GuessYouLike")
@RequiredArgsConstructor
public class UserGuessYouLikeController {


    private final UserFootMarkManageService userFootMarkService;


    /**
     * 猜你喜欢
     * @param guessYouLikeQueryDTO 查询参数
     */
    @Log("猜你喜欢")
    @GetMapping("/list")
    public Result<IPage<GuessYouLikeVO>> guessYouLike(GuessYouLikeQueryDTO guessYouLikeQueryDTO) {
        return Result.ok(
                userFootMarkService.guessYouLike(guessYouLikeQueryDTO)
        );
    }

}
