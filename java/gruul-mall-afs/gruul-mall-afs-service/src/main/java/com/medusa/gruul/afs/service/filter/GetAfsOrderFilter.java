package com.medusa.gruul.afs.service.filter;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.medusa.gruul.afs.service.model.bo.AfsUpdateBO;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
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
public class GetAfsOrderFilter implements IAutomaticFilter<AfsUpdateBO> {

    private final IAfsOrderService afsOrderService;

    @Override
    public void doFilter(FilterContext<AfsUpdateBO> context) {
        AfsUpdateBO data = context.getData();
        data.setAfsOrder(
                getAfsOrder(data).getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception)
        );
    }

    private Option<AfsOrder> getAfsOrder(AfsUpdateBO data) {
        LambdaQueryChainWrapper<AfsOrder> queryChainWrapper = afsOrderService.lambdaQuery()
                .eq(AfsOrder::getNo, data.getAfsNo());
        boolean isSystem = BooleanUtil.isTrue(data.getIsSystem());
        if (!isSystem) {
            ISecurity.match()
                    //当是用户时
                    .ifUser(secureUser -> queryChainWrapper.eq(AfsOrder::getBuyerId, secureUser.getId()))
                    //商家管理员时
                    .ifAnyShopAdmin(secureUser -> queryChainWrapper.eq(AfsOrder::getShopId, secureUser.getShopId()))
                    //供应商管理员时
                    .ifAnySupplierAdmin(secureUser -> queryChainWrapper.eq(AfsOrder::getSupplierId, secureUser.getShopId()));
        }
        return Option.of(
                TenantShop.disable(queryChainWrapper::one)
        );
    }
}
