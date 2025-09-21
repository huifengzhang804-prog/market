package com.medusa.gruul.common.system.model.remote;

import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import com.medusa.gruul.common.system.model.filter.SystemFilter;
import io.vavr.control.Option;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * dubbo rpc服务提供者 解析token
 *
 * @author 张治保
 * date 2022/7/5
 */
@Activate(group = CommonConstants.PROVIDER)
public class SystemDubboProviderSpreadConfig implements Filter {


    private String getAttachment(RpcContext rpcContext, String key) {
        return Option.of(rpcContext.getAttachment(key.toLowerCase()))
                .getOrElse(() -> rpcContext.getAttachment(key));
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();
        SystemFilter.setContextHolder(
                "[dubbo]>>>" + invocation.getTargetServiceUniqueName() + "#" + invocation.getMethodName(),
                this.getAttachment(serverAttachment, SystemHeaders.IP),
                this.getAttachment(serverAttachment, SystemHeaders.DEVICE_ID),
                this.getAttachment(serverAttachment, SystemHeaders.CLIENT_TYPE),
                this.getAttachment(serverAttachment, SystemHeaders.VERSION),
                this.getAttachment(serverAttachment, SystemHeaders.PROVIDER_ID),
                this.getAttachment(serverAttachment, SystemHeaders.SHOP_ID),
                this.getAttachment(serverAttachment, SystemHeaders.PLATFORM)

        );
        try {
            return invoker.invoke(invocation);
        } finally {
            SystemFilter.clearAll();
        }
    }
}
