package com.medusa.gruul.storage.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.storage.service.model.param.StorageDetailParam;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.service.QueryStorageDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StorageDetailController.java
 *
 * @author xiaoq
 * @Description StorageDetailController.java
 * @date 2023-07-27 14:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/storage/detail")
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'inventory:count')).match()
        """)
public class StorageDetailController {

    private final QueryStorageDetailService queryStorageDetailService;


    /**
     * 获取仓储变化明细
     *
     * @param storageDetailParam 查询参数
     * @return IPage<StorageDetail>
     */
    @Log("仓储明细")
    @GetMapping("page")
    public Result<IPage<StorageDetail>> getStorageDetailList(StorageDetailParam storageDetailParam) {
        storageDetailParam.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(queryStorageDetailService.getStorageDetailList(storageDetailParam));
    }

    @Log("仓储明细导出")
    @PostMapping("page/export")
    public Result<Void> export(@RequestBody StorageDetailParam storageDetailParam) {
        storageDetailParam.setShopId(ISecurity.userMust().getShopId());
        queryStorageDetailService.export(storageDetailParam);
        return Result.ok();
    }
}
