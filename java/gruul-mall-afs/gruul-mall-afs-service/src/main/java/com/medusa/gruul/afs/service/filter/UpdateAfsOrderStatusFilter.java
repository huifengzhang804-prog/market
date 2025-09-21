package com.medusa.gruul.afs.service.filter;

import com.medusa.gruul.afs.service.model.bo.AfsUpdateBO;
import com.medusa.gruul.afs.service.model.enums.AfsError;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.global.model.filter.FilterContext;
import com.medusa.gruul.global.model.filter.IAutomaticFilter;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/22
 */
@Component
@RequiredArgsConstructor
public class UpdateAfsOrderStatusFilter implements IAutomaticFilter<AfsUpdateBO> {

    private final IAfsOrderService afsOrderService;

    @Override
    public void doFilter(FilterContext<AfsUpdateBO> context) {
        AfsUpdateBO data = context.getData();
        AfsStatus nextStatus = Option.of(data.getNextStatus()).getOrElseThrow(AfsError.AFS_NEXT_STATUS_NOT_NULL::exception);
        boolean success = TenantShop.disable(
                () -> afsOrderService.lambdaUpdate()
                        .set(AfsOrder::getStatus, nextStatus)
                        .set(AfsOrder::getPackageStatus, data.getShopOrderItem().getPackageStatus())
                        .eq(AfsOrder::getStatus, data.getAfsOrder().getStatus())
                        .eq(AfsOrder::getNo, data.getAfsNo())
                        .update()
        );
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
    }
}
