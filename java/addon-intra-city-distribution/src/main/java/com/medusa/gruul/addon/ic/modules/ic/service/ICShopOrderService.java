package com.medusa.gruul.addon.ic.modules.ic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICErrorHandleDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopOrderPageDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.CourierVO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.DeliverPriceVO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.DeliveryInfoVO;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.OrderCallbackParam;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.model.ic.ICStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/27
 */
public interface ICShopOrderService {

    /**
     * uu跑腿 回调
     *
     * @param param 回调参数
     */
    void uuptCallback(OrderCallbackParam param);

    /**
     * 分页查询 派送单
     *
     * @param secureUser 当前登录的用户信息
     * @param param      分页查询参数
     * @return 分页查询结果
     */
    IPage<ICShopOrder> page(SecureUser<?> secureUser, ICShopOrderPageDTO param);

    /**
     * 店员接单
     *
     * @param secureUser 当前登录的用户信息
     * @param orderNo    订单号
     */
    void takeOrder(SecureUser<?> secureUser, String orderNo);

    /**
     * 店员取消接单
     *
     * @param secureUser 当前登录的用户信息
     * @param orderNo    订单号
     */
    void offerOrder(SecureUser<?> secureUser, String orderNo);

    /**
     * 订单更新为下个状态
     *
     * @param orderNo 订单号
     */
    void orderStatusNext(SecureUser<?> secureUser, String orderNo);

    /**
     * 获取指定店铺的同城配送类型
     *
     * @param shopId 店铺 id
     * @return 同城配送类型 列表
     */
    List<DeliverType> deliverType(Long shopId);

    /**
     * 批量获取订单运费价格 当选择UU 跑腿作为配送方时可用
     *
     * @param shopId   店铺 id
     * @param orderNos 订单号集合
     * @return 订单运费计算结果
     */
    DeliverPriceVO deliverPrice(Long shopId, Set<String> orderNos);

    /**
     * 获取指定订单的物流配送信息
     *
     * @param isUser  是否时消费者查询
     * @param shopId  店铺 id
     * @param orderNo 订单号
     * @return 订单的物流配送历史
     */
    DeliveryInfoVO deliverInfo(boolean isUser, Long shopId, String orderNo);

    /**
     * 获取UU跑腿配送员最新定位
     *
     * @param shopId  店铺 id
     * @param orderNo 订单号
     * @return 配送员信息与定位
     */
    CourierVO courier(Long shopId, String orderNo);

    /**
     * 批量查询同城订单配送状态
     *
     * @param orderNos              订单号
     * @param handledErrorToPending 已处理的错误标记为正常
     * @return key 订单号，value 当前状态
     */
    Map<String, ICStatus> icOrderStatus(Set<String> orderNos, boolean handledErrorToPending);

    /**
     * 同城配送异常处理
     *
     * @param shopId 店铺 id
     * @param param  处理结果
     */
    void errorHandle(Long shopId, ICErrorHandleDTO param);


}
