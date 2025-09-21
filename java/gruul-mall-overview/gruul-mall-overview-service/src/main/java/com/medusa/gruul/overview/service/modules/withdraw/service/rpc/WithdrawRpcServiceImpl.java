package com.medusa.gruul.overview.service.modules.withdraw.service.rpc;

import com.medusa.gruul.overview.api.rpc.WithdrawRpcService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author jipeng
 * @since 2024/11/7
 */
@Service
@DubboService
@RequiredArgsConstructor
public class WithdrawRpcServiceImpl implements WithdrawRpcService {

    private final WithdrawService withdrawService;
    @Override
    public void updateWithdrawOrderStatus(String orderNo, String status,String closeReason) {
       withdrawService.updateWithdrawOrderStatus(orderNo,status,closeReason);

    }
}
