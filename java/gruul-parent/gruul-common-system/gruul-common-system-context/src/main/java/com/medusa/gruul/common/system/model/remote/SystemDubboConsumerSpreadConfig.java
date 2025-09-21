package com.medusa.gruul.common.system.model.remote;

import cn.hutool.core.collection.CollUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;


/**
 * dubbo rpc消费者端传递token信息
 *
 * @author 张治保
 * date 2022/7/5
 */
@Activate(group = CommonConstants.CONSUMER)
public class SystemDubboConsumerSpreadConfig implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> headers = HeaderRender.requestHeaders();
        RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();
        if (CollUtil.isNotEmpty(headers)) {
            headers.forEach(clientAttachment::setAttachment);
        }
        return invoker.invoke(invocation);

    }
}
