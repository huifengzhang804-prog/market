package com.medusa.gruul.order.service.modules.evaluate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.dto.EvaluateQueryDTO;
import com.medusa.gruul.order.service.model.dto.OrderEvaluateDTO;
import com.medusa.gruul.order.service.model.dto.ProductEvaluateQueryDTO;
import com.medusa.gruul.order.service.model.vo.ProductEvaluateOverviewVO;
import com.medusa.gruul.order.service.modules.evaluate.model.EvaluateDetailKey;

import java.util.List;

/**
 * 评价查询服务
 *
 * @author 张治保
 * date 2022/9/5
 */
public interface EvaluateService {

    /**
     * 订单评价
     *
     * @param isSystem      是否是系统处理消息
     * @param userId        用户id
     * @param orderEvaluate 评价参数
     */
    void evaluate(boolean isSystem, Long userId, OrderEvaluateDTO orderEvaluate);


    /**
     * 分页查询 用户真实评价
     *
     * @param evaluateQuery 查询条件
     * @return 分页查询结果
     */
    IPage<OrderEvaluate> trueEvaluatePage(EvaluateQueryDTO evaluateQuery);

    /**
     * 商品评价概况
     *
     * @param productEvaluateQuery 查询条件
     * @return 商品评价概况
     */
    ProductEvaluateOverviewVO productEvaluateOverview(ProductEvaluateQueryDTO productEvaluateQuery);

    /**
     * 分页查询精选评价或用户自己的评价
     *
     * @param productEvaluateQuery 查询条件
     * @return 分页查询结果
     */
    IPage<OrderEvaluate> productEvaluatePage(ProductEvaluateQueryDTO productEvaluateQuery);

    /**
     * 设置评价 的精选状态
     *
     * @param evaluateIds 评价id
     * @param isExcellent 是否为精选
     */
    void setEvaluateExcellent(List<Long> evaluateIds, Boolean isExcellent);

    /**
     * 回复评论
     *
     * @param evaluateId 评价id
     * @param reply      回复内容
     */
    void replyEvaluate(Long evaluateId, String reply);

    /**
     * 查询订单项评价详情
     *
     * @param orderNo 订单号
     * @param key     sku key
     * @return 评价详情
     */
    OrderEvaluate getOrderEvaluate(String orderNo, EvaluateDetailKey key);
}
