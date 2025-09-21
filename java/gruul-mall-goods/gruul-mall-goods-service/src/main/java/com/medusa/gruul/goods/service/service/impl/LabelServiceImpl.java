package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.Tenant;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.helper.Security;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.service.model.dto.ProductLabelDTO;
import com.medusa.gruul.goods.service.mp.service.IProductLabelService;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.service.LabelService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 商品标签实现类
 *
 * @author wufuzhong
 * @date 2023-12-02 10:35:00
 */
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final IProductLabelService productLabelService;
    private final IProductService productService;
    private final Executor globalExecutor;
    private final RabbitTemplate rabbitTemplate;

    private final ShopRpcService shopRpcService;

    /**
     * 分页查询商品标签
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    @Override
    public IPage<ProductLabel> pageProductLabel(Page<Void> page) {
        return productLabelService.lambdaQuery()
                .page(new Page<>(page.getCurrent(), page.getSize()));
    }

    /**
     * 新增商品标签
     *
     * @param dto 商品标签dto
     */
    @Override
    public void newProductLabel(ProductLabelDTO dto) {
        //校验名称是否存在
        boolean exists = productLabelService.lambdaQuery().eq(ProductLabel::getName, dto.getName()).exists();
        GoodsError.LABEL_NAME_EXISTS.trueThrow(exists);
        ProductLabel label = dto.coverProductLabel();
        productLabelService.save(label);
    }

    /**
     * 编辑商品标签
     *
     * @param id  商品标签id
     * @param dto 商品标签dto
     */
    @Override
    public void editProductLabel(Long id, ProductLabelDTO dto) {
        //校验名称是否存在
        boolean exists = productLabelService.lambdaQuery()
                .eq(ProductLabel::getName, dto.getName())
                .ne(ProductLabel::getId, id)
                .exists();
        GoodsError.LABEL_NAME_EXISTS.trueThrow(exists);
        ProductLabel label = dto.coverProductLabel();
        label.setId(id);
        productLabelService.updateById(label);
    }

    /**
     * 删除商品标签
     *
     * @param id 商品标签id
     */
    @Override
    public void deleteProductLabel(Long id) {
        //逻辑删除商品标签
        boolean update = productLabelService.lambdaUpdate()
                .eq(ProductLabel::getId, id)
                .set(ProductLabel::getDeleted, CommonPool.NUMBER_ONE)
                .update();
        //更新商品中的商品标签
        if (update) {
            List<Product> list = TenantShop.disable(() -> productService.lambdaQuery().select(Product::getId).eq(Product::getLabelId, id).list());
            //更新ES商品的商品标签
            if (CollUtil.isNotEmpty(list)) {
                globalExecutor.execute(
                        () -> rabbitTemplate.convertAndSend(
                                GoodsRabbit.GOODS_LABEL_DELETE.exchange(),
                                GoodsRabbit.GOODS_LABEL_DELETE.routingKey(),
                                list)
                );
            }
            productService.lambdaUpdate().eq(Product::getLabelId, id).set(Product::getLabelId, null);
        }
    }

    /**
     * 根据店铺类型，查询商品标签
     *
     * @param shopType 店铺类型
     * @return 商品标签
     */
    @Override
    public List<ProductLabel> searchProductLabelByShopType(ShopType shopType) {
        return TenantShop.disable(() -> productLabelService.lambdaQuery()
                .like(ProductLabel::getShopType, shopType).list());
    }

    @Override
    public List<ProductLabel> listLabels() {
        List<ProductLabel> productLabels = TenantShop.disable(
            () -> productLabelService.lambdaQuery().list());
        if (CollUtil.isEmpty(productLabels)) {
            return Lists.newArrayList();
        }
        if (ClientType.SHOP_CONSOLE.equals(ISystem.clientTypeOpt().getOrNull())) {
            //店铺端查询标签根据店铺类型 过滤标签
            Long shopId = ISystem.shopIdMust();
            ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
            productLabels=productLabels.stream().filter(productLabel ->
                productLabel.getShopType().contains(shopInfo.getShopType())).toList();
        }
        return productLabels;
    }
}
