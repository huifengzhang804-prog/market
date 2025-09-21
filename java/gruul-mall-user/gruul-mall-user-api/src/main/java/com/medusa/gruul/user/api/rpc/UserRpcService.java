package com.medusa.gruul.user.api.rpc;

import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.model.dto.UserDataDTO;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户rpc
 *
 * @author xiaoq
 * @Description UserRpcService.java
 * @date 2022-10-11 14:51
 */
public interface UserRpcService {

    /**
     * 更新用户资料
     */
    void updateUser(UserDataDTO userData);

    /**
     * 查询用户资料
     *
     * @param userId 用户 id
     * @return 用户资料
     */
    UserDataVO userData(Long userId);

    /**
     * 用户进行余额支付扣除余额
     *
     * @param balanceChangeMessage 余额变更消息
     * @return 是否成功
     */
    Boolean userBalancePayment(DataChangeMessage balanceChangeMessage);

    /**
     * 获取用户消费总额
     *
     * @param userId 用户id
     * @return 用户消费总额
     */
    Long getUserConsumption(Long userId);


    /**
     * 批量获取用户消费总额
     *
     * @param userIds 用户id集合
     * @return Map<用户id, 消费总额>
     */
    Map<Long, Long> getUserConsumptionBatch(Set<Long> userIds);

    /**
     * 查看店铺的商品收藏量
     *
     * @param shopId 店铺id
     * @return 商品和收藏量Map
     */
    List<Long> getShopProductCollection(Long shopId);

    /**
     * 获取付费会员关联权益信息
     *
     * @param map map <付费会员id,Set<Long> 关联权益id>
     * @return Map<付费会员id, List < 关联权益信息>>
     */
    Map<Long, List<RelevancyRightsVO>> getPaidMemberRelevancyInfo(Map<Long, Set<Long>> map);


    /**
     * 获取最高等级会员卡数据
     *
     * @param userId 用户id
     * @return 会员聚合信息VO
     */
    MemberAggregationInfoVO getTopMemberCardInfo(Long userId);


    /**
     * 获取用户总积分值
     *
     * @param userId 用户id
     * @return 用户积分值
     */
    Long getUserIntegralTotal(Long userId);


    /**
     * 修改用户积分
     *
     * @param userId   用户id
     * @param integral 增加或减少的积分
     * @param isReduce 是否为减少积分
     * @return 是否成功
     */
    boolean incrementUserIntegralTotal(Long userId, Long integral, boolean isReduce);


    /**
     * 获取免费会员等级code
     *
     * @return 会员等级
     */
    Integer getFreeMemberRankCode();

    /**
     * 获取所有会员等级
     *
     * @return 会员等级
     */
    Map<MemberType, Map<Integer, String>> getAllMemberRankCode();

    /**
     * 获取店铺浏览量
     *
     * @param shopIds 店铺id数组
     * @return 店铺id，浏览量
     */
    Map<Long, Long> getFootMarkCount(Set<Long> shopIds);

    /**
     * 查询用户手机号码
     *
     * @param userIds 用户ids
     * @return map<userid, 手机号>
     */
    Map<Long, String> queryUserPhone(Set<Long> userIds);

    /**
     * 当前用户是否关注店铺商品
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @param userId    用户id
     * @return 是否关注：true-关注
     */
    Boolean findUserIsCollect(Long shopId, Long productId, Long userId);

    /**
     * 判断用户是否收藏 并添加用户足迹
     *
     * @param userId   用户 id
     * @param footMark 足迹信息
     * @return 用户是否收藏了该商品
     */
    boolean isUserCollectAndAddFootMark(Long userId, UserFootMarkDTO footMark);

    /**
     * 获取用户权限Map，包含每个用户的权限信息
     *
     * @param userIds   用户ID集合，需要检查这些用户的权限
     * @param rightsType 权限类型，指定需要检查的权限类型
     * @return Map，其中键是用户ID，值是一个布尔值，表示该用户是否具有指定的权限
     */
    Map<Long, Boolean> getUserRights(Set<Long> userIds, RightsType rightsType);

    /**
     * 查询指定等级会员是否在使用中
     * @param memberId
     * @return true 使用中  false 未使用中
     */
    boolean queryMemberInUse(Long memberId);

    /**
     * 查询用户基本信息
     * @param userIds
     * @return
     */
    Map<Long,UserDataVO> queryUserBaseInfo(Set<Long> userIds);
}
