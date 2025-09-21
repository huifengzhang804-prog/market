package com.medusa.gruul.payment.service.controller;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.payment.service.model.vo.ServiceModeVO;
import com.medusa.gruul.payment.service.service.WxServiceService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 张治保
 * date 2023/6/9
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/valid")
public class ValidController {

    private final WxServiceService wxServiceService;

    /**
     * 检查是否开启了服务商模式
     *
     * @return 是否开启了服务商模式
     */
    @GetMapping("/service/enable")
    @PermitAll
    public Result<ServiceModeVO> isServiceModel() {
        return Result.ok(
                new ServiceModeVO()
                        .setServiceEnable(wxServiceService.serviceEnable())
                        .setOpenid(ISecurity.userOpt().map(SecureUser::getOpenid).getOrNull())
        );
    }
}
