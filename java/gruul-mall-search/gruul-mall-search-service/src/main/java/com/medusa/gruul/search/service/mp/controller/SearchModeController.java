package com.medusa.gruul.search.service.mp.controller;


import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.enums.Mode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search/mode")
public class SearchModeController {

    private final GlobalAppProperties globalAppProperties;


    @GetMapping
    public Result<Mode> getMode() {
        return Result.ok(
                globalAppProperties.getMode()
        );
    }
}
