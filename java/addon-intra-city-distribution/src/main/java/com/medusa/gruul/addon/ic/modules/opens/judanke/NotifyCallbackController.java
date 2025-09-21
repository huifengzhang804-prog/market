package com.medusa.gruul.addon.ic.modules.opens.judanke;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张治保
 * @since 2024/8/8
 */
@RestController
public class NotifyCallbackController {

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("permitAll()")
    public String orderStatusNotify(JudankeRequest request) {
        return "SUCCEED";
    }

}
