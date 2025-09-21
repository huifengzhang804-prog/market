package com.medusa.gruul.carrier.pigeon.service.modules.message;

import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import com.medusa.gruul.carrier.pigeon.service.service.MessageAppletSubscribeService;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appletMessageSubscribe")
public class AppletMessageSubscribeController {


    private final MessageAppletSubscribeService messageAppletSubscribeService;


    /**
     * 获取模板id
     */
    @GetMapping("/getTemplateIds")
    public Result<Map<SubscribeMsg, String>> getTemplateIds() {
        return Result.ok(
                messageAppletSubscribeService.getTemplateIds()
        );
    }

}
