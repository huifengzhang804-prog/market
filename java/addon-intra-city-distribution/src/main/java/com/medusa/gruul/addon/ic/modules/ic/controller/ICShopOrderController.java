package com.medusa.gruul.addon.ic.modules.ic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICErrorHandleDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopOrderPageDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.CourierVO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.DeliverPriceVO;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.DeliveryInfoVO;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopOrderService;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptRequest;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.OrderCallbackParam;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.order.api.enums.DeliverType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 店铺同城订单控制器
 *
 * @author 张治保
 * @since 2024/8/27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ic/shop/order")
@PreAuthorize("@SS.otherRoles(@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN).match()")
public class ICShopOrderController {

    private final ICShopOrderService shopOrderService;
    private final IUuptApiFactory uuptApiFactory;


    /**
     * UU跑腿回调接口
     *
     * @param param uu 跑腿回调参数
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/uupt/callback")
    public void uuptOrderCallback(@RequestBody UuptRequest param) {
        UuptConfig config = uuptApiFactory.config();
        if (config == null) {
            return;
        }
        OrderCallbackParam orderCallbackParam = param.signCheck(config.getAppKey());
        shopOrderService.uuptCallback(orderCallbackParam);

    }

    /**
     * 分页查询商家配送单
     *
     * @param param 分页查询参数
     * @return 商家配送订单
     */
    @PostMapping("/page")
    public Result<IPage<ICShopOrder>> page(@RequestBody @Valid ICShopOrderPageDTO param) {
        return Result.ok(
                shopOrderService.page(ISecurity.userMust(), param)
        );
    }

    /**
     * 店员接单
     *
     * @param orderNo 订单号
     * @return void
     */
    @PostMapping("/take")
    public Result<Void> takeOrder(@BodyParam String orderNo) {
        shopOrderService.takeOrder(ISecurity.userMust(), orderNo);
        return Result.ok();
    }

    /**
     * 店员取消接单
     *
     * @param orderNo 订单号
     * @return void
     */
    @PostMapping("/offer")
    public Result<Void> offerOrder(@BodyParam String orderNo) {
        shopOrderService.offerOrder(ISecurity.userMust(), orderNo);
        return Result.ok();
    }

    /**
     * 订单更新为下个状态
     *
     * @param orderNo 订单号
     * @return void
     */
    @Idem(value = 500)
    @PostMapping("/status/next")
    public Result<Void> orderStatusNext(@BodyParam String orderNo) {
        shopOrderService.orderStatusNext(ISecurity.userMust(), orderNo);
        return Result.ok();
    }

    /**
     * 获取店铺可选择的同城配送方式
     *
     * @return 同城配送方式列表
     */
    @PostMapping("/deliver/type")
    public Result<List<DeliverType>> deliverType() {
        return Result.ok(
                shopOrderService.deliverType(ISecurity.userMust().getShopId())
        );
    }

    /**
     * 批量获取配送单运费价格 当选择UU 跑腿作为配送方时可用
     *
     * @param orderNos 订单号集合
     * @return 订单运费计算结果
     */
    @PostMapping("/deliver/price")
    public Result<DeliverPriceVO> deliverPrice(@RequestBody @Size(min = 1) Set<String> orderNos) {
        return Result.ok(
                shopOrderService.deliverPrice(ISecurity.userMust().getShopId(), orderNos)
        );
    }


    /**
     * 获取指定订单的配送详情
     *
     * @param orderNo 订单号
     * @return 物流详情
     */
    @PostMapping("/deliver/info")
    @PreAuthorize("@SS.platform('order','order:afs').shop('order:delivery','order:sale').otherRoles(@S.USER).match()")
    public Result<DeliveryInfoVO> deliverInfo(@BodyParam(required = false) Long shopId, @BodyParam String orderNo) {
        boolean mustHaveShopId = ISecurity.matcher().anyRole(Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN, Roles.USER).match();
        if (mustHaveShopId && shopId == null) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        return Result.ok(
                shopOrderService.deliverInfo(
                        ISecurity.matcher().role(Roles.USER).match(),
                        mustHaveShopId ? shopId : ISecurity.userMust().getShopId(),
                        orderNo
                )
        );
    }

    /**
     * 获取UU跑腿配送员最新信息和定位
     *
     * @param shopId  店铺 id
     * @param orderNo 订单号
     */
    @PostMapping("/courier/uupt")
    @PreAuthorize("@SS.platform('order','order:afs').shop('order:delivery','order:sale').otherRoles(@S.USER).match()")
    public Result<CourierVO> courier(@BodyParam(required = false) Long shopId, @BodyParam String orderNo) {
        boolean mustHaveShopId = ISecurity.matcher().anyRole(Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN, Roles.USER).match();
        if (mustHaveShopId && shopId == null) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        return Result.ok(
                shopOrderService.courier(
                        mustHaveShopId ? shopId : ISecurity.userMust().getShopId(),
                        orderNo
                )
        );
    }


    /**
     * 同城单异常处理
     *
     * @param param 处理参数数据
     * @return void
     */
    @PostMapping("/error")
    public Result<Void> errorHandle(@RequestBody @Valid ICErrorHandleDTO param) {
        shopOrderService.errorHandle(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }

}
