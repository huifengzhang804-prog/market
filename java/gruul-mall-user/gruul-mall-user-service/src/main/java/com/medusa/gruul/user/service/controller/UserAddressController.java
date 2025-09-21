package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.api.model.UserAddressVO;
import com.medusa.gruul.user.service.model.dto.UserAddressDTO;
import com.medusa.gruul.user.service.mp.entity.UserAddress;
import com.medusa.gruul.user.service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 用户收货地址 前端控制器
 *
 * @author 张治保
 * date 2022/3/8
 */
@RestController
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
@RequiredArgsConstructor
@RequestMapping("/user/address")
public class UserAddressController {

    private final AddressService addressService;

    /**
     * 分页查询用户收货地址
     *
     * @param page 分页参数
     */
    @Log("分页查询用户收货地址")
    @GetMapping
    public Result<IPage<UserAddress>> userAddressPage(Page<UserAddress> page) {
        return Result.ok(
                this.addressService.userAddressPage(page)
        );
    }

    /**
     * 根据收货地址ID获取地址详情
     *
     * @param addressId 收货地址id
     */
    @Log("根据收货地址ID获取地址详情")
    @GetMapping("/{addressId}")
    public Result<UserAddressVO> userAddressById(@PathVariable Long addressId) {
        return Result.ok(
                this.addressService.userAddressById(addressId)
        );
    }

    /**
     * 获取默认收货地址
     */
    @Log("获取默认收货地址")
    @GetMapping("/default")
    public Result<UserAddress> userAddressDefault() {
        return Result.ok(
                this.addressService.userAddressDefault()
        );
    }

    /**
     * 新增用户收货地址
     *
     * @param userAddress 收货地址DTO
     */
    @Log("新增用户收货地址")
    @Idem
    @PostMapping
    public Result<Void> newUserAddress(@RequestBody @Valid UserAddressDTO userAddress) {
        this.addressService.newUserAddress(ISecurity.userOpt().get().getId(), userAddress);
        return Result.ok();
    }

    /**
     * 根据id编辑收货地址
     *
     * @param addressId   收货地址id
     * @param userAddress 收获地址
     */
    @Log("根据id编辑收货地址")
    @Idem
    @PutMapping("/{addressId}")
    public Result<Void> editUserAddress(@PathVariable Long addressId, @RequestBody @Valid UserAddressDTO userAddress) {
        this.addressService.editUserAddress(ISecurity.userOpt().get().getId(), addressId, userAddress);
        return Result.ok();
    }

    /**
     * 根据id编辑收货地址
     *
     * @param addressId 收货地址id
     */
    @Log("根据id删除用户地址")
    @Idem
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteUserAddress(@PathVariable Long addressId) {
        this.addressService.deleteUserAddress(ISecurity.userOpt().get().getId(), addressId);
        return Result.ok();
    }

    /**
     * 设置/取消默认地址
     *
     * @param addressId 收货地址id
     * @param isDefault 是否是默认
     */
    @Log("设置/取消默认地址")
    @Idem(expire = 1500)
    @PutMapping("/{addressId}/{isDefault}")
    public Result<Void> updateUserAddressDefaultStatus(@PathVariable Long addressId, @PathVariable Boolean isDefault) {
        this.addressService.updateUserAddressDefaultStatus(ISecurity.userOpt().get().getId(), addressId, isDefault);
        return Result.ok();
    }


}
