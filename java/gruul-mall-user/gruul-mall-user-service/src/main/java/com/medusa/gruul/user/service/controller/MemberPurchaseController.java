package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.service.model.dto.MemberPurchaseQueryDTO;
import com.medusa.gruul.user.service.model.vo.MemberPurchaseHistoryVo;
import com.medusa.gruul.user.service.service.IMemberPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 * @author jipeng
 * @Description 会员购买记录Controller
 * @date 2022-11-15 15:12
 */
@RestController
@RequestMapping("/member/")
@RequiredArgsConstructor
public class MemberPurchaseController {





    private final IMemberPurchaseService memberPurchaseService;

    /**
     * 会员流水列表
     *
     * @param memberPurchaseQuery 会员query
     * @return Page<会员流水vo />
     */
    @Log("会员流水列表查询")
    @Idem(200)
    @GetMapping("purchase/list")
    public Result<Page<MemberPurchaseHistoryVo>> queryList(
            MemberPurchaseQueryDTO memberPurchaseQuery) {
        Page<MemberPurchaseHistoryVo> result = memberPurchaseService.list(memberPurchaseQuery);

        return Result.ok(result);
    }

    /**
     * 会员流水导出
     *
     * @param query 相关条件
     * @return 导出id
     */
    @Log("会员流水列表导出")
    @Idem(200)
    @PostMapping("purchase/export")
    public Result<Long> export(@RequestBody MemberPurchaseQueryDTO query) {
        Long dataExportId = memberPurchaseService.export(query);
        return Result.ok(dataExportId);
    }

}



