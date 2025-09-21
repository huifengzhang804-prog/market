package com.medusa.gruul.goods.service.service.impl;

import com.medusa.gruul.goods.service.functions.copy.ProductCollectorFactory;
import com.medusa.gruul.goods.service.model.param.CopyGoodsParam;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.service.CopyGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * @author miskw
 * @date 2023/1/30
 * @describe 商品一键复制
 */
@Service
@RequiredArgsConstructor
public class CopyGoodsServiceImpl implements CopyGoodsService {

    private final ProductCollectorFactory productCollector;

    /**
     * 一键复制
     *
     * @param copyGoods 参数
     * @return ProductDTO
     */
    @Override
    public CopyProductVO getDetail(CopyGoodsParam copyGoods) {
        return productCollector.execute(copyGoods.getCopyGoodsType(), copyGoods);
    }
}
