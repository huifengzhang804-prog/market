package com.medusa.gruul.addon.bargain.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.bargain.model.dto.BargainOrderQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainOrderVO;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.order.api.pojo.OrderInfo;

/**
 * 砍价订单 服务类
 *
 * @author WuDi
 * @since 2023-03-14
 */
public interface IBargainOrderService extends IService<BargainOrder> {

    /**
     * 分页查询砍价订单
     *
     * @param bargainOrderQuery 砍价订单查询参数
     * @return 砍价订单分页数据
     */
    IPage<BargainOrder> getBargainOrderPage(BargainOrderQueryDTO bargainOrderQuery);

    /**
     * 砍价订单详情
     *
     * @param shopId 店铺id
     * @param id     砍价订单id
     * @return 砍价订单详情
     */
    BargainOrderVO getBargainOrderDetail(Long shopId, Long id);

    /**
     * 更新砍价状态
     *
     * @param bargainOrder 砍价订单
     */
    void updateBargainStatus(BargainOrder bargainOrder);

    /**
     * 砍价订单创建失败
     *
     * @param orderInfo 订单信息
     */
    void bargainOrderCreateFail(OrderInfo orderInfo);
}
