package com.medusa.gruul.user.service.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.service.model.vo.RankedMemberVO;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.mapper.UserAccountMapper;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author WuDi
 * @since 2022-09-13
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {

    @Override
    public UserAccount getUserMemberInfoByUserId(Long userId) {
        return baseMapper.getUserMemberInfoByUserId(userId);
    }

    @Override
    public Map<Long, String> queryUserPhoneInfo(Set<Long> userIds) {
        List<UserAccount> userAccounts = new LambdaQueryChainWrapper<>(UserAccount.class)
                .in(UserAccount::getUserId, userIds).list();
        if (CollectionUtil.isEmpty(userAccounts)) {
            return new HashMap<>(userIds.size());
        }
        Map<Long, String> result = new HashMap<>(userAccounts.size());
        for (UserAccount userAccount : userAccounts) {
            if (Objects.nonNull(userAccount.getUserPhone())) {
                result.put(userAccount.getUserId(), userAccount.getUserPhone());
            }
        }

        return result;
    }

    @Override
    public List<RankedMemberVO> getRankedMember(Set<Long> userIds) {
        return baseMapper.queryRankedMember(userIds);
    }

    @Override
    public Boolean checkFreeMemberInUse(Long needValue) {
        Long count = lambdaQuery().gt(UserAccount::getGrowthValue, needValue).count();
        return count > 0;
    }

    @Override
    public Map<Long, UserDataVO> queryBaseInfo(Set<Long> userIds) {
        List<UserAccount> list = lambdaQuery()
                .select(UserAccount::getUserId, UserAccount::getUserPhone,
                        UserAccount::getUserNickname, UserAccount::getUserHeadPortrait)
                .in(UserAccount::getUserId, userIds)
                .list();
        if (CollectionUtil.isEmpty(list)) {
            return Map.of();
        }
        Map<Long, UserDataVO> result = list.stream()
                .collect(Collectors.toMap(UserAccount::getUserId, v -> new UserDataVO()
                        .setAvatar(v.getUserHeadPortrait())
                        .setNickname(v.getUserNickname())
                        .setMobile(v.getUserPhone())
                        .setUserId(v.getUserId())));
        return result;
    }

}
