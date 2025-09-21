package com.medusa.gruul.addon.rebate.model.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.rebate.model.enums.RebateUsers;
import com.medusa.gruul.addon.rebate.mp.entity.RebateUsersExtendValue;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.user.api.enums.MemberType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class RebateConfDTO implements BaseDTO {


    /**
     * id
     */
    private Long id;

    /**
     * 消费返利状态
     */
    @NotNull
    private Boolean rebateStatus;

    /**
     * 返利用户
     */
    @NotNull
    private RebateUsers rebateUsers;


    /**
     * 付费返利用户json
     */

    @Valid
    private List<RebateUsersExtendValue> payRebateUsers;


    /**
     * 所有返利用户json
     */
    @Valid
    private List<RebateUsersExtendValue> allRebateUsers;

    @Override
    public void validParam() {
        //检查并获取当前会员配置信息
        List<RebateUsersExtendValue> rebateUsersExtendValues = currentRebateUsers();
        //根据会员类型分组
        Map<MemberType, List<RebateUsersExtendValue>> memberTypeListMap = rebateUsersExtendValues.stream()
                .collect(Collectors.groupingBy(RebateUsersExtendValue::getMemberType));
        List<RebateUsersExtendValue> paidUsers = new ArrayList<>();
        List<RebateUsersExtendValue> freeUsers = new ArrayList<>();
        memberTypeListMap.forEach(
                (key, users) -> {

                    //检查 rank code 是否有重复配置
                    if (users.size() != users.stream().map(RebateUsersExtendValue::getRankCode).collect(Collectors.toSet()).size()) {
                        throw new GlobalException("相同会员等级不能重复配置");
                    }
                    //按照 rank code 从小到大排序
                    List<RebateUsersExtendValue> sortedUsers = users.stream().sorted(Comparator.comparingInt(RebateUsersExtendValue::getRankCode)).toList();
                    //检查 返利百分比与返利支付百分比是否 配置正确
                    this.sortedCheck(sortedUsers);
                    (key == MemberType.FREE_MEMBER ? freeUsers : paidUsers).addAll(sortedUsers);

                }
        );
        if (getRebateUsers() == RebateUsers.PAID_MEMBER) {
            this.setPayRebateUsers(paidUsers);
            this.setAllRebateUsers(List.of());
        } else {
            freeUsers.addAll(paidUsers);
            this.setPayRebateUsers(List.of());
            this.setAllRebateUsers(freeUsers);
        }
    }

    /**
     * 检查排序  返利百分比  返利支付百分比  提现门槛是否正确
     * 1.返利百分比需从小到大排序
     * 2.返利支付百分比需从小到大排序
     * 3.提现门槛需从小到大排序
     *
     * @param users 排序后的数据
     */
    private void sortedCheck(List<RebateUsersExtendValue> users) {
        for (int i = 1; i < users.size(); i++) {
            RebateUsersExtendValue current = users.get(i);
            RebateUsersExtendValue pre = users.get(i - 1);

            String currentName = current.getMemberName();
            String preName = pre.getMemberName();
            // 如果上一条的返利百分比大于当前数据的返利百分比 则提示错误
            if (pre.getRebatePercentage() > current.getRebatePercentage()) {
                throw new GlobalException(
                        StrUtil.format(
                                "'{}'的返利百分比需大于等于'{}'", currentName, preName
                        )
                );
            }
            // 如果上一条的返利支付百分比大于当前数据的返利支付百分比 则提示错误
            if (pre.getRebatePaymentPercentage() > current.getRebatePaymentPercentage()) {
                throw new GlobalException(
                        StrUtil.format(
                                "'{}'的返利支付百分比需大于等于'{}'", currentName, preName
                        )
                );
            }
            // 如果当前数据的提现门槛大于上一条的提现门槛 则提示错误
            if (current.getWithdrawalThreshold() > pre.getWithdrawalThreshold()) {
                throw new GlobalException(
                        StrUtil.format(
                                "'{}'的提现门槛需小于等于'{}'", currentName, preName
                        )
                );
            }
        }
    }


    /**
     * 获取当前会员配置信息
     *
     * @return 会员配置信息
     */
    private List<RebateUsersExtendValue> currentRebateUsers() {
        return switch (getRebateUsers()) {
            case ALL_MEMBERS -> {
                List<RebateUsersExtendValue> allRebateUsers = getAllRebateUsers();
                if (CollUtil.isEmpty(allRebateUsers)) {
                    throw new GlobalException("请设置所有会员消费返利相关百分比");
                }
                yield allRebateUsers;
            }
            case PAID_MEMBER -> {
                List<RebateUsersExtendValue> payRebateUsers = getPayRebateUsers();
                if (CollUtil.isEmpty(payRebateUsers)) {
                    throw new GlobalException("请配置付费会员消费返利相关百分比");
                }
                if (payRebateUsers.stream().anyMatch(user -> MemberType.PAID_MEMBER != user.getMemberType())) {
                    throw new GlobalException("只能配置付费会员");
                }
                yield payRebateUsers;
            }
        };
    }

}
