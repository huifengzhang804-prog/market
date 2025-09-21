package com.medusa.gruul.addon.bargain.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.bargain.mp.entity.BargainProduct;
import com.medusa.gruul.addon.bargain.mp.mapper.BargainProductMapper;
import com.medusa.gruul.addon.bargain.mp.service.IBargainProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 砍价商品 服务实现类
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Service
@RequiredArgsConstructor
public class BargainProductServiceImpl extends ServiceImpl<BargainProductMapper, BargainProduct> implements IBargainProductService {

}
