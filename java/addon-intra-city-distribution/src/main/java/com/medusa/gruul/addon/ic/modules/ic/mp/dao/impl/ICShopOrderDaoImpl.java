package com.medusa.gruul.addon.ic.modules.ic.mp.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopOrderDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.ic.mp.mapper.ICShopOrderMapper;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/8/27
 */
@Service
public class ICShopOrderDaoImpl extends ServiceImpl<ICShopOrderMapper, ICShopOrder> implements ICShopOrderDao {
}
