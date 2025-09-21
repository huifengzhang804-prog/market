package com.medusa.gruul.addon.store.controller;

import com.medusa.gruul.addon.store.model.dto.StoreOrderStockUpDTO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreOrderInfoVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreTransactionSummaryVO;
import com.medusa.gruul.addon.store.service.MobileShopStoreOrderService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author xiaoq
 * @Description 店铺门店订单控制层
 * @date 2023-03-16 16:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/store/order")
public class ShopStoreOrderController {

    private final MobileShopStoreOrderService mobileShopStoreOrderService;

    /**
     * 获取门店核销码
     *
     * @param oderNo  订单号
     * @param storeId 门店id
     * @return 核销码数据
     */
    @Log("获取门店订单核销码 By orderNo")
    @GetMapping("get/code/{storeId}/{orderNo}")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<ShopStoreOrderInfoVO> getStoreOrderCodeByOrderNo(@PathVariable(value = "storeId") Long storeId, @PathVariable(value = "orderNo") String oderNo) {
        return Result.ok(mobileShopStoreOrderService.getStoreOrderCodeByOrderNo(storeId, oderNo));
    }


    /**
     * 门店订单进行备货完成操作
     *
     * @return void
     */
    @Idem(1000)
    @Log("门店订单备货完成")
    @PostMapping("stock/up")
    @PreAuthorize("@S.matcher().role(@S.R.SHOP_STORE).match()")
    public Result<Void> storeOrderProceedStockUp(@RequestBody StoreOrderStockUpDTO storeOrderStockUp) {
        mobileShopStoreOrderService.storeOrderProceedStockUp(storeOrderStockUp);
        return Result.ok();
    }


    /**
     * 门店订单核销
     *
     * @return Void
     */
    @Idem(1000)
    @Log("门店订单核销")
    @PutMapping("verification/{storeId}/{code}")
    @PreAuthorize("@S.matcher().role(@S.R.SHOP_STORE).match()")
    public Result<Void> storeOrderVerification(@PathVariable(name = "storeId") Long storeId, @PathVariable(name = "code") String code) {
        mobileShopStoreOrderService.storeOrderVerification(storeId, code);
        return Result.ok();
    }

    /**
     * 获取门店交易汇总信息
     *
     * @param storeId 门店id
     * @return 交易汇总信息
     */
    @Log("门店交易汇总")
    @GetMapping("transaction/summary/{storeId}")
    @PreAuthorize("@S.matcher().role(@S.R.SHOP_STORE).match()")
    public Result<ShopStoreTransactionSummaryVO> getStoreTransactionSummary(@PathVariable(name = "storeId") Long storeId) {
        return Result.ok(
                mobileShopStoreOrderService.getStoreTransactionSummary(storeId)
        );
    }


}
