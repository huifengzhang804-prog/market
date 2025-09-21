/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.medusa.gruul.common.dubbo.rpc.filter;

import com.medusa.gruul.global.model.exception.GlobalException;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.filter.GenericFilter;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.rpc.support.ProtocolUtils;

import static org.apache.dubbo.common.constants.CommonConstants.$INVOKE;
import static org.apache.dubbo.common.constants.CommonConstants.$INVOKE_ASYNC;
import static org.apache.dubbo.rpc.Constants.GENERIC_KEY;

/**
 * GenericInvokerFilter.
 *
 * @author 张治保
 * @see GenericFilter
 */
@Activate(group = CommonConstants.PROVIDER, order = -20000)
public class DubboGenericFilter extends GenericFilter {

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation inv) {
        if (breakSuper(appResponse, invoker, inv)) {
            return;
        }
        super.onResponse(appResponse, invoker, inv);

    }

    /**
     * 判断是否需要跳过父类的方法
     *
     * @return 是否需要跳过父类的方法
     */
    private boolean breakSuper(Result appResponse, Invoker<?> invoker, Invocation inv) {
        //是否是泛化调用
        boolean isGenericInvoke = inv.getMethodName().equals($INVOKE) || inv.getMethodName().equals($INVOKE_ASYNC);
        if (!isGenericInvoke || inv.getArguments() == null || inv.getArguments().length != 3 || GenericService.class.isAssignableFrom(invoker.getInterface())) {
            return true;
        }
        //是否是 GlobalException异常
        if (appResponse.hasException() && appResponse.getException() instanceof GlobalException) {
            return true;
        }
        //是否是 其它序列化方式
        String generic = getGenericValue(inv);
        return !ProtocolUtils.isJavaGenericSerialization(generic) && !ProtocolUtils.isBeanGenericSerialization(generic)
                && !ProtocolUtils.isProtobufGenericSerialization(generic) && !ProtocolUtils.isGenericReturnRawResult(generic);
    }

    /**
     * 获取泛化类型
     *
     * @param inv Invocation
     * @return 泛化类型
     */
    private String getGenericValue(Invocation inv) {
        String generic = inv.getAttachment(GENERIC_KEY);
        if (StringUtils.isNotBlank(generic)) {
            return generic;
        }
        generic = RpcContext.getServerAttachment().getAttachment(GENERIC_KEY);
        if (StringUtils.isBlank(generic)) {
            generic = RpcContext.getClientAttachment().getAttachment(GENERIC_KEY);
        }
        return generic;
    }

}
