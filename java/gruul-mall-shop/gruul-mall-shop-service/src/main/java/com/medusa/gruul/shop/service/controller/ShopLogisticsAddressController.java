package com.medusa.gruul.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.entity.ShopLogisticsAddress;
import com.medusa.gruul.shop.api.enums.AddressTypeEnum;
import com.medusa.gruul.shop.service.model.dto.ShopLogisticsAddressDTO;
import com.medusa.gruul.shop.service.model.param.ShopLogisticsAddressParam;
import com.medusa.gruul.shop.service.service.ShopLogisticsAddressManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 物流收发货地址管理控制层
 *
 * @author : xiaoq
 * Description : 物流收发货地址管理控制层
 * date : 2022-05-01 12:43
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','freight:logistics'))
                .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','freight:logistics'))
                .match()
        """)
@RequestMapping("shop/logistics/address")
public class ShopLogisticsAddressController {

    private final ShopLogisticsAddressManageService shopLogisticsAddressManageService;

    /**
     * 新增或修改物流地址
     *
     * @return com.medusa.gruul.common.model.resp
     */
    @Idem(1000)
    @PostMapping("set")
    @Log("新增或者修改地址")
    public Result<Void> editAddress(@RequestBody ShopLogisticsAddressDTO logisticsAddress) {
        shopLogisticsAddressManageService.editAddress(logisticsAddress);
        return Result.ok();
    }


    /**
     * 删除物流地址
     *
     * @param id 收发货地址id
     * @return com.medusa.gruul.common.model.resp
     */
    @Log("删除物流地址")
    @DeleteMapping("del/{id}")
    public Result<Void> delAddress(@PathVariable("id") Long id) {
        shopLogisticsAddressManageService.delAddress(id);
        return Result.ok();
    }

    /**
     * 获取物流地址by logisticsAddressParam
     *
     * @param logisticsAddressParam 查询参数
     * @return com.medusa.gruul.common.model.resp.Result.ok(" 分页数据 ")
     */
    @Log("获取物流地址")
    @GetMapping("list")
    @PreAuthorize("""
                    @S.matcher()
                    .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                    .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','order:delivery','freight:logistics'))
                    .match()
            """)
    public Result<IPage<ShopLogisticsAddress>> getAddressListByPage(ShopLogisticsAddressParam logisticsAddressParam) {
        return Result.ok(shopLogisticsAddressManageService.getAddressListByPage(logisticsAddressParam));
    }


    /**
     * 获取物流地址 by id
     *
     * @param id 物流地址id
     * @return LogisticsAddress
     */
    @GetMapping("get")
    @PreAuthorize("permitAll()")
    @Log("获取物流地址")
    public Result<ShopLogisticsAddress> getAddressById(Long id) {
        return Result.ok(shopLogisticsAddressManageService.getAddressById(id));
    }


    /**
     * 设置默认收发货地址
     *
     * @param id   地址id
     * @param type 收发货类型
     * @return com.medusa.gruul.common.model.resp
     */
    @Log("设置收货发货默认地址")
    @PutMapping(value = "/set/def/address/{id}/{type}")
    public Result<Void> setDefAddress(@PathVariable("id") Long id,
                                      @PathVariable("type") AddressTypeEnum type) {
        shopLogisticsAddressManageService.setDefAddress(id, type);
        return Result.ok();
    }


}
