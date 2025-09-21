package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.service.model.vo.RankedMemberVO;
import com.medusa.gruul.user.service.mp.entity.UserAccount;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务类
 *
 * @author WuDi
 * @since 2022-09-13
 */
public interface IUserAccountService extends IService<UserAccount> {


    /**
     * 根据用户id查询用户会员资料
     *
     * @param userId 用户id
     * @return 用户账号与会员卡数据
     */
    UserAccount getUserMemberInfoByUserId(Long userId);

    /**
     * 获取用户手机号
     * @param userIds 用户userIds
     * @return map<userId,用户手机号>
     */
    Map<Long,String> queryUserPhoneInfo(Set<Long> userIds);

    List<RankedMemberVO> getRankedMember(Set<Long> userIds);

    /**
     * 查询免费会员等级是否在使用中（即有用户的会员成长值到达该会员等级所需要的成长值）
     * @param needValue
     * @return
     */
    Boolean checkFreeMemberInUse(Long needValue);

    /**
     * 查询用户基本信息
     * @param userIds
     * @return
     */
    Map<Long, UserDataVO> queryBaseInfo(Set<Long> userIds);
}
