package com.medusa.gruul.goods.service.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.service.mp.mapper.ProductLabelMapper;
import com.medusa.gruul.goods.service.mp.service.IProductLabelService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 商品标签数据实现层
 * @author wufuzhong
 * @date 2023-12-02 11:20:00
 */
@Service
public class ProductLabelServiceImpl extends ServiceImpl<ProductLabelMapper, ProductLabel> implements IProductLabelService {

  @Override
  public Map<Long,ProductLabel> queryLabelList(Set<Long> labIds) {
    List<ProductLabel> labels = TenantShop.disable(()-> getBaseMapper().selectBatchIds(labIds));

    if (CollectionUtil.isEmpty(labels)) {
      return Maps.newHashMap();
    }
    return labels.stream().collect(Collectors.toMap(ProductLabel::getId,x->x));
  }
}
