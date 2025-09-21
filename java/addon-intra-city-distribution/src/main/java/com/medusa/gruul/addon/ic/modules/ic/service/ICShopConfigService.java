package com.medusa.gruul.addon.ic.modules.ic.service;

import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopConfigDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.ShopConfigVO;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.shop.api.model.dto.ShopIcDistributeInfoDTO;
import com.vividsolutions.jts.geom.Point;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/13
 */
public interface ICShopConfigService {

    /**
     * 获取原始店铺同城配置
     *
     * @param shopId 店铺 id
     * @return 原始店铺同城配置
     */
    ICShopConfig config(Long shopId);

    /**
     * 获取店铺同城配置
     *
     * @param shopId 店铺 id
     * @return 店铺同城配置
     */
    ShopConfigVO getConfig(Long shopId);

    /**
     * 保存店铺同城配置
     *
     * @param shopId 店铺id
     * @param config 店铺同城配置
     */
    void saveConfig(Long shopId, ICShopConfigDTO config);

    /**
     * 同城配送运费计算
     *
     * @param shopIds
     * @param point
     * @return
     */
    Map<Long, ShopIcDistributeInfoDTO> calculateDistributeInfo(Set<Long> shopIds, Point point);
}
