package com.medusa.gruul.addon.ic.modules.ic.mp.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopConfigDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.addon.ic.modules.ic.mp.mapper.ICShopConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/3/2
 */
@Service
@RequiredArgsConstructor
public class ICShopConfigDaoImpl extends ServiceImpl<ICShopConfigMapper, ICShopConfig> implements ICShopConfigDao {


}
