package com.medusa.gruul.addon.freight.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsBaseModel;
import com.medusa.gruul.addon.freight.mp.mapper.LogisticsBaseModelMapper;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsBaseModelService;
import org.springframework.stereotype.Service;

/**
 * @Description: 物流基础运费模板服务类
 * @Author: xiaoq
 * @Date : 2022-05-03 12:03
 */
@Service
public class LogisticsBaseModelServiceImpl extends ServiceImpl<LogisticsBaseModelMapper, LogisticsBaseModel> implements ILogisticsBaseModelService {
}
