package com.medusa.gruul.common.mp.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * description: 租户维护处理器
 *
 * @author alan
 * date: 2019/7/13 19:32
 */
@Slf4j
@RequiredArgsConstructor
public class ProviderTenantHandler implements TenantLineHandler {

    private final TenantConfigProperties properties;

    /**
     * 获取租户值
     * 
     * TODO 校验租户状态
     *
     * @return 租户值
     */
    @Override
    public Expression getTenantId() {
        return new LongValue(-1);
    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getPlatformId();
    }

    /**
     * 根据表名判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤
     */
    @Override
    public boolean ignoreTable(String tableName) {
        return true;
    }
}
