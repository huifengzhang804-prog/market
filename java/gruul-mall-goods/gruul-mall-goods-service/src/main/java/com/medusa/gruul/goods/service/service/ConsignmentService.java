package com.medusa.gruul.goods.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.ConsignmentSetting;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.dto.PaveGoodsDTO;
import com.medusa.gruul.goods.api.model.dto.SinglePaveGoodsDTO;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierIssueProductListVO;

/**
 * @author miskw
 * @date 2023/8/8
 * @describe 描述
 */
public interface ConsignmentService {
    /**
     * 代销设置修改
     *
     * @param consignmentPriceSetting 代销设置参数
     * @param shopId 店铺id
     */
    void consignmentConfig(Long shopId, ConsignmentPriceSettingDTO consignmentPriceSetting);

    /**
     * 查询代销设置
     *
     * @param shopId 店铺id
     * @return 代销设置
     */
    ConsignmentSetting config(Long shopId);

    /**
     * 批量一键铺货
     *
     * @param paveGoods 商品信息
     * @param shopId 店铺id
     */
    void paveGoods(PaveGoodsDTO paveGoods, Long shopId);

    /**
     * 已铺货代销商品
     *
     * @param purchaseProductParam 查询参数
     * @return    IPage<已铺货的代销商品>
     */
    IPage<SupplierIssueProductListVO> getPaveGoods(PurchaseProductParam purchaseProductParam);

    /**
     * 已铺货的代销商品上架
     * @param productId 商品id
     */
    void consignmentProductUpdateStatus(Long productId);

    /**
     * 单个一键铺货
     *
     * @param singlePaveGoods 单个一键铺货参数
     */
    void singlePaveGoods(SinglePaveGoodsDTO singlePaveGoods);
}
