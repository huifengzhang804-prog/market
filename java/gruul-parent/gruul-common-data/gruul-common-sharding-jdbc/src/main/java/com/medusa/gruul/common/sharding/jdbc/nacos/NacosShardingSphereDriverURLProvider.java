//package com.medusa.gruul.common.sharding.jdbc.nacos;
//
//import com.alibaba.cloud.nacos.NacosConfigProperties;
//import com.alibaba.nacos.shaded.com.google.common.base.Strings;
//import lombok.SneakyThrows;
//import org.apache.shardingsphere.driver.jdbc.core.driver.ShardingSphereDriverURLProvider;
//
//import java.nio.charset.StandardCharsets;
//
///**
// * @author 张治保
// * @since 2024/5/9
// */
//public class NacosShardingSphereDriverURLProvider implements ShardingSphereDriverURLProvider {
//    private static final String CLASSPATH_TYPE = "nacos:";
//
//    private static final String URL_PREFIX = "jdbc:shardingsphere:";
//
//    @Override
//    public boolean accept(final String url) {
//        return !Strings.isNullOrEmpty(url) && url.contains(CLASSPATH_TYPE);
//    }
//
//
//    @Override
//    @SneakyThrows
//    public byte[] getContent(String url) {
//        String configPath = url.substring(URL_PREFIX.length(), url.contains("?") ? url.indexOf('?') : url.length());
//        String dataId = configPath.substring(CLASSPATH_TYPE.length());
//        NacosConfigProperties nacosConfigProperties = NacosConfigiServiceUtils.getConfigManager()
//                .getNacosConfigProperties();
//        String content = NacosConfigiServiceUtils.getConfigService()
//                .getConfig(dataId, nacosConfigProperties.getGroup(), nacosConfigProperties.getTimeout());
//        return content.getBytes(StandardCharsets.UTF_8);
//    }
//}
