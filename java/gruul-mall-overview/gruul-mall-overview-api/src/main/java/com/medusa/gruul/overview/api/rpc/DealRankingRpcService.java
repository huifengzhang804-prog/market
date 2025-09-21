package com.medusa.gruul.overview.api.rpc;

import com.medusa.gruul.overview.api.model.SupplierDealRankingDTO;

/**
 * <p>deal ranking RPC接口</p>
 * @author An.Yan
 */
public interface DealRankingRpcService {

    /**
     * 保存deal ranking数据
     * @param dto {@link SupplierDealRankingDTO}
     */
    void saveDealRanking(SupplierDealRankingDTO dto);
}
