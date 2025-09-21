package com.medusa.gruul.common.mp.handler;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.medusa.gruul.common.mp.exception.TenantException;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.mp.model.TenantShopError;
import com.medusa.gruul.common.mp.properties.TenantConfigProperties;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.i18n.I18N;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.HashSet;
import java.util.Set;

/**
 * description: 租户维护处理器
 *
 * @author alan
 * Date: 2019/7/13 19:32
 */
@Slf4j
@RequiredArgsConstructor
public class ShopTenantHandler implements TenantLineHandler {

    private static final TenantException EXCEPTION = new TenantException(TenantShopError.SHOP_ID_INVALID.code(), TenantShopError.SHOP_ID_INVALID.msg());
    private final TenantConfigProperties properties;

    /**
     * 获取租户值
     *
     * @return 租户值
     */
    @Override
    public Expression getTenantId() {
        Long shopId = ISystem.shopIdMust();
        if (log.isDebugEnabled()) {
            log.debug(I18N.msg("mp.shop.id.debug", shopId));
        }
        return new LongValue(shopId);

    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getShopId();
    }

    /**
     * 根据表名判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤
     */
    @Override
    public boolean ignoreTable(String tableName) {

        if (TenantShop.isDisable()) {
            return true;
        }
        Set<String> ignoreAllTables = properties.getIgnoreAllTables();
        Set<String> ignoreShopIdTables = properties.getIgnoreShopIdTables();

        Set<String> ignoreTables = new HashSet<>(ignoreAllTables);
        ignoreTables.addAll(ignoreShopIdTables);
        /*
         * 避免保留字的表名
         */
        return ignoreTables.stream().anyMatch(
                name -> StrUtil.equals(tableName, name) || StrUtil.equals(tableName, avoidReservedWordTableName(name))
        );
    }

    /**
     * 获取 ``字符修饰的表名 `t_menu`
     */
    private String avoidReservedWordTableName(String tableName) {
        return "`" + tableName + "`";
    }
}
