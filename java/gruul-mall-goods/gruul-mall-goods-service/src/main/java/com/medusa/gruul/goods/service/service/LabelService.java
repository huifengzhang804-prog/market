package com.medusa.gruul.goods.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.service.model.dto.ProductLabelDTO;

import java.util.List;

/**
 * @author wufuzhong
 */
public interface LabelService {

    /**
     * 分页查询商品标签
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    IPage<ProductLabel> pageProductLabel(Page<Void> page);

    /**
     * 新增商品标签
     *
     * @param dto 商品标签dto
     */
    void newProductLabel(ProductLabelDTO dto);

    /**
     * 编辑商品标签
     *
     * @param id  商品标签id
     * @param dto 商品标签dto
     */
    void editProductLabel(Long id, ProductLabelDTO dto);

    /**
     * 删除商品标签
     *
     * @param id 商品标签id
     */
    void deleteProductLabel(Long id);

    /**
     * 根据店铺类型，查询商品标签
     *
     * @param shopType 店铺类型
     * @return 商品标签
     */
    List<ProductLabel> searchProductLabelByShopType(ShopType shopType);

    /**
     * 查询商品所有标签
     * @return
     */
    List<ProductLabel> listLabels();
}
