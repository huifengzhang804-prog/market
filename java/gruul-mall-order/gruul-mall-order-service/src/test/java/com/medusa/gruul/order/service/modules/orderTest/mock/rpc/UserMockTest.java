package com.medusa.gruul.order.service.modules.orderTest.mock.rpc;

import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.model.dto.UserDataDTO;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.api.model.vo.CurrentMemberVO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/2/20
 */
public class UserMockTest implements UserRpcService {
    @Override
    public void updateUser(UserDataDTO userData) {

    }

    @Override
    public UserDataVO userData(Long userId) {
        return null;
    }

    @Override
    public Boolean userBalancePayment(DataChangeMessage balanceChangeMessage) {
        return null;
    }

    @Override
    public Long getUserConsumption(Long userId) {
        return null;
    }

    @Override
    public Map<Long, Long> getUserConsumptionBatch(Set<Long> userIds) {
        return null;
    }

    @Override
    public List<Long> getShopProductCollection(Long shopId) {
        return null;
    }

    @Override
    public Map<Long, List<RelevancyRightsVO>> getPaidMemberRelevancyInfo(Map<Long, Set<Long>> map) {
        return null;
    }

    @Override
    public MemberAggregationInfoVO getTopMemberCardInfo(Long userId) {
        return new MemberAggregationInfoVO()
                .setUserNickname("张三")
                .setUserHeadPortrait("http://www.baidu.com")
                .setGrowthValue(0L)
                .setMemberType(MemberType.FREE_MEMBER)
                .setCurrentMemberVO(
                        new CurrentMemberVO()
                                .setRankCode(0)
                                .setRelevancyRights(Map.of())
                );
    }

    @Override
    public Long getUserIntegralTotal(Long userId) {
        return null;
    }

    @Override
    public boolean incrementUserIntegralTotal(Long userId, Long integral, boolean isReduce) {
        return false;
    }

    @Override
    public Integer getFreeMemberRankCode() {
        return null;
    }

    @Override
    public Map<MemberType, Map<Integer, String>> getAllMemberRankCode() {
        return null;
    }

    @Override
    public Map<Long, Long> getFootMarkCount(Set<Long> shopIds) {
        return null;
    }

    @Override
    public Map<Long, String> queryUserPhone(Set<Long> userIds) {
        return null;
    }

    @Override
    public Boolean findUserIsCollect(Long shopId, Long productId, Long userId) {
        return null;
    }

    @Override
    public boolean isUserCollectAndAddFootMark(Long userId, UserFootMarkDTO footMark) {
        return false;
    }

    @Override
    public Map<Long, Boolean> getUserRights(Set<Long> userIds, RightsType rightsType) {
        return Map.of();
    }

    @Override
    public boolean queryMemberInUse(Long memberId) {
        return false;
    }

    @Override
    public Map<Long, UserDataVO> queryUserBaseInfo(Set<Long> userIds) {
        return Map.of();
    }



}
