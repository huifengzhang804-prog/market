package com.medusa.gruul.addon.live.config;

import com.medusa.gruul.addon.live.properties.AddressKeyProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.live.v20180801.LiveClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 腾讯云客户端配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LiveConfig {
    private final AddressKeyProperties addressKeyProperties;

    @Bean
    @ConditionalOnMissingBean
    public LiveClient liveClient(){
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(addressKeyProperties.getSecretId(), addressKeyProperties.getSecretKey());
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("live.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        return new LiveClient(cred, "ap-guangzhou", clientProfile);
    }
}
