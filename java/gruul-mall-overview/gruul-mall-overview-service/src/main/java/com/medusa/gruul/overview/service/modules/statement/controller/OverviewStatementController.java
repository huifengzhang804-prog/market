package com.medusa.gruul.overview.service.modules.statement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.overview.api.enums.SourceEnum;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.vo.OverviewStatementPageVO;
import com.medusa.gruul.overview.service.modules.statement.service.StatementQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 对账单 前端控制器
 *
 * @author WuDi
 * @since 2022-10-09
 */
@RestController
@RequestMapping("/overview/statement")
@RequiredArgsConstructor
public class OverviewStatementController {

    private final StatementQueryService statementQueryService;


    /**
     * 分页查询对账单
     *
     * @param query 查询参数
     * @return 对账单
     */
    @Log("查询对账单")
    @PostMapping
    @PreAuthorize("""
            	@S.matcher().any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.R.CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN, @S.R.SUPER_ADMIN)
            		.or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'reconciliation','finance:reconciliation'))
            		.match()
            """)
    public Result<OverviewStatementPageVO> queryStatement(@RequestBody @Valid OverviewStatementQueryDTO query) {
        query.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(
                statementQueryService.queryStatement(query)
        );
    }

    @PostMapping("/export")
    @Log("导出对账单")
//	@Idem
    @PreAuthorize("""
            	@S.matcher().any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.R.CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN, @S.R.SUPER_ADMIN)
            		.or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'reconciliation','finance:reconciliation'))
            		.match()
            """)
    public Result<Long> export(@RequestBody OverviewStatementQueryDTO query) {
        query.setPage(new Page<>());
        AtomicReference<SourceEnum> source = new AtomicReference<>();
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> source.set(SourceEnum.SHOP))
                .ifCustomAdmin(secureUser -> source.set(SourceEnum.SHOP))
                .ifAnySupplierAdmin(secureUser -> source.set(SourceEnum.SUPPLIER))
                .ifCustomSuperAdmin(secureUser -> source.set(SourceEnum.PLATFORM))
                .ifSuperAdmin(secureUser -> source.set(SourceEnum.PLATFORM));
        query.setShopId(ISecurity.userMust().getShopId());
        SystemCode.PARAM_VALID_ERROR.trueThrow(Objects.isNull(source.get()));
        Long exportRecorderId = statementQueryService.export(query, source.get());
        return Result.ok(exportRecorderId);
    }
}
