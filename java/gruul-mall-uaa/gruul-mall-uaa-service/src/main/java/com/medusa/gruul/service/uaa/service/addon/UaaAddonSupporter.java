package com.medusa.gruul.service.uaa.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.IAddon;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author xiaoq
 */
@AddonSupporter(id = UaaConstant.UAA_SUPPORT_ID)
public interface UaaAddonSupporter extends IAddon {

    /**
     * 获取付费会员的最高等级
     *
     * @param paidRankCode 付费会员级别
     * @return 会员等级
     * <p>
     * 插件实现服务 addon-member {@link com.medusa.gruul.addon.member.addon.impl.AddonPaidMemberProviderImpl#maxPaidMemberRankCode}
     */
    @AddonMethod(returnType = Integer.class)
    List<Integer> getMaxPaidMemberRankCode(Integer paidRankCode);

    /**
     * 查询用户分销码
     *
     * @param userId 用户 id
     * @return 分销码
     * <p>
     * 插件实现服务 addon-member {@link com.medusa.gruul.addon.distribute.addon.impl.DistributeAddonProviderImpl#distributorCode}
     */
    String distributorCode(@NotNull Long userId);
}
