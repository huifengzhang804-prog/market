package com.medusa.gruul.overview.service.modules.operate.controller;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.modules.operate.service.ManageDealRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 移动端商家后台
 *
 * @author miskw
 * @date 2023/3/3
 */
@RestController
@RequestMapping("/overview/mobile/merchant/background")
@RequiredArgsConstructor
public class MobileMerchantBackgroundController {

    private final ManageDealRankingService manageDealRankingService;

    /**
     * 移动端商家后台 店铺指定月份的交易概况
     *
     * @param dealRankingParam 指定月份
     */
    @GetMapping("statistics")
    public Result<DealStatisticsVO> getMobileDealStatistics(@Validated(DealRankingParam.MobileDealStatistics.class) DealRankingParam dealRankingParam) {
        return Result.ok(manageDealRankingService.getMobileDealStatistics(dealRankingParam));
    }
}
