package com.medusa.gruul.addon.team.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.team.model.enums.TeamMode;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2023/3/2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TeamDTO implements BaseDTO {

    /**
     * 活动名称
     */
    @NotBlank
    @Size(max = 10)
    private String name;

    /**
     * 活动开始日期
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    @NotNull
    private LocalDateTime endTime;

    /**
     * 拼团有效时间，单位：分钟，大于等于15
     */
    @NotNull
    @Min(5)
    @Max(4320)
    private Integer effectTimeout;

    /**
     * 拼团模式 [COMMON,STAIRS]
     */
//    @NotNull
    private TeamMode mode;

    /**
     * 参团人数 阶梯团长度为1-3 且递增
     */
    @NotNull
    @Size(min = 1, max = 3)
    private List<Integer> users;

    /**
     * 支付超时时间，单位：分钟，3-360
     */
    @NotNull
    @Min(3)
    @Max(360)
    private Integer payTimeout;

    /**
     * 是否开启模拟成团
     */
    @NotNull
    private Boolean simulate;

    /**
     * 是否开启凑团模式
     */
    @NotNull
    private Boolean huddle;


    /**
     * 叠加优惠
     */
    @NotNull
    private StackableDiscount stackable;

    /**
     * 活动商品
     */
    @NotNull
    @Valid
    @Size(min = 1, max = 5)
    private List<TeamProductDTO> products;

    @Override
    public void validParam() {

        if (startTime.isBefore(LocalDate.now().atStartOfDay())) {
            throw new GlobalException("开始时间不能小于于当前日期");
        }
        if (endTime.isBefore(LocalDateTime.now().plusMinutes(CommonPool.NUMBER_THIRTY))) {
            throw new GlobalException("结束时间要大于当前时间的30分钟");
        }
        if (startTime.isAfter(endTime)) {
            throw new GlobalException("活动开始日期不能大于结束日期");
        }
        this.validUsersAndProductSizes();
    }

    private void validUsersAndProductSizes() {
        List<Integer> currentUsers = getUsers();
        mode = currentUsers.size() == CommonPool.NUMBER_ONE ? TeamMode.COMMON : TeamMode.STAIRS;
        // 阶梯团需要用户数量需要升序排列
        if (!this.whetherSorted(Boolean.TRUE, currentUsers)) {
            throw new GlobalException("参团人数需逐级递增");
        }
        // 验证商品价格
        getProducts().stream()
                .flatMap(product -> product.getSkus().stream())
                .forEach(
                        sku -> {
                            List<Long> currentPrices = sku.getPrices();
                            // 阶梯团需要商品价格需要降序排列
                            if (currentPrices.size() != currentUsers.size()) {
                                throw new GlobalException("当前团购模式商品价格设置不正确");
                            }
                            if (!this.whetherSorted(Boolean.FALSE, currentPrices)) {
                                throw new GlobalException("阶梯团需要商品价格需要降序排列");
                            }
                        }
                );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean whetherSorted(boolean isAsc, List<? extends Comparable> nums) {
        if (CollUtil.isEmpty(nums)) {
            return true;
        }
        for (int i = 0; i < nums.size() - 1; i++) {
            // 升序
            if (isAsc && nums.get(i).compareTo(nums.get(i + 1)) > 0) {
                return false;
            }
            // 降序
            if (!isAsc && nums.get(i).compareTo(nums.get(i + 1)) < 0) {
                return false;
            }
        }
        return true;
    }


}
