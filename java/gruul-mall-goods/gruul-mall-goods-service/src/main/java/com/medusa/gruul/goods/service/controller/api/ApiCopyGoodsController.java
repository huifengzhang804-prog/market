package com.medusa.gruul.goods.service.controller.api;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.service.model.param.CopyGoodsParam;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.service.CopyGoodsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 一键复制
 *
 * @author miskw
 * @date 2023/1/30
 * @describe 一键复制
 */
@RestController
@RequestMapping("/api/copy/goods")
@RequiredArgsConstructor
public class ApiCopyGoodsController {

    private final CopyGoodsService copyGoodsService;

    /**
     * 一键复制
     */
    @GetMapping("/detail")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:list')).match()
            """)
    public Result<CopyProductVO> getDetail(@Valid CopyGoodsParam copyGoodsDto) {
        return Result.ok(copyGoodsService.getDetail(copyGoodsDto));
    }

}
