package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品标签数据层
 * @author wufuzhong
 * @date 2023-12-02 11:20:00
 */
public interface IProductLabelService extends IService<ProductLabel> {

  /**
   * 查询标签列表
   * @param labIds 标签id集合
   * @return 标签对应Map
   */
  Map<Long,ProductLabel> queryLabelList(Set<Long> labIds);
}
