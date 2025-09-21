package com.medusa.gruul.order.service.mp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.order.api.entity.OrderReceiver;
import com.medusa.gruul.order.service.mp.mapper.OrderReceiverMapper;
import com.medusa.gruul.order.service.mp.service.IOrderReceiverService;
import org.springframework.stereotype.Service;

/**
 * 订单优列表 服务实现类
 *
 * @author 张治保
 * @since 2022-07-19
 */
@Service
public class OrderReceiverServiceImpl extends ServiceImpl<OrderReceiverMapper, OrderReceiver> implements IOrderReceiverService {

    @Override
    public OrderReceiver getCurrentOrderReceiver(String orderNo, String shopOrderNo) {
        boolean empty = StrUtil.isEmpty(shopOrderNo);
        LambdaQueryChainWrapper<OrderReceiver> wrapper = this.lambdaQuery()
                .eq(OrderReceiver::getOrderNo, orderNo)
                .orderByDesc(OrderReceiver::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1);
        if (empty) {
            return wrapper.isNull(OrderReceiver::getShopOrderNo)
                    .one();
        }
        return wrapper.and(
                query -> query.eq(OrderReceiver::getShopOrderNo, shopOrderNo)
                        .or()
                        .isNull(OrderReceiver::getShopOrderNo)
        ).one();
    }
}
