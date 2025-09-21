package com.medusa.gruul.goods.service.service;

import com.medusa.gruul.goods.service.model.param.CopyGoodsParam;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;

/**
 * @author miskw
 * @date 2023/1/30
 * @describe 商品一键复制
 */
public interface CopyGoodsService {
    /**
     * 一键复制
     *
     * @param copyGoodsDto copyGoodsDto
     * @return CopyProductDTO
     */
    CopyProductVO getDetail(CopyGoodsParam copyGoodsDto);
}
