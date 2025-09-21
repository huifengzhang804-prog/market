package com.medusa.gruul.shop.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.shop.api.entity.ShopVisitor;
import com.medusa.gruul.shop.service.mp.mapper.ShopVisitorMapper;
import com.medusa.gruul.shop.service.mp.service.IShopVisitorService;
import org.springframework.stereotype.Service;

/**
 * 店铺访客实现层
 *
 * @author xiaoq
 * @Description ShopVisitorServiceImpl.java
 * @date 2022-10-25 17:17
 */
@Service
public class ShopVisitorServiceImpl extends ServiceImpl<ShopVisitorMapper, ShopVisitor> implements IShopVisitorService {
}
