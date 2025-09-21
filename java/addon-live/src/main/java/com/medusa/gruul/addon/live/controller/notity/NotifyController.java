package com.medusa.gruul.addon.live.controller.notity;

import com.medusa.gruul.addon.live.service.AddonLiveRoomService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 直播回调
 *
 * @author miskw
 * @date 2023/6/5
 * @describe 直播回调
 */
@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class NotifyController {
    private final AddonLiveRoomService liveRoomService;

    /**
     * 直播回调
     *
     * @param request 请求
     * @return 回调成功放回200
     * @throws IOException 参数转换异常
     */
    @Log("直播回调")
    @PostMapping("api/notify")
    public Result<String> notify(HttpServletRequest request) throws IOException {
        liveRoomService.liveNotify(request);
        return Result.ok("回调成功");
    }
}
